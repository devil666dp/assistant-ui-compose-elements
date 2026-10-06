package com.assistantui.elements.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object AuiColors {
    val Blue = Color(0xFF3B82F6)
    val Green = Color(0xFF10B981)
    val Red = Color(0xFFDC2626)
    val Ink = Color(0xFF171717)
}

val paperBorder: BorderStroke
    @Composable get() = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .10f))

@Composable
fun AssistantUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF60A5FA),
            background = Color(0xFF171717),
            surface = Color(0xFF202020),
            surfaceVariant = Color(0xFF2A2A2A),
            onBackground = Color.White,
            onSurface = Color.White,
            error = Color(0xFFF87171),
        )
    } else {
        lightColorScheme(
            primary = AuiColors.Blue,
            background = Color.White,
            surface = Color.White,
            surfaceVariant = Color(0xFFF5F5F5),
            onBackground = AuiColors.Ink,
            onSurface = AuiColors.Ink,
            error = AuiColors.Red,
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}