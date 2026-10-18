package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCodeBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberOfflineRed
import com.example.ui.theme.CyberOnlineGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.util.cyberGlass
import com.example.util.performCyberHaptic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CyberTopBar(
    isOnline: Boolean,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface(
        color = CyberBackground.copy(alpha = 0.94f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenHistory() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, CyberCyan, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_mivazo_logo),
                            contentDescription = "Mivazo AI Logo",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MIVAZO AI",
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Glowing Online/Offline Indicator
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) CyberOnlineGreen else CyberOfflineRed)
                            )
                        }

                        Text(
                            text = if (isOnline) "🟢 Mivazo Online • MOHSIN BOSS" else "🔴 Offline System",
                            color = if (isOnline) CyberCyan else CyberOfflineRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            performCyberHaptic(context)
                            onNewChat()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("new_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Chat",
                            tint = CyberCyan
                        )
                    }

                    IconButton(
                        onClick = {
                            performCyberHaptic(context)
                            onOpenHistory()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("chat_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Chat History",
                            tint = CyberTextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            performCyberHaptic(context)
                            onOpenSettings()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = CyberTextSecondary
                        )
                    }
                }
            }

            // Futuristic neon horizontal separator line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyberCyan.copy(alpha = 0.5f),
                                CyberPurple.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun AiTypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_anim")
    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot1"
    )
    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot2"
    )
    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dot3"
    )

    Row(
        modifier = modifier
            .cyberGlass(cornerRadius = 14.dp, backgroundColor = CyberCardBg.copy(alpha = 0.85f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mivazo is processing",
            color = CyberCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberCyan.copy(alpha = dotAlpha1)))
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberCyan.copy(alpha = dotAlpha2)))
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberPurple.copy(alpha = dotAlpha3)))
    }
}

@Composable
fun CyberInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onOpenTools: () -> Unit,
    onToggleVoice: () -> Unit,
    isListening: Boolean,
    attachedImageUri: String?,
    onRemoveImage: () -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "mic_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberBackground.copy(alpha = 0.95f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Thumbnail preview if image attached
        if (attachedImageUri != null) {
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, CyberCyan, RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = attachedImageUri,
                    contentDescription = "Attached preview",
                    modifier = Modifier.fillMaxWidth()
                )
                IconButton(
                    onClick = onRemoveImage,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove attached image",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plus / Tools button
            IconButton(
                onClick = {
                    performCyberHaptic(context)
                    onOpenTools()
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .border(1.dp, CyberCyan.copy(alpha = 0.4f), CircleShape)
                    .testTag("tools_plus_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tools menu",
                    tint = CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Main Text Input Field
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = {
                    Text(
                        text = if (isListening) "Listening to you, Boss..." else "Ask Mivazo anything, Boss...",
                        color = CyberTextMuted,
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberCyan.copy(alpha = 0.25f),
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_textfield")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Voice Mic button
            IconButton(
                onClick = {
                    performCyberHaptic(context)
                    onToggleVoice()
                },
                modifier = Modifier
                    .size(42.dp)
                    .scale(if (isListening) micScale else 1f)
                    .clip(CircleShape)
                    .background(if (isListening) CyberOfflineRed else CyberSurfaceVariant)
                    .border(
                        1.dp,
                        if (isListening) CyberOfflineRed else CyberPurple.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .testTag("mic_voice_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice microphone",
                    tint = if (isListening) Color.White else CyberPurple,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send Button
            val canSend = (inputText.isNotBlank() || attachedImageUri != null) && !isGenerating
            IconButton(
                onClick = {
                    if (canSend) {
                        performCyberHaptic(context)
                        onSend()
                    }
                },
                enabled = canSend,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (canSend) CyberCyan else CyberSurfaceVariant.copy(alpha = 0.5f),
                    contentColor = if (canSend) Color.Black else CyberTextMuted
                ),
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .testTag("send_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = CyberCyan,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CodeBlockCard(
    language: String,
    code: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCodeBackground)
            .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        // Code Block Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F1527))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.ifBlank { "code" }.uppercase(),
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        performCyberHaptic(context)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("code", code)
                        clipboard.setPrimaryClip(clip)
                        isCopied = true
                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            delay(2000)
                            isCopied = false
                        }
                    }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = if (isCopied) CyberOnlineGreen else CyberTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isCopied) "Copied" else "Copy Code",
                    color = if (isCopied) CyberOnlineGreen else CyberTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Code Content
        SelectionContainer {
            Text(
                text = code,
                color = Color(0xFFE2E8F0),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            )
        }
    }
}

// Formatted Markdown text parser and renderer for AI messages
@Composable
fun FormattedAiMessage(
    text: String,
    fontSizeSp: Int = 15,
    modifier: Modifier = Modifier
) {
    // Break the text into code blocks and normal markdown segments
    val regex = Regex("```([a-zA-Z0-9_-]*)\\s*\\n?([\\s\\S]*?)```")
    var lastIndex = 0
    val elements = mutableListOf<@Composable () -> Unit>()

    for (match in regex.findAll(text)) {
        val nonCodePart = text.substring(lastIndex, match.range.first)
        if (nonCodePart.isNotBlank()) {
            elements.add {
                MarkdownParagraph(text = nonCodePart, fontSizeSp = fontSizeSp)
            }
        }

        val language = match.groupValues[1]
        val code = match.groupValues[2]
        elements.add {
            CodeBlockCard(language = language, code = code)
        }
        lastIndex = match.range.last + 1
    }

    if (lastIndex < text.length) {
        val trailingPart = text.substring(lastIndex)
        if (trailingPart.isNotBlank()) {
            elements.add {
                MarkdownParagraph(text = trailingPart, fontSizeSp = fontSizeSp)
            }
        }
    }

    Column(modifier = modifier) {
        for (element in elements) {
            element()
        }
    }
}

@Composable
fun MarkdownParagraph(
    text: String,
    fontSizeSp: Int = 15,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text) {
        buildAnnotatedString {
            val lines = text.split("\n")
            for ((index, line) in lines.withIndex()) {
                val trimmed = line.trim()
                if (trimmed.startsWith("### ")) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = (fontSizeSp + 2).sp)) {
                        append(trimmed.removePrefix("### "))
                    }
                } else if (trimmed.startsWith("## ")) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = (fontSizeSp + 3).sp)) {
                        append(trimmed.removePrefix("## "))
                    }
                } else if (trimmed.startsWith("# ")) {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = CyberCyan, fontSize = (fontSizeSp + 4).sp)) {
                        append(trimmed.removePrefix("# "))
                    }
                } else {
                    // Parse inline bold (**bold**) and code (`code`)
                    parseInlineMarkdown(line, fontSizeSp)
                }

                if (index < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    SelectionContainer {
        Text(
            text = annotatedString,
            color = CyberTextPrimary,
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp + 7).sp,
            modifier = modifier.padding(vertical = 2.dp)
        )
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.parseInlineMarkdown(
    line: String,
    fontSizeSp: Int
) {
    var cursor = 0
    val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
    val codeRegex = Regex("`(.*?)`")

    // Simple parser for **bold** and `code`
    var i = 0
    while (i < line.length) {
        if (line.startsWith("**", i)) {
            val end = line.indexOf("**", i + 2)
            if (end != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                    append(line.substring(i + 2, end))
                }
                i = end + 2
                continue
            }
        } else if (line.startsWith("`", i)) {
            val end = line.indexOf("`", i + 1)
            if (end != -1) {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = CyberCyan,
                        background = Color(0xFF131A30)
                    )
                ) {
                    append(line.substring(i + 1, end))
                }
                i = end + 1
                continue
            }
        }
        append(line[i])
        i++
    }
}
