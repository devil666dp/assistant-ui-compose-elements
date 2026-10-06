package com.assistantui.elements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.AuiColors
import com.assistantui.elements.ui.ElementShape
import com.assistantui.elements.ui.FieldShape
import com.assistantui.elements.ui.IconWell
import com.assistantui.elements.ui.PreviewFrame
import com.assistantui.elements.ui.paperBorder

enum class ApprovalState { Request, Running, Done, Denied }
enum class ApprovalVariant { Default, Destructive }
data class ApprovalDetail(val label: String, val value: String)

@Composable
fun ApprovalCard(
    state: ApprovalState,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    command: String? = null,
    description: String? = null,
    details: List<ApprovalDetail> = emptyList(),
    variant: ApprovalVariant = ApprovalVariant.Default,
    icon: ImageVector = Icons.Outlined.Terminal,
    onAllowOnce: (() -> Unit)? = null,
    onAlwaysAllow: (() -> Unit)? = null,
    onDeny: (() -> Unit)? = null,
    allowOnceLabel: String = "Allow once",
    alwaysAllowLabel: String = "Always allow",
    denyLabel: String = "Deny",
    statusLabel: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(paperBorder, ElementShape)
            .background(MaterialTheme.colorScheme.surface, ElementShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconWell(
                tint = if (variant == ApprovalVariant.Destructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface.copy(.45f),
            ) { tint -> Icon(icon, null, Modifier.size(16.dp), tint) }
            Column {
                Text(title, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(.45f))
            }
        }
        description?.let {
            Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(.60f))
        }
        command?.let {
            Text(
                it,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(.04f), FieldShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(.70f),
            )
        }
        if (details.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(.04f), FieldShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                details.forEach { detail ->
                    Row {
                        Text(
                            detail.label,
                            Modifier.weight(1f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(.40f),
                        )
                        Text(
                            detail.value,
                            Modifier.weight(2f),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(.80f),
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(32.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state == ApprovalState.Request) {
                onDeny?.let { GhostButton(denyLabel, it) }
                onAlwaysAllow?.let { GhostButton(alwaysAllowLabel, it) }
                onAllowOnce?.let {
                    Surface(
                        onClick = it,
                        shape = CircleShape,
                        color = if (variant == ApprovalVariant.Destructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface,
                        contentColor = if (variant == ApprovalVariant.Destructive) MaterialTheme.colorScheme.onError
                        else MaterialTheme.colorScheme.surface,
                    ) {
                        Text(
                            allowOnceLabel,
                            Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            } else {
                val pair = when (state) {
                    ApprovalState.Running -> Icons.Outlined.Refresh to (statusLabel ?: "Approved, running")
                    ApprovalState.Denied -> Icons.Outlined.Close to (statusLabel ?: "Denied")
                    ApprovalState.Done -> Icons.Outlined.Check to (statusLabel ?: "Finished")
                    else -> Icons.Outlined.Check to ""
                }
                Icon(
                    pair.first,
                    null,
                    Modifier.size(14.dp),
                    if (state == ApprovalState.Done) AuiColors.Green
                    else MaterialTheme.colorScheme.onSurface.copy(.45f),
                )
                Spacer(Modifier.size(8.dp))
                Text(pair.second, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(.55f))
            }
        }
    }
}

@Composable
private fun GhostButton(label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, color = androidx.compose.ui.graphics.Color.Transparent) {
        Text(
            label,
            Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(.45f),
        )
    }
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun ApprovalCardPreview() = PreviewFrame {
    ApprovalCard(
        state = ApprovalState.Request,
        command = "pnpm vitest run --changed",
        title = "Run command",
        subtitle = "The agent wants to run a shell command",
        onAllowOnce = {},
        onAlwaysAllow = {},
        onDeny = {},
    )
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun DestructiveApprovalCardPreview() = PreviewFrame {
    ApprovalCard(
        state = ApprovalState.Request,
        title = "Delete 12 archived conversations",
        subtitle = "This action cannot be undone",
        description = "The selected conversations and their generated files will be permanently removed.",
        details = listOf(
            ApprovalDetail("Conversations", "12 archived"),
            ApprovalDetail("Generated files", "38 files"),
        ),
        variant = ApprovalVariant.Destructive,
        icon = Icons.Outlined.DeleteOutline,
        allowOnceLabel = "Delete conversations",
        denyLabel = "Keep conversations",
        onAllowOnce = {},
        onDeny = {},
    )
}