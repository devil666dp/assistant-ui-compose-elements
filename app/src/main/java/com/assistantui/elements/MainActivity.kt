package com.assistantui.elements

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.components.AgentCard
import com.assistantui.elements.components.AgentSkill
import com.assistantui.elements.components.AgentState
import com.assistantui.elements.components.AgentStatus
import com.assistantui.elements.components.ApprovalCard
import com.assistantui.elements.components.ApprovalState
import com.assistantui.elements.components.ArtifactCard
import com.assistantui.elements.components.AttachmentComposer
import com.assistantui.elements.components.AttachmentItem
import com.assistantui.elements.ui.AssistantUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AssistantUITheme { CatalogScreen() } }
    }
}

@Composable
private fun CatalogScreen() {
    Scaffold { insets ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Text("assistant-ui · Compose", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "The first ten web elements, ported to Kotlin and Material 3.",
                color = MaterialTheme.colorScheme.onSurface.copy(.55f),
            )
            ElementSection("Agent card") {
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
            ElementSection("Agent status") {
                AgentStatus(AgentState.Working, "Refactoring composer", elapsed = "0:08")
            }
            ElementSection("Approval card") {
                ApprovalCard(
                    state = ApprovalState.Request,
                    command = "pnpm vitest run --changed",
                    title = "Run command",
                    subtitle = "The agent wants to run a shell command",
                    onDeny = {},
                    onAlwaysAllow = {},
                    onAllowOnce = {},
                )
            }
            ElementSection("Artifact card") {
                ArtifactCard("Draft persistence RFC", "Document · v3 · just now")
            }
            ElementSection("Attachment") {
                AttachmentComposer(
                    listOf(
                        AttachmentItem("screenshot.png", true),
                        AttachmentItem("document.pdf"),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ElementSection(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        content()
    }
}