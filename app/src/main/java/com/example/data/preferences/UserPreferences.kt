package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val userName: String = "Boss",
    val aiNickname: String = "Mivazo AI",
    val preferredLanguage: String = "English",
    val responseStyle: String = "Balanced",
    val aiModel: String = "gemini-3.8-flash",
    val accentColorHex: String = "#00F0FF",
    val themeMode: String = "Cyber Neon",
    val autoReadResponses: Boolean = false,
    val ttsSpeed: Float = 1.0f,
    val ttsPitch: Float = 1.0f,
    val fontSizeSp: Int = 15
) {
    fun getAccentColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(accentColorHex))
        } catch (_: Exception) {
            Color(0xFF00F0FF)
        }
    }
}

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mivazo_user_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        return UserSettings(
            userName = prefs.getString("user_name", "Boss") ?: "Boss",
            aiNickname = prefs.getString("ai_nickname", "Mivazo AI") ?: "Mivazo AI",
            preferredLanguage = prefs.getString("pref_language", "English") ?: "English",
            responseStyle = prefs.getString("response_style", "Balanced") ?: "Balanced",
            aiModel = prefs.getString("ai_model", "gemini-3.8-flash") ?: "gemini-3.8-flash",
            accentColorHex = prefs.getString("accent_color", "#00F0FF") ?: "#00F0FF",
            themeMode = prefs.getString("theme_mode", "Cyber Neon") ?: "Cyber Neon",
            autoReadResponses = prefs.getBoolean("auto_read", false),
            ttsSpeed = prefs.getFloat("tts_speed", 1.0f),
            ttsPitch = prefs.getFloat("tts_pitch", 1.0f),
            fontSizeSp = prefs.getInt("font_size", 15)
        )
    }

    fun updateSettings(newSettings: UserSettings) {
        prefs.edit()
            .putString("user_name", newSettings.userName)
            .putString("ai_nickname", newSettings.aiNickname)
            .putString("pref_language", newSettings.preferredLanguage)
            .putString("response_style", newSettings.responseStyle)
            .putString("ai_model", newSettings.aiModel)
            .putString("accent_color", newSettings.accentColorHex)
            .putString("theme_mode", newSettings.themeMode)
            .putBoolean("auto_read", newSettings.autoReadResponses)
            .putFloat("tts_speed", newSettings.ttsSpeed)
            .putFloat("tts_pitch", newSettings.ttsPitch)
            .putInt("font_size", newSettings.fontSizeSp)
            .apply()
        _settings.value = newSettings
    }
}
