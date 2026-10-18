package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CyberInputBar
import com.example.ui.components.CyberTopBar
import com.example.ui.screens.ChatConversationView
import com.example.ui.screens.ChatHistoryDrawerSheet
import com.example.ui.screens.HomeDashboardView
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ToolsBottomSheet
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatViewModel
import com.example.util.CyberMatrixBackground
import com.example.util.configureAppCheck

enum class AppScreen {
    SPLASH,
    MAIN,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            val chatViewModel: ChatViewModel = viewModel()
            val userSettings by chatViewModel.userSettings.collectAsState()

            MyApplicationTheme(accentColor = userSettings.getAccentColor()) {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(
                                onFinishSplash = { currentScreen = AppScreen.MAIN }
                            )
                        }
                        AppScreen.MAIN -> {
                            MainAppContent(
                                viewModel = chatViewModel,
                                onOpenSettings = { currentScreen = AppScreen.SETTINGS }
                            )
                        }
                        AppScreen.SETTINGS -> {
                            BackHandler {
                                currentScreen = AppScreen.MAIN
                            }
                            SettingsScreen(
                                currentSettings = userSettings,
                                onSaveSettings = { chatViewModel.updateSettings(it) },
                                onClearAllData = { chatViewModel.clearAllData() },
                                onBack = { currentScreen = AppScreen.MAIN }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: ChatViewModel,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val currentMessages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val streamingText by viewModel.streamingAiText.collectAsState()
    val attachedImageUri by viewModel.attachedImageUri.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showToolsSheet by remember { mutableStateOf(false) }
    var initialToolTab by remember { mutableIntStateOf(0) }
    var showHistorySheet by remember { mutableStateOf(false) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.setAttachedImage(uri)
    }

    // Audio Permission Launcher for Voice Recognition
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.voiceManager.startListening { transcript ->
                inputText = transcript
            }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice chat.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CyberTopBar(
                isOnline = isOnline,
                onOpenHistory = { showHistorySheet = true },
                onOpenSettings = onOpenSettings,
                onNewChat = {
                    viewModel.startNewChat()
                }
            )
        },
        bottomBar = {
            CyberInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = {
                    val prompt = inputText
                    inputText = ""
                    viewModel.sendMessage(prompt)
                },
                onOpenTools = {
                    initialToolTab = 0
                    showToolsSheet = true
                },
                onToggleVoice = {
                    if (isListening) {
                        viewModel.voiceManager.stopListening()
                    } else {
                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        )
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            viewModel.voiceManager.startListening { transcript ->
                                inputText = transcript
                            }
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                isListening = isListening,
                attachedImageUri = attachedImageUri,
                onRemoveImage = { viewModel.removeAttachedImage() },
                isGenerating = isGenerating,
                modifier = Modifier.imePadding()
            )
        },
        containerColor = CyberBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background cyber matrix canvas
            CyberMatrixBackground(particleCount = 18)

            // Switch between Dashboard view (if no messages) and Conversation view (if messages present)
            if (currentMessages.isEmpty() && streamingText.isBlank()) {
                HomeDashboardView(
                    userName = userSettings.userName,
                    onPromptSelected = { prompt ->
                        viewModel.sendMessage(prompt)
                    },
                    onOpenTool = { toolIndex ->
                        initialToolTab = toolIndex
                        showToolsSheet = true
                    },
                    onPickImage = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onStartVoice = {
                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        )
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            viewModel.voiceManager.startListening { transcript ->
                                inputText = transcript
                            }
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                )
            } else {
                ChatConversationView(
                    messages = currentMessages,
                    streamingText = streamingText,
                    isGenerating = isGenerating,
                    isOnline = isOnline,
                    isSpeaking = isSpeaking,
                    fontSizeSp = userSettings.fontSizeSp,
                    onRegenerate = { viewModel.regenerateLastResponse() },
                    onRateMessage = { id, liked -> viewModel.rateMessage(id, liked) },
                    onSpeak = { text ->
                        viewModel.voiceManager.speak(
                            text = text,
                            speed = userSettings.ttsSpeed,
                            pitch = userSettings.ttsPitch
                        )
                    },
                    onStopSpeaking = { viewModel.voiceManager.stopSpeaking() },
                    onEditMessage = { oldText ->
                        inputText = oldText
                    },
                    onSuggestionSelected = { prompt ->
                        viewModel.sendMessage(prompt)
                    }
                )
            }
        }
    }

    // Tools Bottom Sheet
    if (showToolsSheet) {
        ToolsBottomSheet(
            onDismiss = { showToolsSheet = false },
            initialTab = initialToolTab,
            onLaunchToolPrompt = { prompt ->
                viewModel.sendMessage(prompt)
            },
            onPickImage = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    // Chat History Drawer / Sheet
    if (showHistorySheet) {
        ChatHistoryDrawerSheet(
            sessions = sessions,
            currentSessionId = currentSessionId,
            onSelectSession = { sessionId ->
                viewModel.selectSession(sessionId)
            },
            onNewChat = {
                viewModel.startNewChat()
            },
            onRenameSession = { sessionId, title ->
                viewModel.renameSession(sessionId, title)
            },
            onDeleteSession = { sessionId ->
                viewModel.deleteSession(sessionId)
            },
            onTogglePin = { sessionId ->
                viewModel.togglePinSession(sessionId)
            },
            onToggleFavorite = { sessionId ->
                viewModel.toggleFavoriteSession(sessionId)
            },
            onDismiss = { showHistorySheet = false }
        )
    }
}
