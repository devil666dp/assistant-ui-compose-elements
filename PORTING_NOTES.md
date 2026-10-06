# React → Jetpack Compose porting notes

Upstream repository commit: `0d72f48`.

## Shared translation

| React/Tailwind source | Compose equivalent |
|---|---|
| `paper` | `surface` background + `paperBorder` at 10% ink |
| `field` | `onSurface.copy(alpha = .04f)` |
| `rounded-[20px]` | `RoundedCornerShape(20.dp)` |
| `rounded-xl` | `RoundedCornerShape(12.dp)` |
| `text-[13.5px]` | `fontSize = 13.5.sp` |
| `text-xs` | `fontSize = 12.sp` |
| `mono` | `FontFamily.Monospace`, 11 sp |
| `size-9` | `36.dp` |
| `size-8` | `32.dp` |
| `gap-3.5` | `Arrangement.spacedBy(14.dp)` |
| `active:scale-[0.96]` | Material `Surface`/`clickable` press handling |
| `motion-reduce:*` | Compose animations are decorative; static previews remain valid |

## 1. Activity graph

- `HeatGraph.Root` becomes date-range normalization using `LocalDate`.
- Monday-first layout is preserved with `dayOfWeek.value - 1`.
- `HeatGraph.Grid` becomes one `Canvas`; each cell is exactly 9×9 dp, has a
  2 dp corner radius, and sits on a 12 dp pitch (the original 3 px gap).
- The five `LEVEL_TINT` values map to 6% ink and 25/45/70/100% blue.
- Tuesday, Thursday, and Saturday are the visible alternating row labels.

## 2. Agent card

- Every prop has a direct Kotlin parameter.
- `AgentSkill` is a Kotlin data class.
- `skills.map` becomes `skills.forEach`.
- `disabled={connected}` becomes `enabled = !connected`.
- Connected and disconnected button surfaces preserve field/ink appearance.

## 3. Handoff

- `settled` changes source-chip opacity, arrow color, and destination surface.
- The carried-over left rule is reproduced as a 1 dp ink strip.
- All copy and demo values are unchanged.

## 4. Agent plan

- `progressOf` is represented by `activeIndex.coerceIn(0, steps.size)`.
- Progress width is the same completed/total fraction.
- Done, active, and future steps map to check, rotating refresh, and dot.
- Only the active item displays its description.

## 5. Agent status

- The string union becomes `enum class AgentState`.
- Working uses a pulsing blue dot; waiting uses an outlined dot.
- Done/failed use check/close and switch the trailing icon to refresh.
- `trailing` remains an optional composable slot.

## 6. Approval card

- State and variant unions become enums.
- Optional command and details blocks are only emitted when supplied.
- Request actions remain individually optional callbacks.
- Running, done, and denied receipts preserve icon and text semantics.
- The destructive preview ports the upstream second demo.

## 7. Artifact card

- `generating` pulses the file icon and switches metadata to writing/word count.
- The web-only hover arrow is kept permanently visible for touch discoverability.
- The complete card is clickable.

## 8. Assistant modal

- Popover state becomes remembered Compose state.
- The 400×500 panel, 44 dp header/launcher, 12 dp radius, border, and bottom-end
  anchoring match the web source.
- Outside dismiss is intentionally ignored, matching the web cancellation of
  outside-press and focus-out.
- Thread and thread-list views keep the same header controls.

## 9. Assistant sidebar

- The horizontal resizable panel group becomes weighted Compose boxes.
- The 8 dp divider accepts horizontal drag gestures and clamps the assistant
  panel to 25–65% width.
- The child application remains a composable slot.

## 10. Attachment

- The 56 dp tile, 14 dp radius, 20 dp remove control, composer border, 24 dp
  composer radius, attachment rail, add action, and send action are preserved.
- Image data can replace the gradient placeholder in `AttachmentTile`.
- Add, remove, and send behaviors are callbacks rather than runtime-coupled APIs.