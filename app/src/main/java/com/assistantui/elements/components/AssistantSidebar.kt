package com.assistantui.elements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.AssistantUITheme

@Composable
fun AssistantSidebar(
    modifier: Modifier = Modifier,
    initialAssistantFraction: Float = .38f,
    content: @Composable () -> Unit,
) {
    var assistantFraction by remember { mutableFloatStateOf(initialAssistantFraction) }
    val density = LocalDensity.current
    Row(modifier.fillMaxSize()) {
        Box(Modifier.weight(1f - assistantFraction).fillMaxSize()) { content() }
        Box(
            Modifier
                .width(8.dp)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, amount ->
                        val parentWidthPx = with(density) { 800.dp.toPx() }
                        assistantFraction = (assistantFraction - amount / parentWidthPx).coerceIn(.25f, .65f)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier.width(1.dp).fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(.10f)),
            )
        }
        Box(
            Modifier.weight(assistantFraction).fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        ) { AssistantThread() }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 520)
@Composable
private fun AssistantSidebarPreview() = AssistantUITheme {
    AssistantSidebar {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Your application", fontSize = 22.sp)
        }
    }
}