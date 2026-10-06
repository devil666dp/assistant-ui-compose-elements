const components = JSON.parse(
  document.querySelector("#component-data").textContent,
);

const $ = (selector) => document.querySelector(selector);
const componentNav = $("#componentNav");
const previewRoot = $("#previewRoot");
const codeView = $("#codeView");
const phone = $("#phone");
let currentIndex = 0;
let previewDark = false;
let connected = false;
let approvalState = "request";
let planStep = 1;
let artifactWords = 138;
let modalOpen = true;

const escapeHtml = (value) =>
  value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;");

function highlightLine(line) {
  let out = escapeHtml(line);
  if (/^\s*\/\//.test(line)) return `<span class="tok-comment">${out}</span>`;
  out = out
    .replace(/(&quot;.*?&quot;)/g, '<span class="tok-string">$1</span>')
    .replace(/\b(package|import|fun|data|class|enum|private|when|if|else|val|var|return|object|internal|for|in|is)\b/g, '<span class="tok-key">$1</span>')
    .replace(/\b(Composable|Modifier|String|Int|Boolean|Float|Color|List|LocalDate|AgentState|ApprovalState)\b/g, '<span class="tok-type">$1</span>')
    .replace(/\b(\d+(?:\.\d+)?)(?=\.?(?:dp|sp|f)?\b)/g, '<span class="tok-number">$1</span>')
    .replace(/\b([A-Za-z_]\w*)(?=\()/g, '<span class="tok-fn">$1</span>');
  return out;
}

function renderCode(code) {
  codeView.innerHTML = code
    .split("\n")
    .map((line) => `<span class="code-line">${highlightLine(line) || " "}</span>`)
    .join("");
  codeView.parentElement.scrollTop = 0;
}

function heatCells() {
  return Array.from({ length: 126 }, (_, i) => {
    const level = ((Math.sin(i * .42) + Math.cos(i * .17) + 2) * 1.25) | 0;
    return `<i class="l${Math.min(level, 4)}"></i>`;
  }).join("");
}

const previews = {
  "activity-graph": () => `
    <div class="demo-card demo-stack">
      <div class="demo-between"><span class="demo-title">Agent runs</span><span class="demo-mono">1,743 in 6 months</span></div>
      <div class="heat-grid">${heatCells()}</div>
      <div class="demo-mono" style="text-align:right">less　<span class="blue">■ ■ ■ ■</span>　more</div>
    </div>`,
  "agent-card": () => `
    <div class="demo-card demo-stack">
      <div class="demo-row">
        <span class="demo-icon">♙</span>
        <div style="min-width:0;flex:1"><div class="demo-title">Maintainer <span class="demo-mono">v1.4.0</span></div><div class="demo-muted">assistant-ui</div></div>
      </div>
      <div class="demo-muted">Works through the issue queue: reproduces the report, writes the fix, and opens the PR.</div>
      <div class="demo-stack" style="gap:5px">
        <div class="demo-row"><span class="demo-field demo-mono">triage</span><span class="demo-muted">Read an issue and label it</span></div>
        <div class="demo-row"><span class="demo-field demo-mono">repro</span><span class="demo-muted">Build a minimal reproduction</span></div>
        <div class="demo-row"><span class="demo-field demo-mono">patch</span><span class="demo-muted">Open a PR with the fix</span></div>
      </div>
      <div class="demo-between" style="border-top:1px solid var(--phone-border);padding-top:8px"><span class="demo-mono">agents.example.com/a2a</span><span class="demo-mono">opus</span></div>
      <button class="demo-button" data-action="connect">${connected ? '<span class="green">✓</span> Connected' : "Connect"}</button>
    </div>`,
  "agent-handoff": () => `
    <div class="demo-stack">
      <div class="demo-row"><span class="demo-chip">♙ Triage</span><span class="blue">→</span><span class="demo-chip" style="background:rgb(59 130 246/.12);color:#2563eb">♙ Maintainer</span></div>
      <div class="demo-muted">Triage reproduced the report and narrowed it to the converter, so the fix goes to the agent that can write and verify a patch.</div>
      <span class="demo-mono">carried over</span>
      <div class="demo-muted" style="border-left:1px solid var(--phone-border);padding-left:9px">The failing test and its output</div>
      <div class="demo-muted" style="border-left:1px solid var(--phone-border);padding-left:9px">The two files the reader already narrowed to</div>
    </div>`,
  "agent-plan": () => {
    const steps = ["Read existing composer state", "Design the draft store", "Wire runtime persistence", "Add regression tests", "Update the docs"];
    return `<div class="demo-stack">
      <div class="demo-between"><span class="demo-title">Composer draft</span><span class="demo-mono">${planStep} of 5</span></div>
      <div class="progress"><i style="width:${planStep * 20}%"></i></div>
      ${steps.map((s, i) => `<div class="demo-row" style="opacity:${i < planStep ? .45 : i === planStep ? 1 : .35}"><span>${i < planStep ? "✓" : i === planStep ? "◌" : "•"}</span><span>${s}${i === planStep ? '<small class="demo-muted" style="display:block">Keep pending changes local until they are ready.</small>' : ""}</span></div>`).join("")}
      <button class="demo-button" data-action="plan">Advance step</button>
    </div>`;
  },
  "agent-status": () => `
    <div class="status-pill">
      <span class="pulse"></span><span>Refactoring composer</span><span class="demo-mono">0:08</span><span>Ⅱ</span>
    </div>`,
  "approval-card": () => `
    <div class="demo-card demo-stack">
      <div class="demo-row"><span class="demo-icon">›_</span><div><div class="demo-title">Run command</div><div class="demo-muted">The agent wants to run a shell command</div></div></div>
      <div class="demo-field demo-mono">pnpm vitest run --changed</div>
      ${approvalState === "request" ? `<div class="demo-row" style="justify-content:flex-end"><button class="demo-button ghost" data-action="deny">Deny</button><button class="demo-button ghost" data-action="allow">Always allow</button><button class="demo-button" data-action="allow">Allow once</button></div>` : `<div class="demo-row" style="justify-content:flex-end"><span class="${approvalState === "done" ? "green" : "red-text"}">${approvalState === "running" ? "◌ Approved, running" : approvalState === "done" ? "✓ Finished" : "× Denied"}</span></div>`}
    </div>`,
  "artifact-card": () => `
    <div class="demo-card demo-row">
      <span class="demo-icon">▤</span><div style="flex:1"><div class="demo-title">Draft persistence RFC</div><div class="demo-mono">Writing · ${artifactWords} words</div></div><span>↗</span>
    </div>`,
  "assistant-modal": () => `
    <div style="width:100%;height:100%;position:relative">
      ${modalOpen ? `<div class="demo-card" style="height:300px;display:flex;flex-direction:column;padding:0;overflow:hidden;box-shadow:0 14px 35px -20px #000">
        <div class="demo-between" style="height:36px;padding:0 10px;border-bottom:1px solid var(--phone-border)"><span class="demo-title">New Chat</span><span>↶　＋</span></div>
        <div style="padding:14px"><div class="demo-title" style="font-size:16px;margin-top:20px">How can I help?</div><div class="demo-muted">Ask a question or describe what you want to build.</div></div>
        <div class="demo-field" style="margin:auto 10px 10px;height:70px"><span class="demo-muted">Send a message…</span></div>
      </div>` : ""}
      <button class="demo-button" data-action="modal" style="position:absolute;right:4px;bottom:4px;width:36px;height:36px;padding:0">${modalOpen ? "⌄" : "♙"}</button>
    </div>`,
  "assistant-sidebar": () => `
    <div style="width:100%;height:330px;display:grid;grid-template-columns:1fr 4px .65fr;border:1px solid var(--phone-border)">
      <div style="display:grid;place-items:center"><span class="demo-muted">Your application</span></div>
      <div style="background:var(--phone-border)"></div>
      <div class="demo-stack" style="padding:10px"><span class="demo-title">How can I help?</span><span class="demo-muted">Ask a question or describe what you want to build.</span><div class="demo-field" style="margin-top:auto">Send a message…</div></div>
    </div>`,
  "attachment": () => `
    <div class="demo-field demo-stack" style="border:1px solid var(--phone-border);border-radius:18px;padding:8px">
      <div class="demo-row"><span class="attachment-tile image"><b>×</b></span><span class="attachment-tile">▤<b>×</b></span></div>
      <div class="demo-muted" style="padding:5px">Send a message…</div>
      <div class="demo-between"><button class="demo-button ghost" style="font-size:15px;padding:0 8px">＋</button><button class="demo-button" style="width:28px;height:28px;padding:0">↑</button></div>
    </div>`,
};

function bindPreviewActions() {
  previewRoot.querySelectorAll("[data-action]").forEach((button) => {
    button.addEventListener("click", () => {
      const action = button.dataset.action;
      if (action === "connect") connected = true;
      if (action === "plan") planStep = (planStep + 1) % 6;
      if (action === "allow") {
        approvalState = "running";
        setTimeout(() => { approvalState = "done"; renderPreview(); }, 1200);
      }
      if (action === "deny") approvalState = "denied";
      if (action === "modal") modalOpen = !modalOpen;
      renderPreview();
    });
  });
}

function renderPreview() {
  const component = components[currentIndex];
  previewRoot.innerHTML = previews[component.slug]?.() ?? "";
  bindPreviewActions();
}

function renderNav(filter = "") {
  componentNav.innerHTML = "";
  components.forEach((component, index) => {
    if (!component.title.toLowerCase().includes(filter.toLowerCase())) return;
    const button = document.createElement("button");
    button.className = `nav-item${index === currentIndex ? " active" : ""}`;
    button.innerHTML = `<span>${component.title}</span><span>→</span>`;
    button.addEventListener("click", () => selectComponent(index));
    componentNav.append(button);
  });
}

function selectComponent(index, updateHash = true) {
  currentIndex = (index + components.length) % components.length;
  const component = components[currentIndex];
  $("#componentTitle").textContent = component.title;
  $("#componentDescription").textContent = component.description;
  $("#positionText").textContent = `${String(currentIndex + 1).padStart(2, "0")} / ${String(components.length).padStart(2, "0")}`;
  $("#fileName").textContent = component.file;
  $("#sourceFact").textContent = component.file;
  renderCode(component.code);
  renderPreview();
  renderNav($("#filterInput").value);
  if (updateHash) history.replaceState(null, "", `#${component.slug}`);
}

function renderSearchResults(query = "") {
  const results = components.filter((component) =>
    `${component.title} ${component.description}`.toLowerCase().includes(query.toLowerCase()),
  );
  $("#dialogResults").innerHTML = results
    .map((item) => `<button class="search-result" data-slug="${item.slug}">${item.title}<small>${item.description}</small></button>`)
    .join("");
  $("#dialogResults").querySelectorAll("button").forEach((button) => {
    button.addEventListener("click", () => {
      selectComponent(components.findIndex((item) => item.slug === button.dataset.slug));
      $("#searchDialog").close();
    });
  });
}

$("#filterInput").addEventListener("input", (event) => renderNav(event.target.value));
$("#previousButton").addEventListener("click", () => selectComponent(currentIndex - 1));
$("#nextButton").addEventListener("click", () => selectComponent(currentIndex + 1));
$("#runButton").addEventListener("click", () => {
  artifactWords = Math.min(214, artifactWords + 19);
  phone.animate([{ transform: "scale(.985)" }, { transform: "scale(1)" }], { duration: 260 });
  renderPreview();
});
$("#resetButton").addEventListener("click", () => {
  connected = false; approvalState = "request"; planStep = 1; artifactWords = 138; modalOpen = true;
  renderPreview();
});
$("#themeButton").addEventListener("click", () => {
  const dark = document.documentElement.dataset.theme === "dark";
  document.documentElement.dataset.theme = dark ? "light" : "dark";
});
$("#previewThemeButton").addEventListener("click", () => {
  previewDark = !previewDark;
  phone.classList.toggle("dark", previewDark);
});
$("#copyButton").addEventListener("click", async () => {
  await navigator.clipboard.writeText(components[currentIndex].code);
  $("#copyButton").textContent = "Copied";
  setTimeout(() => { $("#copyButton").textContent = "Copy code"; }, 1200);
});
$("#searchButton").addEventListener("click", () => {
  renderSearchResults();
  $("#searchDialog").showModal();
  $("#dialogInput").focus();
});
$("#dialogInput").addEventListener("input", (event) => renderSearchResults(event.target.value));
document.addEventListener("keydown", (event) => {
  if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === "k") {
    event.preventDefault();
    renderSearchResults();
    $("#searchDialog").showModal();
    $("#dialogInput").focus();
  }
});

const hashIndex = components.findIndex((component) => `#${component.slug}` === location.hash);
selectComponent(hashIndex >= 0 ? hashIndex : 0, false);