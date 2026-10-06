package com.assistantui.elements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.AuiColors
import com.assistantui.elements.ui.PreviewFrame

@Composable
fun AgentHandoff(
    from: String,
    to: String,
    reason: String,
    carried: List<String>,
    settled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AgentChip(
                text = from,
                background = MaterialTheme.colorScheme.onSurface.copy(.04f),
                foreground = MaterialTheme.colorScheme.onSurface.copy(.45f),
                modifier = Modifier.alpha(if (settled) .45f else 1f),
            )
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward,
                null,
                Modifier.size(14.dp),
                if (settled) MaterialTheme.colorScheme.onSurface.copy(.25f) else AuiColors.Blue,
            )
            AgentChip(
                text = to,
                background = if (settled) MaterialTheme.colorScheme.onSurface.copy(.04f) else AuiColors.Blue.copy(.12f),
                foreground = if (settled) MaterialTheme.colorScheme.onSurface.copy(.80f) else Color(0xFF1D4ED8),
            )
        }
        Text(reason, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface.copy(.55f))
        if (carried.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "carried over",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(.30f),
                )
                carried.forEach {
                    Text(
                        it,
                        modifier = Modifier
                            .width(320.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(.12f))
                            .padding(start = 1.dp)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(start = 10.dp),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(.60f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentChip(text: String, background: Color, foreground: Color, modifier: Modifier = Modifier) {
    Row(
        modifier.background(background, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.Outlined.SmartToy, null, Modifier.size(12.dp), foreground)
        Text(text, fontSize = 12.sp, color = foreground)
    }
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun AgentHandoffPreview() = PreviewFrame {
    AgentHandoff(
        from = "Triage",
        to = "Maintainer",
        reason = "Triage reproduced the report and narrowed it to the converter, so the fix goes to the agent that can write and verify a patch.",
        carried = listOf("The failing test and its output", "The two files the reader already narrowed to"),
        settled = false,
    )
}