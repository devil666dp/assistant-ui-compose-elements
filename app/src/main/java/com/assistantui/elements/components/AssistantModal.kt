package com.assistantui.elements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.assistantui.elements.ui.AssistantUITheme
import com.assistantui.elements.ui.paperBorder

@Composable
fun AssistantModal(
    modifier: Modifier = Modifier,
    initiallyOpen: Boolean = false,
) {
    var open by remember { mutableStateOf(initiallyOpen) }
    var showThreads by remember { mutableStateOf(false) }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Surface(
            onClick = { open = !open },
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            border = paperBorder,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    if (open) Icons.Outlined.ExpandMore else Icons.Outlined.SmartToy,
                    if (open) "Close Assistant" else "Open Assistant",
                    Modifier.size(20.dp),
                )
            }
        }
    }

    if (open) {
        Dialog(
            onDismissRequest = { /* Web original ignores outside press. */ },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Column(
                    Modifier
                        .width(400.dp)
                        .height(500.dp)
                        .shadow(24.dp, RoundedCornerShape(12.dp))
                        .border(paperBorder, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                ) {
                    Row(
                        Modifier
                            .height(44.dp)
                            .border(
                                width = 0.dp,
                                color = androidx.compose.ui.graphics.Color.Transparent,
                            )
                            .padding(start = 14.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            if (showThreads) "Threads" else "New Chat",
                            Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        )
                        IconButton(
                            onClick = { showThreads = !showThreads },
                            Modifier.size(28.dp),
                        ) {
                            Icon(Icons.Outlined.History, "Threads", Modifier.size(14.dp))
                        }
                        IconButton(
                            onClick = { showThreads = false },
                            Modifier.size(28.dp),
                        ) {
                            Icon(Icons.Outlined.Add, "New Thread", Modifier.size(14.dp))
                        }
                    }
                    Box(
                        Modifier
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(.10f)),
                    )
                    if (showThreads) ThreadList() else AssistantThread()
                }
            }
        }
    }
}

@Composable
private fun ThreadList() {
    Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf("Composer draft", "Fix failing converter", "New Chat").forEachIndexed { index, title ->
            Row(
                Modifier
                    .background(
                        if (index == 0) MaterialTheme.colorScheme.onSurface.copy(.05f)
                        else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            ) {
                Text(title, fontSize = 13.sp)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 460, heightDp = 620)
@Composable
private fun AssistantModalPreview() = AssistantUITheme {
    AssistantModal(Modifier.padding(16.dp), initiallyOpen = true)
}