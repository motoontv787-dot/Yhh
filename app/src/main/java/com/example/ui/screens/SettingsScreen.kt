package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.UserSettings
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberOfflineRed
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.util.cyberGlass
import com.example.util.performCyberHaptic

@Composable
fun SettingsScreen(
    currentSettings: UserSettings,
    onSaveSettings: (UserSettings) -> Unit,
    onClearAllData: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf(currentSettings.userName) }
    var selectedLanguage by remember { mutableStateOf(currentSettings.preferredLanguage) }
    var responseStyle by remember { mutableStateOf(currentSettings.responseStyle) }
    var aiModel by remember { mutableStateOf(currentSettings.aiModel) }
    var selectedAccentHex by remember { mutableStateOf(currentSettings.accentColorHex) }
    var autoRead by remember { mutableStateOf(currentSettings.autoReadResponses) }
    var ttsSpeed by remember { mutableFloatStateOf(currentSettings.ttsSpeed) }
    var ttsPitch by remember { mutableFloatStateOf(currentSettings.ttsPitch) }
    var fontSizeSp by remember { mutableStateOf(currentSettings.fontSizeSp) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val accentColors = listOf(
        "#00F0FF" to "Cyan",
        "#BC13FE" to "Purple",
        "#00FF9D" to "Emerald",
        "#FF007A" to "Pink",
        "#FFB300" to "Amber"
    )

    val models = listOf("gemini-3.8-flash", "gemini-3.5-flash-lite", "gemini-4-argon")
    val styles = listOf("Balanced", "Short", "Detailed", "Professional", "Friendly", "Creative")
    val languages = listOf("English", "Urdu", "Hindi", "Arabic", "Spanish", "French", "German")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    performCyberHaptic(context)
                    onBack()
                },
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberCyan
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "SYSTEM SETTINGS",
                color = CyberTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Personalization Section
        SettingsCard(title = "USER & CALLSIGN", icon = Icons.Default.Language) {
            Text("Address User As:", color = CyberTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = userName,
                onValueChange = {
                    userName = it
                    onSaveSettings(currentSettings.copy(userName = it))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberCyan.copy(alpha = 0.3f),
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("user_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Preferred Language:", color = CyberTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(languages) { lang ->
                    val isSelected = selectedLanguage == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyberCyan else CyberSurfaceVariant)
                            .clickable {
                                selectedLanguage = lang
                                onSaveSettings(currentSettings.copy(preferredLanguage = lang))
                            }
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Intelligence Section
        SettingsCard(title = "AI INTELLIGENCE & PERSONALITY", icon = Icons.Default.Psychology) {
            Text("AI Model Architecture:", color = CyberTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(models) { model ->
                    val isSelected = aiModel == model
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyberPurple else CyberSurfaceVariant)
                            .clickable {
                                aiModel = model
                                onSaveSettings(currentSettings.copy(aiModel = model))
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = model,
                            color = if (isSelected) Color.White else CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Response Style:", color = CyberTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(styles) { style ->
                    val isSelected = responseStyle == style
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyberCyan else CyberSurfaceVariant)
                            .clickable {
                                responseStyle = style
                                onSaveSettings(currentSettings.copy(responseStyle = style))
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = style,
                            color = if (isSelected) Color.Black else CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Theme & Visuals
        SettingsCard(title = "CYBER THEME & ACCENT", icon = Icons.Default.ColorLens) {
            Text("Cyber Accent Lighting:", color = CyberTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for ((hex, name) in accentColors) {
                    val color = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = selectedAccentHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedAccentHex = hex
                                onSaveSettings(currentSettings.copy(accentColorHex = hex))
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Chat Font Size (${fontSizeSp}sp):", color = CyberTextSecondary, fontSize = 12.sp)
            Slider(
                value = fontSizeSp.toFloat(),
                onValueChange = {
                    fontSizeSp = it.toInt()
                    onSaveSettings(currentSettings.copy(fontSizeSp = fontSizeSp))
                },
                valueRange = 12f..20f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = CyberCyan,
                    activeTrackColor = CyberCyan
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speech & Voice Settings
        SettingsCard(title = "SPEECH & VOICE CORE", icon = Icons.Default.VolumeUp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Auto Read AI Responses", color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("Read answers aloud via voice engine", color = CyberTextMuted, fontSize = 11.sp)
                }
                Switch(
                    checked = autoRead,
                    onCheckedChange = {
                        autoRead = it
                        onSaveSettings(currentSettings.copy(autoReadResponses = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberCyan,
                        checkedTrackColor = CyberSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Speech Rate (${String.format("%.1fx", ttsSpeed)}):", color = CyberTextSecondary, fontSize = 12.sp)
            Slider(
                value = ttsSpeed,
                onValueChange = {
                    ttsSpeed = it
                    onSaveSettings(currentSettings.copy(ttsSpeed = it))
                },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(thumbColor = CyberPurple, activeTrackColor = CyberPurple)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Voice Pitch (${String.format("%.1f", ttsPitch)}):", color = CyberTextSecondary, fontSize = 12.sp)
            Slider(
                value = ttsPitch,
                onValueChange = {
                    ttsPitch = it
                    onSaveSettings(currentSettings.copy(ttsPitch = it))
                },
                valueRange = 0.5f..1.5f,
                colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Data Storage & Privacy
        SettingsCard(title = "STORAGE & LOCAL DATA", icon = Icons.Default.DeleteForever) {
            Text(
                text = "All chat history is encrypted and persisted locally on your device.",
                color = CyberTextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { showClearConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberOfflineRed.copy(alpha = 0.15f),
                    contentColor = CyberOfflineRed
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberOfflineRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .testTag("clear_data_button")
            ) {
                Text("Clear All Local Chat Data", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // About Mivazo Section
        SettingsCard(title = "SYSTEM INFO", icon = Icons.Default.Info) {
            Text(
                text = "Mivazo AI",
                color = CyberCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Created for MOHSIN BOSS",
                color = CyberPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Version 1.0.0 Cyber Edition • Real-Time AI Intelligence via Firebase AI Logic & Gemini Models.",
                color = CyberTextMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Erase All Data?", color = CyberOfflineRed, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all saved chats and messages on this device, Boss.", color = CyberTextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirmDialog = false
                        Toast.makeText(context, "Local chat archives cleared, Boss.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberOfflineRed, contentColor = Color.White)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = CyberTextSecondary)
                }
            },
            containerColor = CyberSurfaceVariant
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .cyberGlass(cornerRadius = 16.dp, backgroundColor = CyberSurface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
