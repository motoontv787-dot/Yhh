package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ChatHistoryItem
import com.example.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.preferences.UserPreferences
import com.example.data.preferences.UserSettings
import com.example.util.NetworkMonitor
import com.example.util.VoiceManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ChatViewModel"

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val userPreferences = UserPreferences(application)
    private val networkMonitor = NetworkMonitor(application)
    private val aiService = GeminiAiService()
    val voiceManager = VoiceManager(application)

    val userSettings: StateFlow<UserSettings> = userPreferences.settings

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        networkMonitor.checkCurrentNetwork()
    )

    val sessions: StateFlow<List<ChatSessionEntity>> = chatDao.getAllSessions().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _streamingAiText = MutableStateFlow("")
    val streamingAiText: StateFlow<String> = _streamingAiText.asStateFlow()

    private val _attachedImageUri = MutableStateFlow<String?>(null)
    val attachedImageUri: StateFlow<String?> = _attachedImageUri.asStateFlow()

    private var attachedBitmap: Bitmap? = null

    private var activeGenerationJob: Job? = null
    private var messagesObserverJob: Job? = null

    init {
        // Observe sessions to select or create default initial session
        viewModelScope.launch {
            sessions.collectLatest { sessionList ->
                if (_currentSessionId.value == null) {
                    if (sessionList.isNotEmpty()) {
                        selectSession(sessionList.first().id)
                    }
                }
            }
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
        observeSessionMessages(sessionId)
    }

    private fun observeSessionMessages(sessionId: Long) {
        messagesObserverJob?.cancel()
        messagesObserverJob = viewModelScope.launch {
            chatDao.getMessagesForSession(sessionId).collectLatest { msgs ->
                _currentMessages.value = msgs
            }
        }
    }

    fun startNewChat() {
        viewModelScope.launch {
            val newSessionId = chatDao.insertSession(
                ChatSessionEntity(
                    title = "New Conversation",
                    modelName = userSettings.value.aiModel
                )
            )
            _currentSessionId.value = newSessionId
            observeSessionMessages(newSessionId)
        }
    }

    fun renameSession(sessionId: Long, newTitle: String) {
        viewModelScope.launch {
            val session = chatDao.getSessionById(sessionId)
            if (session != null) {
                chatDao.updateSession(session.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatDao.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                _currentSessionId.value = null
                _currentMessages.value = emptyList()
            }
        }
    }

    fun togglePinSession(sessionId: Long) {
        viewModelScope.launch {
            val session = chatDao.getSessionById(sessionId)
            if (session != null) {
                chatDao.updateSession(session.copy(isPinned = !session.isPinned))
            }
        }
    }

    fun toggleFavoriteSession(sessionId: Long) {
        viewModelScope.launch {
            val session = chatDao.getSessionById(sessionId)
            if (session != null) {
                chatDao.updateSession(session.copy(isFavorite = !session.isFavorite))
            }
        }
    }

    fun clearCurrentSession() {
        val sId = _currentSessionId.value ?: return
        viewModelScope.launch {
            chatDao.deleteMessagesForSession(sId)
            _currentMessages.value = emptyList()
        }
    }

    fun setAttachedImage(uri: Uri?) {
        if (uri == null) {
            _attachedImageUri.value = null
            attachedBitmap = null
            return
        }

        _attachedImageUri.value = uri.toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val fullBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Downscale to max dimension 1024 to optimize latency & memory
                if (fullBitmap != null) {
                    val maxDim = 1024
                    val width = fullBitmap.width
                    val height = fullBitmap.height
                    if (width > maxDim || height > maxDim) {
                        val ratio = maxDim.toFloat() / maxOf(width, height)
                        attachedBitmap = Bitmap.createScaledBitmap(
                            fullBitmap,
                            (width * ratio).toInt(),
                            (height * ratio).toInt(),
                            true
                        )
                    } else {
                        attachedBitmap = fullBitmap
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading image bitmap", e)
            }
        }
    }

    fun removeAttachedImage() {
        _attachedImageUri.value = null
        attachedBitmap = null
    }

    fun sendMessage(userPrompt: String) {
        val cleanPrompt = userPrompt.trim()
        val currentImgUri = _attachedImageUri.value
        val bitmapToSend = attachedBitmap

        if (cleanPrompt.isBlank() && currentImgUri == null) return

        viewModelScope.launch {
            var sId = _currentSessionId.value
            if (sId == null) {
                val initialTitle = if (cleanPrompt.isNotBlank()) {
                    if (cleanPrompt.length > 28) cleanPrompt.take(28) + "..." else cleanPrompt
                } else "Image Analysis"
                sId = chatDao.insertSession(
                    ChatSessionEntity(
                        title = initialTitle,
                        modelName = userSettings.value.aiModel
                    )
                )
                _currentSessionId.value = sId
                observeSessionMessages(sId)
            }

            // Insert user message
            val userMsg = ChatMessageEntity(
                sessionId = sId,
                sender = "user",
                content = cleanPrompt.ifBlank { "What is in this image, Boss?" },
                timestamp = System.currentTimeMillis(),
                imageUri = currentImgUri
            )
            chatDao.insertMessage(userMsg)

            // Update session title if first message
            val session = chatDao.getSessionById(sId)
            if (session != null && (session.title == "New Conversation" || session.title.isBlank())) {
                val derivedTitle = if (cleanPrompt.isNotBlank()) {
                    if (cleanPrompt.length > 28) cleanPrompt.take(28) + "..." else cleanPrompt
                } else "Image Analysis"
                chatDao.updateSession(session.copy(title = derivedTitle, updatedAt = System.currentTimeMillis()))
            }

            // Clear current input image attachment
            removeAttachedImage()

            // Generate AI response
            executeAiGeneration(sId, cleanPrompt.ifBlank { "Analyze this image and describe everything in detail, Boss." }, bitmapToSend)
        }
    }

    private fun executeAiGeneration(sessionId: Long, prompt: String, imageBitmap: Bitmap?) {
        activeGenerationJob?.cancel()
        activeGenerationJob = viewModelScope.launch {
            _isGenerating.value = true
            _streamingAiText.value = ""

            val history = _currentMessages.value.map {
                ChatHistoryItem(sender = it.sender, content = it.content)
            }

            val accumulatedText = StringBuilder()
            try {
                aiService.streamChat(
                    history = history,
                    newPrompt = prompt,
                    imageBitmap = imageBitmap,
                    settings = userSettings.value
                ).collect { chunk ->
                    accumulatedText.append(chunk)
                    _streamingAiText.value = accumulatedText.toString()
                }

                val finalResponse = accumulatedText.toString().trim()
                if (finalResponse.isNotBlank()) {
                    val aiMessage = ChatMessageEntity(
                        sessionId = sessionId,
                        sender = "model",
                        content = finalResponse,
                        timestamp = System.currentTimeMillis()
                    )
                    chatDao.insertMessage(aiMessage)

                    // Auto read response if enabled
                    if (userSettings.value.autoReadResponses) {
                        voiceManager.speak(
                            text = finalResponse,
                            speed = userSettings.value.ttsSpeed,
                            pitch = userSettings.value.ttsPitch
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Generation execution error", e)
                val errMsg = "⚠️ Error generating response: ${e.localizedMessage ?: "Unknown error"}"
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = sessionId,
                        sender = "model",
                        content = errMsg,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } finally {
                _isGenerating.value = false
                _streamingAiText.value = ""
            }
        }
    }

    fun regenerateLastResponse() {
        val sId = _currentSessionId.value ?: return
        val messages = _currentMessages.value
        if (messages.isEmpty()) return

        val lastModelMsg = messages.lastOrNull { it.sender == "model" }
        val lastUserMsg = messages.lastOrNull { it.sender == "user" } ?: return

        viewModelScope.launch {
            if (lastModelMsg != null) {
                chatDao.deleteMessage(lastModelMsg.id)
            }
            executeAiGeneration(sId, lastUserMsg.content, null)
        }
    }

    fun rateMessage(messageId: Long, isLiked: Boolean) {
        viewModelScope.launch {
            val msg = _currentMessages.value.find { it.id == messageId }
            if (msg != null) {
                chatDao.updateMessage(msg.copy(isLiked = if (msg.isLiked == isLiked) null else isLiked))
            }
        }
    }

    fun updateSettings(newSettings: UserSettings) {
        userPreferences.updateSettings(newSettings)
    }

    fun clearAllData() {
        viewModelScope.launch {
            chatDao.clearAllSessions()
            _currentSessionId.value = null
            _currentMessages.value = emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
