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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.AuiColors
import com.assistantui.elements.ui.ElementShape
import com.assistantui.elements.ui.IconWell
import com.assistantui.elements.ui.PreviewFrame
import com.assistantui.elements.ui.paperBorder

data class AgentSkill(val name: String, val description: String)

@Composable
fun AgentCard(
    name: String,
    description: String,
    provider: String,
    version: String,
    model: String,
    endpoint: String,
    skills: List<AgentSkill>,
    connected: Boolean,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(paperBorder, ElementShape)
            .background(MaterialTheme.colorScheme.surface, ElementShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            IconWell { tint ->
                Icon(Icons.Outlined.SmartToy, null, Modifier.size(16.dp), tint)
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        name,
                        Modifier.weight(1f, fill = false),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    MonoText("v$version", .30f)
                }
                Text(provider, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(.45f))
            }
        }
        Text(
            description,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(.60f),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            skills.forEach { skill ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        skill.name,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onSurface.copy(.04f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(.55f),
                    )
                    Text(
                        skill.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(.45f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(.07f)),
        )
        Row {
            MonoText(endpoint, .30f, Modifier.weight(1f))
            MonoText(model, .30f)
        }
        Surface(
            onClick = onConnect,
            enabled = !connected,
            modifier = Modifier.fillMaxWidth().height(32.dp),
            shape = CircleShape,
            color = if (connected) MaterialTheme.colorScheme.onSurface.copy(.04f)
            else MaterialTheme.colorScheme.onSurface,
            contentColor = if (connected) MaterialTheme.colorScheme.onSurface.copy(.55f)
            else MaterialTheme.colorScheme.surface,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (connected) {
                    Icon(Icons.Outlined.Check, null, Modifier.size(14.dp), AuiColors.Green)
                    Spacer(Modifier.size(6.dp))
                }
                Text(if (connected) "Connected" else "Connect", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MonoText(value: String, alpha: Float, modifier: Modifier = Modifier) = Text(
    value,
    modifier,
    fontFamily = FontFamily.Monospace,
    fontSize = 11.sp,
    color = MaterialTheme.colorScheme.onSurface.copy(alpha),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
)

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun AgentCardPreview() = PreviewFrame {
    AgentCard(
        name = "Maintainer",
        description = "Works through the issue queue: reproduces the report, writes the fix, and opens the PR.",
        provider = "assistant-ui",
        version = "1.4.0",
        model = "opus",
        endpoint = "https://agents.example.com/a2a",
        skills = listOf(
            AgentSkill("triage", "Read an issue and label it"),
            AgentSkill("repro", "Build a minimal reproduction"),
            AgentSkill("patch", "Open a PR with the fix"),
        ),
        connected = false,
        onConnect = {},
    )
}