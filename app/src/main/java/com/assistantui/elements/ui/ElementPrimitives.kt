package com.assistantui.elements.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val ElementShape = RoundedCornerShape(20.dp)
val FieldShape = RoundedCornerShape(12.dp)
val IconShape = RoundedCornerShape(12.dp)

@Composable
fun IconWell(
    modifier: Modifier = Modifier,
    tint: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = .45f),
    content: @Composable (Color) -> Unit,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .background(
                androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = .05f),
                IconShape,
            ),
        contentAlignment = Alignment.Center,
    ) { content(tint) }
}