# assistant-ui Elements for Jetpack Compose

An Android/Jetpack Compose Material 3 port of the first ten entries in the
[assistant-ui Elements](https://www.assistant-ui.com/elements) catalog.

## Included elements

| # | Web element | Kotlin source | Android Studio preview |
|---|---|---|---|
| 1 | Activity graph | `ActivityGraph.kt` | `ActivityGraphPreview` |
| 2 | Agent card | `AgentCard.kt` | `AgentCardPreview` |
| 3 | Handoff | `AgentHandoff.kt` | `AgentHandoffPreview` |
| 4 | Agent plan | `AgentPlan.kt` | `AgentPlanPreview` |
| 5 | Agent status | `AgentStatus.kt` | `AgentStatusPreview` |
| 6 | Approval card | `ApprovalCard.kt` | `ApprovalCardPreview`, `DestructiveApprovalCardPreview` |
| 7 | Artifact card | `ArtifactCard.kt` | `ArtifactCardPreview` |
| 8 | Assistant modal | `AssistantModal.kt` | `AssistantModalPreview` |
| 9 | Assistant sidebar | `AssistantSidebar.kt` | `AssistantSidebarPreview` |
| 10 | Attachment | `Attachment.kt` | `AttachmentPreview` |

All component files are under:

`app/src/main/java/com/assistantui/elements/components/`

## Open and run

1. Open this folder in a current Android Studio release.
2. Let Gradle sync and install Android SDK 35 if prompted.
3. Open any component Kotlin file and choose **Split** or **Design** to render
   its `@Preview`.
4. Run the `app` configuration for the small component catalog.

The project uses `minSdk 26`, `compileSdk 35`, Kotlin 2.1.10, the Compose BOM,
Material 3, and Material Icons Extended.

The complete project was compiled successfully with Gradle
`:app:assembleDebug`; the packaged debug APK is included beside the source zip.

## Web component gallery

The repository includes an assistant-ui-style website with a Kotlin source
viewer and an interactive phone preview for all ten components.

```bash
npm run build:web
npm run dev:web
```

`zerops.yaml` builds the static site and deploys the generated `dist/`
directory to a Zerops Static service.

## Manual APK build

The APK no longer builds on every commit. Open GitHub Actions and manually run
**Build debug APK (manual)** when an Android package is needed. The workflow
uploads the generated APK as an artifact retained for 14 days.

## Fidelity

The Compose code intentionally preserves the upstream measurements:

- 20 dp card corners, 16 dp card padding, 14 dp content gaps
- 13.5 sp titles, 12 sp body text, 11 sp monospace metadata
- 36 dp icon wells with 12 dp corners
- 32 dp pill actions and 44 dp assistant launcher
- alpha-based ink, field, border, blue, green, and destructive states
- matching loading/pulse/rotation states and interactive callbacks

See `PORTING_NOTES.md` for the line-by-line React-to-Compose mapping. The
`reference/` directory contains the upstream React sources and full-page visual
captures used during the port.

## License

The copied upstream reference code remains subject to the assistant-ui
repository license. The Kotlin port should be reviewed against that license
before redistribution.