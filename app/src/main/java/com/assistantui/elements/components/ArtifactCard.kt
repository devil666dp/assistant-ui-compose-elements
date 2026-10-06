package com.assistantui.elements.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.ElementShape
import com.assistantui.elements.ui.IconWell
import com.assistantui.elements.ui.PreviewFrame
import com.assistantui.elements.ui.paperBorder

@Composable
fun ArtifactCard(
    title: String,
    meta: String,
    modifier: Modifier = Modifier,
    generating: Boolean = false,
    words: Int = 0,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(paperBorder, ElementShape)
            .background(MaterialTheme.colorScheme.surface, ElementShape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconWell { tint ->
            val pulse = rememberInfiniteTransition(label = "document")
            val opacity by pulse.animateFloat(
                .45f,
                1f,
                infiniteRepeatable(tween(850), RepeatMode.Reverse),
                label = "opacity",
            )
            Icon(
                Icons.Outlined.Description,
                null,
                Modifier.size(16.dp).alpha(if (generating) opacity else 1f),
                tint,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row {
                Text(
                    if (generating) "Writing" else meta,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(.40f),
                )
                if (generating) {
                    Text(
                        " · $words words",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(.40f),
                    )
                }
            }
        }
        Spacer(Modifier.size(2.dp))
        Icon(
            Icons.Outlined.ArrowOutward,
            null,
            Modifier.size(14.dp),
            MaterialTheme.colorScheme.onSurface.copy(.35f),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ArtifactCardPreview() = PreviewFrame {
    ArtifactCard("Draft persistence RFC", "Document · v3 · just now", generating = true, words = 138)
}