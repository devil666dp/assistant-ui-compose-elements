package com.assistantui.elements.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assistantui.elements.ui.AuiColors
import com.assistantui.elements.ui.PreviewFrame
import com.assistantui.elements.ui.paperBorder
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class ActivityPoint(val date: LocalDate, val count: Int)

@Composable
fun ActivityGraph(
    data: List<ActivityPoint>,
    start: LocalDate,
    end: LocalDate,
    title: String,
    total: String,
    modifier: Modifier = Modifier,
) {
    val max = data.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    val values = data.associate { it.date to it.count }
    val leading = start.dayOfWeek.value - 1
    val dayCount = (end.toEpochDay() - start.toEpochDay() + 1).toInt()
    val columns = (leading + dayCount + 6) / 7
    val levels = List(leading) { 0 } + List(dayCount) { offset ->
        val count = values[start.plusDays(offset.toLong())] ?: 0
        if (count == 0) 0 else (count.toFloat() / max * 4).roundToInt().coerceIn(1, 4)
    }
    val cellColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = .06f),
        AuiColors.Blue.copy(alpha = .25f),
        AuiColors.Blue.copy(alpha = .45f),
        AuiColors.Blue.copy(alpha = .70f),
        AuiColors.Blue,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(paperBorder, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
            Text(
                total,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .35f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = (-.25).sp,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                listOf("", "Tue", "", "Thu", "", "Sat", "").forEach {
                    Text(
                        it,
                        modifier = Modifier.height(9.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .25f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.sp,
                        lineHeight = 9.sp,
                    )
                }
            }
            Canvas(
                Modifier
                    .width((columns * 12 - 3).dp)
                    .height(81.dp),
            ) {
                levels.forEachIndexed { index, level ->
                    val col = index / 7
                    val row = index % 7
                    drawRoundedCell(col, row, cellColors[level])
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            LegendLabel("less")
            Spacer(Modifier.width(6.dp))
            cellColors.forEach {
                Spacer(
                    Modifier
                        .padding(horizontal = 1.5.dp)
                        .size(9.dp)
                        .background(it, RoundedCornerShape(2.dp)),
                )
            }
            Spacer(Modifier.width(6.dp))
            LegendLabel("more")
        }
    }
}

private fun DrawScope.drawRoundedCell(column: Int, row: Int, color: Color) {
    drawRoundRect(
        color = color,
        topLeft = Offset(column * 12.dp.toPx(), row * 12.dp.toPx()),
        size = Size(9.dp.toPx(), 9.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
    )
}

@Composable
private fun LegendLabel(value: String) = Text(
    value,
    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .25f),
    fontFamily = FontFamily.Monospace,
    fontSize = 7.sp,
)

private fun activityDemoData(): List<ActivityPoint> {
    val start = LocalDate.of(2026, 2, 2)
    return List(182) { i ->
        val date = start.plusDays(i.toLong())
        val weekend = date.dayOfWeek.value >= 6
        val wave = abs(sin(i * .21)) + abs(cos(i * .07))
        val count = if (weekend) (wave * 2).roundToInt()
        else (wave * 9).roundToInt() + ((i * 7) % 3)
        ActivityPoint(date, count)
    }
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun ActivityGraphPreview() = PreviewFrame {
    val data = activityDemoData()
    ActivityGraph(
        data = data,
        start = data.first().date,
        end = data.last().date,
        title = "Agent runs",
        total = "${data.sumOf { it.count }} in 6 months",
    )
}