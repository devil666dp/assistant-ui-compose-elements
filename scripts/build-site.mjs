import { mkdir, readFile, rm, writeFile, cp } from "node:fs/promises";
import { resolve } from "node:path";

const root = resolve(import.meta.dirname, "..");
const source = resolve(root, "website");
const dist = resolve(root, "dist");

const components = [
  ["activity-graph", "Activity graph", "A half-year of runs as a calendar of cells, dense where the work was.", "ActivityGraph.kt"],
  ["agent-card", "Agent card", "Who you are about to talk to: its skills, its model, and the endpoint behind it.", "AgentCard.kt"],
  ["agent-handoff", "Handoff", "Control passing between agents, with the reason and what came along.", "AgentHandoff.kt"],
  ["agent-plan", "Agent plan", "A checklist the agent works through, with progress you can glance.", "AgentPlan.kt"],
  ["agent-status", "Agent status", "One pill that always answers: what is it doing, and for how long.", "AgentStatus.kt"],
  ["approval-card", "Approval card", "Human in the loop: the agent asks before it takes a consequential action.", "ApprovalCard.kt"],
  ["artifact-card", "Artifact card", "A generated document as a tangible object, written live and versioned.", "ArtifactCard.kt"],
  ["assistant-modal", "Assistant modal", "A floating chat bubble with a thread list and a resizable assistant window.", "AssistantModal.kt"],
  ["assistant-sidebar", "Assistant sidebar", "A resizable side panel for copilot experiences and contextual assistance.", "AssistantSidebar.kt"],
  ["attachment", "Attachment", "Runtime attachments for the composer, with previews, progress, and removal.", "Attachment.kt"],
];

const kotlinRoot = resolve(
  root,
  "app/src/main/java/com/assistantui/elements/components",
);

const payload = [];
for (const [slug, title, description, file] of components) {
  payload.push({
    slug,
    title,
    description,
    file,
    code: await readFile(resolve(kotlinRoot, file), "utf8"),
  });
}

await rm(dist, { recursive: true, force: true });
await mkdir(dist, { recursive: true });
let html = await readFile(resolve(source, "index.html"), "utf8");
html = html.replace(
  "__COMPONENT_DATA__",
  JSON.stringify(payload).replaceAll("<", "\\u003c"),
);
await writeFile(resolve(dist, "index.html"), html);
await cp(resolve(source, "styles.css"), resolve(dist, "styles.css"));
await cp(resolve(source, "app.js"), resolve(dist, "app.js"));
await writeFile(resolve(dist, ".nojekyll"), "");
console.log(`Built ${payload.length} component demos into ${dist}`);