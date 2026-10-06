package com.assistantui.elements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.PreviewFrame

data class AttachmentItem(val name: String, val isImage: Boolean = false)

@Composable
fun AttachmentComposer(
    attachments: List<AttachmentItem>,
    modifier: Modifier = Modifier,
    hint: String = "Send a message...",
    onAdd: () -> Unit = {},
    onRemove: (AttachmentItem) -> Unit = {},
    onSend: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(.10f),
                RoundedCornerShape(24.dp),
            )
            .background(MaterialTheme.colorScheme.onSurface.copy(.04f), RoundedCornerShape(24.dp))
            .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            attachments.forEach { AttachmentTile(it, { onRemove(it) }) }
        }
        Text(
            hint,
            Modifier.padding(start = 14.dp, top = 6.dp, bottom = 12.dp),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(.45f),
        )
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CircleAction(onAdd, light = true) {
                Icon(Icons.Outlined.Add, "Add Attachment", Modifier.size(20.dp))
            }
            CircleAction(onSend, light = false) {
                Icon(
                    Icons.Outlined.ArrowUpward,
                    "Send message",
                    Modifier.size(20.dp),
                    MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}

@Composable
private fun AttachmentTile(item: AttachmentItem, onRemove: () -> Unit) {
    Box(Modifier.size(56.dp)) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    if (item.isImage) Brush.linearGradient(
                        listOf(androidx.compose.ui.graphics.Color(0xFFDBEAFE), androidx.compose.ui.graphics.Color(0xFFBFDBFE)),
                    ) else Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant),
                    ),
                    RoundedCornerShape(14.dp),
                )
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(.10f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (!item.isImage) {
                Icon(
                    Icons.Outlined.Description,
                    item.name,
                    Modifier.size(24.dp),
                    MaterialTheme.colorScheme.onSurface.copy(.65f),
                )
            }
        }
        Box(
            Modifier.align(Alignment.TopEnd).padding(4.dp).size(20.dp)
                .background(androidx.compose.ui.graphics.Color.Black.copy(.50f), CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Close, "Remove file", Modifier.size(12.dp), androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Composable
private fun CircleAction(onClick: () -> Unit, light: Boolean, content: @Composable () -> Unit) {
    Box(
        Modifier.size(34.dp)
            .background(
                if (light) androidx.compose.ui.graphics.Color.Transparent else MaterialTheme.colorScheme.onSurface,
                CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Preview(showBackground = true, widthDp = 640)
@Composable
private fun AttachmentPreview() = PreviewFrame {
    AttachmentComposer(
        attachments = listOf(
            AttachmentItem("screenshot.png", isImage = true),
            AttachmentItem("document.pdf"),
        ),
    )
}