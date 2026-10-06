package com.assistantui.elements.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.PreviewFrame

data class AgentPlanStep(val id: String? = null, val label: String, val description: String? = null)

@Composable
fun AgentPlan(
    steps: List<AgentPlanStep>,
    activeIndex: Int,
    modifier: Modifier = Modifier,
    title: String = "Plan",
) {
    val completed = activeIndex.coerceIn(0, steps.size)
    val allDone = completed >= steps.size
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
            Text(
                "$completed of ${steps.size}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(.35f),
            )
        }
        Box(
            Modifier.fillMaxWidth().height(3.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(.06f)),
        ) {
            Box(
                Modifier.fillMaxWidth(if (steps.isEmpty()) 0f else completed.toFloat() / steps.size)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(.80f), CircleShape),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            steps.forEachIndexed { index, step ->
                val done = allDone || index < completed
                val active = !allDone && index == completed
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                        when {
                            done -> Icon(
                                Icons.Outlined.Check,
                                null,
                                Modifier.size(14.dp),
                                MaterialTheme.colorScheme.onSurface.copy(.35f),
                            )
                            active -> Spinner()
                            else -> Box(
                                Modifier.size(6.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(.15f), CircleShape),
                            )
                        }
                    }
                    Column {
                        Text(
                            step.label,
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                when { done -> .40f; active -> .90f; else -> .35f },
                            ),
                        )
                        if (active && step.description != null) {
                            Text(
                                step.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(.45f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Spinner() {
    val transition = rememberInfiniteTransition(label = "spinner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation",
    )
    Icon(Icons.Outlined.Refresh, null, Modifier.size(14.dp).rotate(rotation))
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun AgentPlanPreview() = PreviewFrame {
    AgentPlan(
        title = "Composer draft",
        activeIndex = 1,
        steps = listOf(
            AgentPlanStep("read", "Read existing composer state", "Trace the current data flow before changing it."),
            AgentPlanStep("design", "Design the draft store", "Keep pending changes local until they are ready."),
            AgentPlanStep("wire", "Wire runtime persistence"),
            AgentPlanStep("test", "Add regression tests"),
            AgentPlanStep("docs", "Update the docs"),
        ),
    )
}