package com.runeprofittouch.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

private val LuxuryColorScheme = darkColorScheme(
    primary = AntiqueGold,
    onPrimary = Obsidian,
    primaryContainer = Color(0xFF332814),
    onPrimaryContainer = BrightGold,
    secondary = Emerald,
    onSecondary = Obsidian,
    secondaryContainer = Color(0xFF0C3828),
    onSecondaryContainer = Color(0xFFA9F4CD),
    tertiary = BrightGold,
    background = Obsidian,
    onBackground = Ivory,
    surface = ObsidianSoft,
    onSurface = Ivory,
    surfaceVariant = Graphite,
    onSurfaceVariant = Mist,
    outline = Color(0xFF675535),
    outlineVariant = Color(0xFF302A20),
    error = Ember,
    surfaceTint = AntiqueGold
)

@Composable
fun RuneProfitTouchTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    primaryColor: Color = AntiqueGold,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryColorScheme,
        typography = Typography,
        content = content
    )
}
