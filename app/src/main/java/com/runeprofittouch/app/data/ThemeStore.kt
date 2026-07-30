package com.runeprofittouch.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeStore {
    private const val PREFERENCES = "theme_preferences"
    private const val COLOR_KEY = "primary_color"
    private const val DARK_MODE_KEY = "dark_mode"
    const val DEFAULT_COLOR = 0xFF6650A4L

    val presets = listOf(
        "Mauve" to 0xFF6650A4L,
        "Violet profond" to 0xFF7B1FA2L,
        "Bleu" to 0xFF1565C0L,
        "Turquoise" to 0xFF00796BL,
        "Vert" to 0xFF2E7D32L,
        "Orange" to 0xFFEF6C00L,
        "Rouge" to 0xFFC62828L,
        "Rose" to 0xFFAD1457L
    )

    private val _primaryColor = MutableStateFlow(DEFAULT_COLOR)
    val primaryColor = _primaryColor.asStateFlow()
    private val _darkMode = MutableStateFlow(false)
    val darkMode = _darkMode.asStateFlow()

    fun initialize(context: Context) {
        val preferences = context.getSharedPreferences(
            PREFERENCES,
            Context.MODE_PRIVATE
        )
        _primaryColor.value = preferences.getLong(COLOR_KEY, DEFAULT_COLOR)
        _darkMode.value = preferences.getBoolean(DARK_MODE_KEY, false)
    }

    fun select(context: Context, color: Long) {
        _primaryColor.value = color
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putLong(COLOR_KEY, color)
            .apply()
    }

    fun selectDarkMode(context: Context, enabled: Boolean) {
        _darkMode.value = enabled
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(DARK_MODE_KEY, enabled)
            .apply()
    }

    fun parseHex(value: String): Long? {
        val clean = value.trim().removePrefix("#")
        if (clean.length != 6 || clean.any { !it.isDigit() && it.lowercaseChar() !in 'a'..'f' }) {
            return null
        }
        return runCatching { 0xFF000000L or clean.toLong(16) }.getOrNull()
    }
}
