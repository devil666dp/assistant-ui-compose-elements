import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private val Ink = Color(0xFF171717)
private val Muted = Color(0xFF73736F)
private val Faint = Color(0xFFA4A4A0)
private val Border = Color(0xFFE8E8E5)
private val Soft = Color(0xFFF7F7F6)
private val Raised = Color(0xFFF1F1EF)
private val Blue = Color(0xFF3B82F6)
private val Green = Color(0xFF10B981)
private val Red = Color(0xFFDC2626)
private val Code = Color(0xFF0F1720)
private val Mono = FontFamily.Monospace

private data class DemoInfo(
    val title: String,
    val slug: String,
    val description: String,
    val file: String,
    val code: String,
)

private val demos = listOf(
    DemoInfo("Activity graph", "activity-graph", "A half-year of runs as a calendar of cells, dense where the work was.", "ActivityGraph.kt", """
@Composable
fun ActivityGraph(
    data: List<ActivityPoint>,
    title: String,
    total: String,
) {
    Column(
        Modifier
            .border(paperBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(title, fontSize = 13.5.sp)
            Text(total, fontFamily = FontFamily.Monospace)
        }
        ActivityHeatMap(data)
        ActivityLegend()
    }
}
""".trimIndent()),
    DemoInfo("Agent card", "agent-card", "Who you are about to talk to: its skills, its model, and the endpoint behind it.", "AgentCard.kt", """
@Composable
fun AgentCard(
    name: String,
    description: String,
    skills: List<AgentSkill>,
    connected: Boolean,
    onConnect: () -> Unit,
) {
    PaperCard {
        AgentIdentity(name, version = "1.4.0")
        Text(description)
        skills.forEach { SkillRow(it) }
        ConnectButton(connected, onConnect)
    }
}
""".trimIndent()),
    DemoInfo("Handoff", "agent-handoff", "Control passing between agents, with the reason and what came along.", "AgentHandoff.kt", """
@Composable
fun AgentHandoff(
    from: String,
    to: String,
    reason: String,
    carried: List<String>,
    settled: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row { AgentChip(from); Text("→"); AgentChip(to) }
        Text(reason)
        carried.forEach { CarriedItem(it) }
    }
}
""".trimIndent()),
    DemoInfo("Agent plan", "agent-plan", "A checklist the agent works through, with progress you can glance.", "AgentPlan.kt", """
@Composable
fun AgentPlan(
    steps: List<AgentPlanStep>,
    activeIndex: Int,
) {
    val completed = activeIndex.coerceIn(0, steps.size)
    Column {
        ProgressHeader(completed, steps.size)
        LinearProgress(completed.toFloat() / steps.size)
        steps.forEachIndexed { index, step ->
            PlanStep(step, index, completed)
        }
    }
}
""".trimIndent()),
    DemoInfo("Agent status", "agent-status", "One pill that always answers: what is it doing, and for how long.", "AgentStatus.kt", """
@Composable
fun AgentStatus(
    state: AgentState,
    label: String,
    elapsed: String?,
) {
    Row(
        Modifier
            .border(paperBorder, CircleShape)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        StatusMark(state)
        Text(label)
        elapsed?.let { MonoText(it) }
    }
}
""".trimIndent()),
    DemoInfo("Approval card", "approval-card", "Human in the loop: the agent asks before it takes a consequential action.", "ApprovalCard.kt", """
@Composable
fun ApprovalCard(
    state: ApprovalState,
    command: String,
    onAllowOnce: () -> Unit,
    onAlwaysAllow: () -> Unit,
    onDeny: () -> Unit,
) {
    PaperCard {
        ApprovalHeader()
        CommandField(command)
        ApprovalActions(state)
    }
}
""".trimIndent()),
    DemoInfo("Artifact card", "artifact-card", "A generated document as a tangible object, written live and versioned.", "ArtifactCard.kt", """
@Composable
fun ArtifactCard(
    title: String,
    meta: String,
    generating: Boolean,
    words: Int,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick, shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(14.dp)) {
            FileIcon(generating)
            ArtifactMetadata(title, meta, words)
            Text("↗")
        }
    }
}
""".trimIndent()),
    DemoInfo("Assistant modal", "assistant-modal", "A floating chat bubble with a thread list and a resizable assistant window.", "AssistantModal.kt", """
@Composable
fun AssistantModal() {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        if (open) AssistantWindow(Modifier.align(Alignment.BottomEnd))
        AssistantLauncher(
            open = open,
            onClick = { open = !open },
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}
""".trimIndent()),
    DemoInfo("Assistant sidebar", "assistant-sidebar", "A resizable side panel for copilot experiences and contextual assistance.", "AssistantSidebar.kt", """
@Composable
fun AssistantSidebar(content: @Composable () -> Unit) {
    Row(Modifier.fillMaxSize()) {
        Box(Modifier.weight(.62f)) { content() }
        ResizeHandle()
        Box(Modifier.weight(.38f)) { AssistantThread() }
    }
}
""".trimIndent()),
    DemoInfo("Attachment", "attachment", "Runtime attachments for the composer, with previews, progress, and removal.", "Attachment.kt", """
@Composable
fun AttachmentComposer(
    attachments: List<AttachmentItem>,
    onAdd: () -> Unit,
    onRemove: (AttachmentItem) -> Unit,
    onSend: () -> Unit,
) {
    ComposerSurface {
        AttachmentRail(attachments, onRemove)
        MessageInput()
        ComposerActions(onAdd, onSend)
    }
}
""".trimIndent()),
)

@Composable
fun AssistantUiGallery() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Ink,
            background = Color.White,
            surface = Color.White,
            onSurface = Ink,
            outline = Border,
        ),
    ) {
        var selected by remember { mutableIntStateOf(0) }
        val current = demos[selected]
        BoxWithConstraints(Modifier.fillMaxSize().background(Color.White)) {
            val compact = maxWidth < 760.dp
            val showToc = maxWidth > 1180.dp
            Column(Modifier.fillMaxSize()) {
                TopBar(compact)
                if (compact) {
                    CompactNav(selected) { selected = it }
                    MainContent(current, selected, compact = true) { selected = it }
                } else {
                    Row(Modifier.fillMaxSize()) {
                        SideBar(selected) { selected = it }
                        MainContent(current, selected, Modifier.weight(1f)) { selected = it }
                        if (showToc) TableOfContents()
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(compact: Boolean) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).border(0.5.dp, Border).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(7.dp))
        Text("assistant-ui", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(Modifier.width(7.dp))
        Text(
            "COMPOSE/WASM",
            Modifier.background(Color(0xFFEAF2FF), RoundedCornerShape(5.dp)).padding(horizontal = 6.dp, vertical = 3.dp),
            color = Blue,
            fontFamily = Mono,
            fontSize = 8.sp,
        )
        Spacer(Modifier.weight(1f))
        if (!compact) {
            TopLink("Components", active = true)
            TopLink("Docs")
            TopLink("GitHub")
            Spacer(Modifier.weight(1f))
            Text(
                "Search　⌘ K",
                Modifier.background(Soft, RoundedCornerShape(8.dp)).border(1.dp, Border, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                color = Muted,
                fontSize = 10.sp,
            )
        }
        Text("Theme", Modifier.padding(start = 12.dp), color = Muted, fontSize = 9.sp)
    }
}

@Composable private fun TopLink(text: String, active: Boolean = false) {
    Text(
        text,
        Modifier.background(if (active) Soft else Color.Transparent, RoundedCornerShape(7.dp)).padding(horizontal = 11.dp, vertical = 7.dp),
        color = if (active) Ink else Muted,
        fontSize = 11.sp,
    )
}

@Composable
private fun SideBar(selected: Int, onSelect: (Int) -> Unit) {
    Column(
        Modifier.width(236.dp).fillMaxHeight().border(0.5.dp, Border).padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MicroText("Elements")
            MicroText("10")
        }
        Spacer(Modifier.height(12.dp))
        Text("Search  Filter", Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(7.dp)).padding(9.dp), color = Faint, fontSize = 10.sp)
        Spacer(Modifier.height(14.dp))
        demos.forEachIndexed { index, demo ->
            Row(
                Modifier.fillMaxWidth()
                    .background(if (selected == index) Raised else Color.Transparent, RoundedCornerShape(7.dp))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 9.dp, vertical = 8.dp),
            ) {
                Text(demo.title, Modifier.weight(1f), color = if (selected == index) Ink else Muted, fontSize = 11.sp)
                if (selected == index) Text(">", color = Faint, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.fillMaxWidth().height(1.dp).background(Border))
        Spacer(Modifier.height(12.dp))
        MicroText("Actual Kotlin/Wasm UI")
        Text("Compose Multiplatform", color = Muted, fontSize = 9.sp)
    }
}

@Composable
private fun CompactNav(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).border(0.5.dp, Border).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        demos.forEachIndexed { index, demo ->
            Text(
                demo.title,
                Modifier
                    .background(if (selected == index) Raised else Color.Transparent, RoundedCornerShape(7.dp))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                color = if (selected == index) Ink else Muted,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun MainContent(
    current: DemoInfo,
    selected: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(horizontal = if (compact) 16.dp else 54.dp, vertical = 44.dp),
    ) {
        MicroText("COMPOSE ELEMENT    ${selected + 1} / ${demos.size}")
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(current.title, fontSize = if (compact) 36.sp else 48.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-2).sp)
                Spacer(Modifier.height(12.dp))
                Text(current.description, color = Muted, fontSize = 14.sp, lineHeight = 22.sp)
            }
            if (!compact) {
                RoundAction("<") { onSelect((selected - 1 + demos.size) % demos.size) }
                Spacer(Modifier.width(6.dp))
                RoundAction(">") { onSelect((selected + 1) % demos.size) }
            }
        }
        Spacer(Modifier.height(27.dp))
        Playground(current, compact)
        Spacer(Modifier.height(56.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(52.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1.15f)) {
                MicroText("IMPLEMENTATION")
                Spacer(Modifier.height(12.dp))
                Text("Actual Compose, running in your browser", fontSize = 26.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-1).sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "This preview is rendered by Kotlin/Wasm and Compose Multiplatform. It is not a screenshot or an HTML imitation: buttons, state, layout and animation execute inside the browser canvas.",
                    color = Muted,
                    lineHeight = 22.sp,
                    fontSize = 12.sp,
                )
            }
            if (!compact) {
                Column(Modifier.weight(.85f)) {
                    Fact("Runtime", "Kotlin/Wasm")
                    Fact("Design system", "Material 3")
                    Fact("Source", current.file)
                    Fact("Interaction", "Live Compose")
                }
            }
        }
        Spacer(Modifier.height(70.dp))
    }
}

@Composable
private fun Playground(current: DemoInfo, compact: Boolean) {
    Column(Modifier.fillMaxWidth().border(1.dp, Border, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))) {
        Row(Modifier.fillMaxWidth().height(49.dp).background(Color.White).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("● ● ●", color = Border, fontSize = 10.sp)
            Spacer(Modifier.width(12.dp))
            Text(current.file, fontFamily = Mono, fontSize = 9.sp, color = Muted)
            Spacer(Modifier.weight(1f))
            Text("Kotlin/Wasm", Modifier.background(Soft, RoundedCornerShape(7.dp)).padding(horizontal = 10.dp, vertical = 7.dp), color = Muted, fontSize = 9.sp)
            Spacer(Modifier.width(6.dp))
            Text("Run  Live", Modifier.background(Ink, RoundedCornerShape(7.dp)).padding(horizontal = 13.dp, vertical = 7.dp), color = Color.White, fontSize = 9.sp)
        }
        if (compact) {
            Column {
                CodePanel(current.code, Modifier.fillMaxWidth().height(390.dp))
                PreviewPanel(current, Modifier.fillMaxWidth().height(560.dp))
            }
        } else {
            Row(Modifier.fillMaxWidth().height(610.dp)) {
                CodePanel(current.code, Modifier.weight(1.08f).fillMaxHeight())
                PreviewPanel(current, Modifier.weight(.92f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun CodePanel(code: String, modifier: Modifier) {
    Column(modifier.background(Code)) {
        Row(Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Kotlin", color = Color(0xFF8190A3), fontFamily = Mono, fontSize = 9.sp)
            Spacer(Modifier.weight(1f))
            Text("Compose Multiplatform", color = Color(0xFF536174), fontFamily = Mono, fontSize = 8.sp)
        }
        SelectionContainer {
            Row(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).horizontalScroll(rememberScrollState()).padding(vertical = 16.dp),
            ) {
                Text(
                    code.lines().indices.joinToString("\n") { (it + 1).toString().padStart(2) },
                    Modifier.padding(start = 14.dp, end = 10.dp),
                    color = Color(0xFF435064),
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    lineHeight = 18.sp,
                )
                Text(
                    colorizeCode(code),
                    Modifier.padding(end = 20.dp),
                    color = Color(0xFFD7E0EB),
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

private fun colorizeCode(code: String) = code

@Composable
private fun PreviewPanel(current: DemoInfo, modifier: Modifier) {
    Column(modifier.background(Soft)) {
        Row(Modifier.fillMaxWidth().height(40.dp).background(Color.White).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Compose/Wasm preview", color = Muted, fontFamily = Mono, fontSize = 9.sp)
            Spacer(Modifier.weight(1f))
            Text("Pixel 9", Modifier.background(Soft, RoundedCornerShape(7.dp)).padding(horizontal = 10.dp, vertical = 7.dp), color = Muted, fontSize = 9.sp)
        }
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Phone(current)
        }
    }
}

@Composable
private fun Phone(current: DemoInfo) {
    Column(
        Modifier.width(330.dp).height(510.dp).background(Ink, RoundedCornerShape(34.dp)).padding(7.dp)
            .background(Color.White, RoundedCornerShape(28.dp)).clip(RoundedCornerShape(28.dp)),
    ) {
        Row(Modifier.fillMaxWidth().height(29.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("9:41", fontSize = 7.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Box(Modifier.width(64.dp).height(17.dp).background(Color(0xFF090B0D), CircleShape))
            Spacer(Modifier.weight(1f))
            Text("LTE", fontSize = 7.sp)
        }
        Row(Modifier.fillMaxWidth().height(42.dp).border(0.5.dp, Border).padding(horizontal = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Compose Elements", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("Theme", fontSize = 7.sp, color = Muted)
        }
        Box(Modifier.fillMaxSize().padding(17.dp), contentAlignment = Alignment.Center) {
            when (current.slug) {
                "activity-graph" -> ActivityGraphDemo()
                "agent-card" -> AgentCardDemo()
                "agent-handoff" -> AgentHandoffDemo()
                "agent-plan" -> AgentPlanDemo()
                "agent-status" -> AgentStatusDemo()
                "approval-card" -> ApprovalCardDemo()
                "artifact-card" -> ArtifactCardDemo()
                "assistant-modal" -> AssistantModalDemo()
                "assistant-sidebar" -> AssistantSidebarDemo()
                else -> AttachmentDemo()
            }
        }
    }
}

@Composable
private fun ActivityGraphDemo() {
    PaperCard {
        Row(Modifier.fillMaxWidth()) {
            DemoTitle("Agent runs")
            Spacer(Modifier.weight(1f))
            MonoText("1,743 in 6 months")
        }
        Canvas(Modifier.fillMaxWidth().height(82.dp)) {
            val cell = 7.dp.toPx()
            val gap = 2.dp.toPx()
            repeat(126) { index ->
                val col = index / 7
                val row = index % 7
                val wave = abs(sin(index * .42)) + abs(cos(index * .17))
                val level = (wave * 2.2).toInt().coerceIn(0, 4)
                val colors = listOf(Ink.copy(.06f), Blue.copy(.25f), Blue.copy(.45f), Blue.copy(.7f), Blue)
                drawRoundRect(colors[level], Offset(col * (cell + gap), row * (cell + gap)), Size(cell, cell), CornerRadius(2.dp.toPx()))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            MonoText("less   ")
            Text("■ ■ ■ ■", color = Blue, fontSize = 7.sp)
            MonoText("   more")
        }
    }
}

@Composable
private fun AgentCardDemo() {
    var connected by remember { mutableStateOf(false) }
    PaperCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconWell("♙")
            Spacer(Modifier.width(9.dp))
            Column {
                DemoTitle("Maintainer   v1.4.0")
                DemoMuted("assistant-ui")
            }
        }
        DemoMuted("Works through the issue queue: reproduces the report, writes the fix, and opens the PR.")
        listOf("triage" to "Read an issue and label it", "repro" to "Build a minimal reproduction", "patch" to "Open a PR with the fix").forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MonoText(it.first, Modifier.background(Ink.copy(.04f), RoundedCornerShape(6.dp)).padding(5.dp))
                Spacer(Modifier.width(7.dp)); DemoMuted(it.second)
            }
        }
        DemoButton(if (connected) "✓  Connected" else "Connect", connected) { connected = true }
    }
}

@Composable
private fun AgentHandoffDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Chip("♙ Triage"); Text(" → ", color = Blue, fontSize = 11.sp); Chip("♙ Maintainer", Blue.copy(.12f), Blue)
        }
        DemoMuted("Triage reproduced the report and narrowed it to the converter, so the fix goes to the agent that can write and verify a patch.")
        MonoText("carried over")
        DemoMuted("│  The failing test and its output")
        DemoMuted("│  The two files the reader already narrowed to")
    }
}

@Composable
private fun AgentPlanDemo() {
    var active by remember { mutableIntStateOf(1) }
    val steps = listOf("Read existing composer state", "Design the draft store", "Wire runtime persistence", "Add regression tests", "Update the docs")
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row { DemoTitle("Composer draft"); Spacer(Modifier.weight(1f)); MonoText("$active of 5") }
        Box(Modifier.fillMaxWidth().height(3.dp).background(Ink.copy(.06f), CircleShape)) {
            Box(Modifier.fillMaxWidth(active / 5f).height(3.dp).background(Ink.copy(.8f), CircleShape))
        }
        steps.forEachIndexed { index, step ->
            Row(Modifier.alpha(if (index < active) .42f else if (index == active) 1f else .34f)) {
                Text(if (index < active) "✓" else if (index == active) "◌" else "•", Modifier.width(20.dp), fontSize = 10.sp)
                Column { Text(step, fontSize = 9.sp); if (index == active) DemoMuted("Keep pending changes local until they are ready.") }
            }
        }
        DemoButton("Advance step") { active = (active + 1) % 6 }
    }
}

@Composable
private fun AgentStatusDemo() {
    val transition = rememberInfiniteTransition()
    val opacity by transition.animateFloat(.25f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse))
    Row(Modifier.border(1.dp, Border, CircleShape).padding(start = 12.dp, end = 7.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).alpha(opacity).background(Blue, CircleShape))
        Spacer(Modifier.width(8.dp)); Text("Refactoring composer", fontSize = 9.sp)
        Spacer(Modifier.width(8.dp)); MonoText("0:08")
        Spacer(Modifier.width(8.dp)); Text("Ⅱ", fontSize = 9.sp)
    }
}

@Composable
private fun ApprovalCardDemo() {
    var state by remember { mutableStateOf("request") }
    PaperCard {
        Row { IconWell(">_"); Spacer(Modifier.width(9.dp)); Column { DemoTitle("Run command"); DemoMuted("The agent wants to run a shell command") } }
        MonoText("pnpm vitest run --changed", Modifier.fillMaxWidth().background(Ink.copy(.04f), RoundedCornerShape(9.dp)).padding(9.dp))
        if (state == "request") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { state = "denied" }, contentPadding = PaddingValues(6.dp)) { Text("Deny", color = Muted, fontSize = 8.sp) }
                DemoButton("Allow once") { state = "done" }
            }
        } else {
            Text(if (state == "done") "✓  Finished" else "×  Denied", Modifier.align(Alignment.End), color = if (state == "done") Green else Red, fontSize = 8.sp)
        }
    }
}

@Composable
private fun ArtifactCardDemo() {
    var words by remember { mutableIntStateOf(138) }
    Row(
        Modifier.fillMaxWidth().border(1.dp, Border, RoundedCornerShape(16.dp)).clickable { words = (words + 19).coerceAtMost(214) }.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWell("▤"); Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) { DemoTitle("Draft persistence RFC"); MonoText("Writing · $words words") }
        Text("↗", color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun AssistantModalDemo() {
    var open by remember { mutableStateOf(true) }
    Box(Modifier.fillMaxSize()) {
        if (open) {
            Column(
                Modifier.fillMaxWidth().height(300.dp).align(Alignment.Center).border(1.dp, Border, RoundedCornerShape(12.dp)).background(Color.White, RoundedCornerShape(12.dp)),
            ) {
                Row(Modifier.fillMaxWidth().height(36.dp).border(0.5.dp, Border).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    DemoTitle("New Chat"); Spacer(Modifier.weight(1f)); Text("↶  ＋", fontSize = 9.sp)
                }
                Column(Modifier.padding(14.dp)) { Text("How can I help?", fontSize = 16.sp); DemoMuted("Ask a question or describe what you want to build.") }
                Spacer(Modifier.weight(1f))
                Box(Modifier.fillMaxWidth().height(70.dp).padding(10.dp).background(Soft, RoundedCornerShape(16.dp)).padding(10.dp)) { DemoMuted("Send a message…") }
            }
        }
        Button(
            onClick = { open = !open },
            Modifier.align(Alignment.BottomEnd).requiredSize(38.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Ink),
            contentPadding = PaddingValues(0.dp),
        ) { Text(if (open) "⌄" else "♙", color = Color.White) }
    }
}

@Composable
private fun AssistantSidebarDemo() {
    Row(Modifier.fillMaxWidth().height(320.dp).border(1.dp, Border)) {
        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) { DemoMuted("Your application") }
        Box(Modifier.width(4.dp).fillMaxHeight().background(Border))
        Column(Modifier.weight(.65f).fillMaxHeight().padding(10.dp)) {
            DemoTitle("How can I help?"); DemoMuted("Ask a question or describe what you want to build.")
            Spacer(Modifier.weight(1f)); Box(Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(14.dp)).padding(9.dp)) { DemoMuted("Send a message…") }
        }
    }
}

@Composable
private fun AttachmentDemo() {
    Column(
        Modifier.fillMaxWidth().background(Soft, RoundedCornerShape(18.dp)).border(1.dp, Border, RoundedCornerShape(18.dp)).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { AttachmentTile(true); AttachmentTile(false) }
        DemoMuted("Send a message…")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("＋", Modifier.padding(7.dp), fontSize = 15.sp)
            Box(Modifier.size(28.dp).background(Ink, CircleShape), contentAlignment = Alignment.Center) { Text("↑", color = Color.White, fontSize = 11.sp) }
        }
    }
}

@Composable
private fun AttachmentTile(image: Boolean) {
    Box(
        Modifier.size(45.dp).background(if (image) Color(0xFFBFDBFE) else Raised, RoundedCornerShape(11.dp)).border(1.dp, Border, RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (!image) Text("▤", fontSize = 14.sp)
        Box(Modifier.align(Alignment.TopEnd).padding(3.dp).size(14.dp).background(Ink.copy(.55f), CircleShape), contentAlignment = Alignment.Center) { Text("×", color = Color.White, fontSize = 7.sp) }
    }
}

@Composable
private fun PaperCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().border(1.dp, Border, RoundedCornerShape(16.dp)).background(Color.White, RoundedCornerShape(16.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable private fun IconWell(text: String) = Box(Modifier.size(30.dp).background(Ink.copy(.05f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Text(text, color = Ink.copy(.5f), fontSize = 12.sp) }
@Composable private fun DemoTitle(text: String) = Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
@Composable private fun DemoMuted(text: String) = Text(text, color = Ink.copy(.48f), fontSize = 8.sp, lineHeight = 12.sp)
@Composable private fun MonoText(text: String, modifier: Modifier = Modifier) = Text(text, modifier, color = Ink.copy(.38f), fontFamily = Mono, fontSize = 7.sp)
@Composable private fun MicroText(text: String) = Text(text, color = Faint, fontFamily = Mono, fontSize = 8.sp, letterSpacing = .5.sp)
@Composable private fun Chip(text: String, background: Color = Ink.copy(.05f), color: Color = Ink.copy(.55f)) = Text(text, Modifier.background(background, CircleShape).padding(horizontal = 8.dp, vertical = 5.dp), color = color, fontSize = 8.sp)

@Composable
private fun DemoButton(text: String, muted: Boolean = false, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        Modifier.height(27.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = if (muted) Soft else Ink, contentColor = if (muted) Muted else Color.White),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) { Text(text, fontSize = 8.sp) }
}

@Composable
private fun RoundAction(text: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, border = BorderStroke(1.dp, Border), color = Color.White) {
        Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) { Text(text, fontSize = 11.sp) }
    }
}

@Composable
private fun Fact(label: String, value: String) {
    Row(Modifier.fillMaxWidth().border(0.5.dp, Border).padding(vertical = 12.dp)) {
        Text(label, Modifier.weight(1f), color = Faint, fontSize = 9.sp)
        Text(value, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TableOfContents() {
    Column(Modifier.width(180.dp).fillMaxHeight().border(0.5.dp, Border).padding(horizontal = 20.dp, vertical = 54.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        MicroText("ON THIS PAGE")
        Spacer(Modifier.height(4.dp))
        Text("Interactive preview", color = Muted, fontSize = 9.sp)
        Text("Kotlin source", color = Muted, fontSize = 9.sp)
        Text("Implementation", color = Muted, fontSize = 9.sp)
        Text("GitHub repository", color = Muted, fontSize = 9.sp)
    }
}