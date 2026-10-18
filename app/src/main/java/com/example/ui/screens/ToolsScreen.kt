package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.util.cyberGlass

enum class CyberToolCategory(val label: String, val icon: ImageVector, val color: Color) {
    WRITER("AI Writer", Icons.Default.Create, CyberCyan),
    TRANSLATOR("Translator", Icons.Default.Language, CyberPurple),
    PROGRAMMER("AI Coder", Icons.Default.Code, CyberGreen),
    SUMMARIZER("Summarizer", Icons.Default.Notes, CyberPink)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsBottomSheet(
    onDismiss: () -> Unit,
    onLaunchToolPrompt: (String) -> Unit,
    onPickImage: () -> Unit,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.4f))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ MIVAZO AI CYBER TOOLS",
                    color = CyberCyan,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = CyberTextSecondary
                    )
                }
            }

            // Quick Media Access Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable {
                            onDismiss()
                            onPickImage()
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Select photo",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Attach Image",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tool Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberSurface,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            ) {
                CyberToolCategory.entries.forEachIndexed { index, tool ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tool.label,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) tool.color else CyberTextMuted
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tool Content Panels
            when (CyberToolCategory.entries[selectedTab]) {
                CyberToolCategory.WRITER -> WriterToolPanel(
                    onGenerate = { prompt ->
                        onDismiss()
                        onLaunchToolPrompt(prompt)
                    }
                )
                CyberToolCategory.TRANSLATOR -> TranslatorToolPanel(
                    onTranslate = { prompt ->
                        onDismiss()
                        onLaunchToolPrompt(prompt)
                    }
                )
                CyberToolCategory.PROGRAMMER -> ProgrammerToolPanel(
                    onCode = { prompt ->
                        onDismiss()
                        onLaunchToolPrompt(prompt)
                    }
                )
                CyberToolCategory.SUMMARIZER -> SummarizerToolPanel(
                    onSummarize = { prompt ->
                        onDismiss()
                        onLaunchToolPrompt(prompt)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WriterToolPanel(onGenerate: (String) -> Unit) {
    var writerType by remember { mutableStateOf("Story") }
    var topicText by remember { mutableStateOf("") }
    val writerTypes = listOf("Story", "Song", "Caption", "Script", "Email", "Post", "Essay")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Content Format:", color = CyberTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(writerTypes) { type ->
                val isSelected = writerType == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) CyberCyan else CyberSurfaceVariant)
                        .clickable { writerType = type }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = type,
                        color = if (isSelected) Color.Black else CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = topicText,
            onValueChange = { topicText = it },
            placeholder = { Text("What should the $writerType be about, Boss?", color = CyberTextMuted, fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberCyan.copy(alpha = 0.25f),
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val prompt = "Write a high-quality $writerType about: ${topicText.ifBlank { "Futuristic Cyber AI technology" }}. Please format it cleanly and make it engaging, Boss."
                onGenerate(prompt)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("generate_writer_button")
        ) {
            Text("Generate $writerType", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TranslatorToolPanel(onTranslate: (String) -> Unit) {
    val languages = listOf("Urdu", "Hindi", "Arabic", "Chinese", "Spanish", "French", "German", "Turkish", "English")
    var targetLanguage by remember { mutableStateOf("Urdu") }
    var sourceText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Translate to:", color = CyberTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(languages) { lang ->
                val isSelected = targetLanguage == lang
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) CyberPurple else CyberSurfaceVariant)
                        .clickable { targetLanguage = lang }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = lang,
                        color = if (isSelected) Color.White else CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = sourceText,
            onValueChange = { sourceText = it },
            placeholder = { Text("Enter text to translate...", color = CyberTextMuted, fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberPurple,
                unfocusedBorderColor = CyberPurple.copy(alpha = 0.25f),
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val prompt = "Translate the following text accurately into $targetLanguage. Provide pronunciation/notes if helpful, Boss:\n\n\"${sourceText.ifBlank { "Hello, welcome to Mivazo AI by MOHSIN BOSS." }}\""
                onTranslate(prompt)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberPurple, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("translate_button")
        ) {
            Text("Translate into $targetLanguage", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProgrammerToolPanel(onCode: (String) -> Unit) {
    val languages = listOf("Kotlin", "Python", "JavaScript", "HTML/CSS", "Java", "C++", "SQL", "JSON")
    var selectedLanguage by remember { mutableStateOf("Kotlin") }
    var taskText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Language / Tech:", color = CyberTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(languages) { lang ->
                val isSelected = selectedLanguage == lang
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) CyberGreen else CyberSurfaceVariant)
                        .clickable { selectedLanguage = lang }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = lang,
                        color = if (isSelected) Color.Black else CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = taskText,
            onValueChange = { taskText = it },
            placeholder = { Text("What code or algorithm do you need in $selectedLanguage, Boss?", color = CyberTextMuted, fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberGreen,
                unfocusedBorderColor = CyberGreen.copy(alpha = 0.25f),
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val prompt = "Write clean, modern, production-grade $selectedLanguage code for: ${taskText.ifBlank { "A complete REST API data repository with error handling" }}. Include detailed comments, syntax highlighting, and an explanation for MOHSIN BOSS."
                onCode(prompt)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("generate_code_button")
        ) {
            Text("Generate $selectedLanguage Code", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SummarizerToolPanel(onSummarize: (String) -> Unit) {
    val styles = listOf("Short Summary", "Detailed Summary", "Bullet Points", "Key Points")
    var selectedStyle by remember { mutableStateOf("Bullet Points") }
    var textToSummarize by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Summary Mode:", color = CyberTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(styles) { style ->
                val isSelected = selectedStyle == style
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) CyberPink else CyberSurfaceVariant)
                        .clickable { selectedStyle = style }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = style,
                        color = if (isSelected) Color.White else CyberTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = textToSummarize,
            onValueChange = { textToSummarize = it },
            placeholder = { Text("Paste text or article to summarize, Boss...", color = CyberTextMuted, fontSize = 13.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberPink,
                unfocusedBorderColor = CyberPink.copy(alpha = 0.25f),
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val prompt = "Please summarize the following text using $selectedStyle format, Boss:\n\n\"${textToSummarize.ifBlank { "Artificial Intelligence is transforming computing, automation, and user interfaces across the world." }}\""
                onSummarize(prompt)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberPink, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("summarize_button")
        ) {
            Text("Summarize Text", fontWeight = FontWeight.Bold)
        }
    }
}
