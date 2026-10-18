package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.ui.components.AiTypingIndicator
import com.example.ui.components.FormattedAiMessage
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberOfflineRed
import com.example.ui.theme.CyberOnlineGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.util.cyberGlass
import com.example.util.performCyberHaptic
import kotlinx.coroutines.launch

@Composable
fun ChatConversationView(
    messages: List<ChatMessageEntity>,
    streamingText: String,
    isGenerating: Boolean,
    isOnline: Boolean,
    isSpeaking: Boolean,
    fontSizeSp: Int,
    onRegenerate: () -> Unit,
    onRateMessage: (Long, Boolean) -> Unit,
    onSpeak: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onEditMessage: (String) -> Unit,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotBlank()) {
            val totalItems = messages.size + if (isGenerating) 1 else 0
            if (totalItems > 0) {
                listState.animateScrollToItem(totalItems - 1)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (messages.isEmpty() && streamingText.isBlank()) {
            // Empty State
            EmptyConversationState(
                onSuggestionSelected = onSuggestionSelected,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Offline banner if disconnected
                if (!isOnline) {
                    item {
                        OfflineWarningBanner(
                            onRetry = onRegenerate,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                items(messages, key = { it.id }) { message ->
                    if (message.sender == "user") {
                        UserMessageItem(
                            message = message,
                            fontSizeSp = fontSizeSp,
                            onEdit = { onEditMessage(message.content) },
                            onCopy = {
                                copyToClipboard(context, message.content)
                            }
                        )
                    } else {
                        AiMessageItem(
                            message = message,
                            fontSizeSp = fontSizeSp,
                            isSpeaking = isSpeaking,
                            onCopy = { copyToClipboard(context, message.content) },
                            onShare = { shareText(context, message.content) },
                            onSpeak = { onSpeak(message.content) },
                            onStopSpeaking = onStopSpeaking,
                            onRegenerate = onRegenerate,
                            onRate = { isLiked -> onRateMessage(message.id, isLiked) }
                        )
                    }
                }

                // Streaming AI response item
                if (isGenerating) {
                    item {
                        StreamingAiMessageItem(
                            streamingText = streamingText,
                            fontSizeSp = fontSizeSp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyConversationState(
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        "“Write a futuristic story”",
        "“Create an app with Kotlin”",
        "“Explain Quantum Computing”",
        "“Generate startup ideas for 2026”",
        "“Translate text to Urdu”"
    )

    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(2.dp, CyberCyan, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mivazo_logo),
                contentDescription = "Mivazo Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "“What can I help you with, Boss?”",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Ask questions, write scripts, generate code, translate or analyze files.",
            color = CyberCyan.copy(alpha = 0.8f),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (suggestion in suggestions) {
                val cleanText = suggestion.replace("“", "").replace("”", "")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCyan.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .clickable { onSuggestionSelected("$cleanText, Boss.") }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = suggestion,
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserMessageItem(
    message: ChatMessageEntity,
    fontSizeSp: Int,
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            // Attached image if present
            if (message.imageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .size(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = message.imageUri,
                        contentDescription = "User uploaded image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // User Message Bubble
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CyberCyan.copy(alpha = 0.25f),
                                CyberPurple.copy(alpha = 0.35f),
                                Color(0xFF131D38)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        CyberCyan.copy(alpha = 0.5f),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = Color.White,
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp + 6).sp
                )
            }

            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 3.dp)
            ) {
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onEdit()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit message",
                        tint = CyberTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }

                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onCopy()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = CyberTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiMessageItem(
    message: ChatMessageEntity,
    fontSizeSp: Int,
    isSpeaking: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit,
    onRegenerate: () -> Unit,
    onRate: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI Avatar
        Box(
            modifier = Modifier
                .padding(top = 4.dp, end = 8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .border(1.dp, CyberCyan, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mivazo_logo),
                contentDescription = "Mivazo AI",
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(modifier = Modifier.widthIn(max = 320.dp)) {
            // AI Message Bubble
            Box(
                modifier = Modifier
                    .cyberGlass(
                        cornerRadius = 16.dp,
                        borderColor = CyberCyan.copy(alpha = 0.25f),
                        backgroundColor = CyberCardBg
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                FormattedAiMessage(text = message.content, fontSizeSp = fontSizeSp)
            }

            // AI Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Copy
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onCopy()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberTextSecondary, modifier = Modifier.size(14.dp))
                }

                // Read Aloud / Stop
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        if (isSpeaking) onStopSpeaking() else onSpeak()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Voice read aloud",
                        tint = if (isSpeaking) CyberOfflineRed else CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Share
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onShare()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberTextSecondary, modifier = Modifier.size(14.dp))
                }

                // Regenerate
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onRegenerate()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = CyberTextSecondary, modifier = Modifier.size(15.dp))
                }

                // Like
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onRate(true)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = if (message.isLiked == true) CyberOnlineGreen else CyberTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Dislike
                IconButton(
                    onClick = {
                        performCyberHaptic(context)
                        onRate(false)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.ThumbDown,
                        contentDescription = "Dislike",
                        tint = if (message.isLiked == false) CyberOfflineRed else CyberTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StreamingAiMessageItem(
    streamingText: String,
    fontSizeSp: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp, end = 8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .border(1.dp, CyberCyan, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mivazo_logo),
                contentDescription = "Mivazo AI",
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(modifier = Modifier.widthIn(max = 320.dp)) {
            if (streamingText.isBlank()) {
                AiTypingIndicator()
            } else {
                Box(
                    modifier = Modifier
                        .cyberGlass(
                            cornerRadius = 16.dp,
                            borderColor = CyberCyan.copy(alpha = 0.5f),
                            backgroundColor = CyberCardBg
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Column {
                        FormattedAiMessage(text = streamingText, fontSizeSp = fontSizeSp)
                        Spacer(modifier = Modifier.height(6.dp))
                        AiTypingIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineWarningBanner(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberOfflineRed.copy(alpha = 0.15f))
            .border(1.dp, CyberOfflineRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = CyberOfflineRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Internet connection is unavailable. Please check your connection and try again.",
                color = CyberTextPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberOfflineRed,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(34.dp)
        ) {
            Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Mivazo Message", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Mivazo AI Response")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Mivazo AI Response"))
}
