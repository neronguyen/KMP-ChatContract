# Master Architecture Flows Registry

This directory serves as the **Single Source of Truth (SSOT)** for cross-cutting communication protocols, failure mode recovery sequences, and state lifecycles across the client-server ecosystem.

In accordance with `docs/PHILOSOPHY.md` Section 5, every change touching a communication flow or state machine must specify its to-be architecture diagram before implementation.

---

## 1. Flow Inventory

| Flow Name | Diagram Type | Status | Specification (JSON SSOT) | Interactive Viewer (HTML) | Static Preview (Dark / Light) | Owning Track | Asserting Test Suite |
|---|---|---|---|---|---|---|---|
| **WebSocket Lifecycle & Backoff** | `sequence` | `[PLANNED]` | [`ws-lifecycle.sequence.archify.json`](../specifications/flows/ws-lifecycle.sequence.archify.json) | [`ws-lifecycle.html`](../interactive/flows/ws-lifecycle.html) | [Dark](../assets/ws-lifecycle.png) / [Light](../assets/ws-lifecycle.light.png) | Track A | `:core:network` MockWebServer WS Suite & `RealtimeConnectionManagerTest` |
| **Single-Flight Token Refresh** | `sequence` | `[AS-IS]` | [`token-refresh.sequence.archify.json`](../specifications/flows/token-refresh.sequence.archify.json) | [`token-refresh.html`](../interactive/flows/token-refresh.html) | [Dark](../assets/token-refresh.png) / [Light](../assets/token-refresh.light.png) | Track A | `TokenAuthenticatorTest` & `RetrofitAuthNetworkTest` |
| **Optimistic Send & Idempotency** | `sequence` | `[AS-IS]` | [`optimistic-send.sequence.archify.json`](../specifications/flows/optimistic-send.sequence.archify.json) | [`optimistic-send.html`](../interactive/flows/optimistic-send.html) | [Dark](../assets/optimistic-send.png) / [Light](../assets/optimistic-send.light.png) | Track A / B | `ChatMessageRepositoryTest` & Backend `ChatWebSocketHandlerTest` |

---

## 2. Status Conventions

- `[PLANNED]`: Design-first specification of planned state lifecycles, failure paths, or wire interactions. Authored before code changes; tests in upcoming tracks assert against this exact sequence.
- `[AS-IS]`: Verified runtime sequence matching committed production code and passing headless test assertions.

---

## 3. Authoring & Verification Commands

Flow diagrams are rendered using [archify](https://github.com/tt-a1i/archify):

```bash
# Render interactive standalone HTML
node ~/.gemini/config/skills/archify/bin/archify.mjs deliver sequence \
  docs/architecture/specifications/flows/<name>.sequence.archify.json \
  docs/architecture/interactive/flows/<name>.html \
  --quality showcase --json

# Run headless artifact integrity and layout checks
node ~/.gemini/config/skills/archify/bin/archify.mjs check \
  docs/architecture/interactive/flows/<name>.html --json

# Static Image Assets (for README and Markdown embedding)
# Whenever diagrams are embedded in README.md or markdown docs, maintain dual-theme static PNGs:
# docs/architecture/assets/<name>.png (dark mode)
# docs/architecture/assets/<name>.light.png (light mode)
```
