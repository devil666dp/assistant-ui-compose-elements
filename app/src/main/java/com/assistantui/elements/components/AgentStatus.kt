package com.assistantui.elements.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.AuiColors
import com.assistantui.elements.ui.PreviewFrame
import com.assistantui.elements.ui.paperBorder

enum class AgentState { Working, Waiting, Done, Failed }

@Composable
fun AgentStatus(
    state: AgentState,
    label: String,
    modifier: Modifier = Modifier,
    elapsed: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .border(paperBorder, CircleShape)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (state) {
            AgentState.Done -> Icon(Icons.Outlined.Check, null, Modifier.size(12.dp), AuiColors.Green)
            AgentState.Failed -> Icon(Icons.Outlined.Close, null, Modifier.size(12.dp), MaterialTheme.colorScheme.error)
            AgentState.Working -> PulsingDot()
            AgentState.Waiting -> Box(
                Modifier.size(6.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(.35f), CircleShape),
            )
        }
        Text(label, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (elapsed != null && state != AgentState.Done && state != AgentState.Failed) {
            Text(
                elapsed,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(.30f),
            )
        }
        Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            trailing?.invoke() ?: Icon(
                if (state == AgentState.Done || state == AgentState.Failed) Icons.Outlined.Refresh else Icons.Outlined.Pause,
                null,
                Modifier.size(12.dp),
                MaterialTheme.colorScheme.onSurface.copy(.45f),
            )
        }
    }
}

@Composable
private fun PulsingDot() {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val opacity by pulse.animateFloat(
        initialValue = .35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "opacity",
    )
    Box(Modifier.size(6.dp).alpha(opacity).background(AuiColors.Blue, CircleShape))
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun AgentStatusPreview() = PreviewFrame {
    AgentStatus(AgentState.Working, "Refactoring composer", elapsed = "0:08")
}