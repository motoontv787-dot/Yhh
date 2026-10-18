package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.preferences.UserSettings
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "MivazoAiService"

data class ChatHistoryItem(
    val sender: String, // "user" or "model"
    val content: String,
    val bitmap: Bitmap? = null
)

class GeminiAiService {

    private fun getSystemInstruction(settings: UserSettings): String {
        val styleInstruction = when (settings.responseStyle) {
            "Short" -> "Keep your responses concise, direct, and under 3-4 sentences whenever possible."
            "Detailed" -> "Provide comprehensive, deeply analyzed, and highly thorough explanations."
            "Professional" -> "Maintain a formal, structured, high-level executive and analytical tone."
            "Friendly" -> "Be energetic, conversational, supportive, and warm."
            "Creative" -> "Use vivid imagination, engaging metaphors, and creative cyberpunk flair."
            else -> "Provide a balanced, structured, and clear response."
        }

        return """
            You are Mivazo AI, a futuristic, world-class AI assistant created specifically for MOHSIN BOSS.
            Primary Identity & Protocol:
            - Developed for: MOHSIN BOSS (your creator and leader).
            - Default Persona: Highly intelligent, fast, respectful, modern, and futuristic with a sleek cyberpunk edge.
            - Calling the User: When appropriate and natural, you can address the user as "Boss" (e.g., "Sure Boss, let me handle that.", "Right away, Boss.", "Here is the solution, Boss:"). Do not spam "Boss" in every sentence—use it naturally and respectfully.
            - Preferred language: ${settings.preferredLanguage}.
            - Response style: $styleInstruction.
            - Expertise: Programming, code debugging, writing, language translation, mathematical reasoning, technology, science, idea generation, and daily assistance.
            - Formatting: Use clean Markdown with bold headings, bullet points, numbered lists, and full markdown code blocks (e.g. ```kotlin ... ```) with syntax naming.
        """.trimIndent()
    }

    suspend fun streamChat(
        history: List<ChatHistoryItem>,
        newPrompt: String,
        imageBitmap: Bitmap? = null,
        settings: UserSettings
    ): Flow<String> = flow {
        val modelName = if (settings.aiModel.isNotBlank()) settings.aiModel else "gemini-3.8-flash"
        val systemInstruction = getSystemInstruction(settings)

        try {
            val generativeModel = Firebase.ai.generativeModel(
                modelName = modelName,
                systemInstruction = content { text(systemInstruction) }
            )

            // Convert conversation history into Firebase AI Content items
            val historyContentList = mutableListOf<Content>()
            val recentHistory = history.takeLast(12)
            for (item in recentHistory) {
                if (item.sender == "user") {
                    historyContentList.add(content("user") {
                        if (item.bitmap != null) {
                            image(item.bitmap)
                        }
                        text(item.content)
                    })
                } else {
                    historyContentList.add(content("model") {
                        text(item.content)
                    })
                }
            }

            val chatSession = generativeModel.startChat(historyContentList)

            withTimeout(60_000L) {
                val responseStream = if (imageBitmap != null) {
                    val promptContent = content {
                        image(imageBitmap)
                        text(newPrompt)
                    }
                    chatSession.sendMessageStream(promptContent)
                } else {
                    chatSession.sendMessageStream(newPrompt)
                }

                responseStream.collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        emit(chunkText)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI generation timed out", e)
            emit("\n\n⚠️ *Generation timed out after 60 seconds, Boss. Please try again.*")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI Service call exception", e)
            val errorMsg = e.localizedMessage.orEmpty()

            val isOffline = e is UnknownHostException ||
                    e is ConnectException ||
                    e is SocketTimeoutException ||
                    errorMsg.contains("UnknownHost", ignoreCase = true) ||
                    errorMsg.contains("Unable to resolve host", ignoreCase = true) ||
                    errorMsg.contains("No address associated with hostname", ignoreCase = true) ||
                    errorMsg.contains("ENETUNREACH", ignoreCase = true)

            if (isOffline) {
                emit("Internet connection is unavailable. Please check your connection and try again.")
            } else if (errorMsg.contains("App Check", ignoreCase = true)) {
                Log.w(TAG, "App Check attestation exception caught: $errorMsg")
                emit("Unable to verify App Check attestation token. Please ensure your device has internet access and try again, Boss.")
            } else {
                emit("⚠️ Notice: ${errorMsg.ifBlank { "An error occurred while generating response." }}\n*Please try again, Boss.*")
            }
        }
    }.flowOn(Dispatchers.IO)
}
