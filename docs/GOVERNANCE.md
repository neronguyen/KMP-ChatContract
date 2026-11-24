# NeroChat — Ecosystem Agent Governance & Lifecycle

*Operational rules shared across `KMP-ChatContract`, `KMP-ChatBackend`, and `Android-ChatApp`.*
*For durable architectural principles and the Mandatory Agent Planning Protocol, see [`PHILOSOPHY.md`](./PHILOSOPHY.md).*

---

## 1. EGM Applicability Gate

Use the **Elephant-and-Goldfish Model (EGM)** to author or update a technical design document (`docs/designs/*.md`) **only when** a task introduces or changes:
- A new cross-module or cross-repo boundary, wire contract, or dependency topology
- A state lifecycle, concurrency/retry invariant, or communication protocol
- A new verification harness, static guardrail category, or UI architecture rule

**Skip EGM design doc authoring** (implement and verify directly) for:
- Expanding test coverage on an established pattern (e.g., adding tests to `*FeatureTest.kt` or `*ViewModelTest.kt`)
- Localized bug fixes or internal refactors that preserve existing boundaries and contracts
- Documentation syncs or routine dependency/CI maintenance

---

## 2. Unified EGM + Archify Lifecycle

1. **Design Placement**:
   - **Cross-repo** specs (`KMP-ChatContract` + Backend/Android) live in `KMP-ChatContract/docs/designs/<slug>.md`.
   - **Single-repo** specs live in `<repo>/docs/designs/<slug>.md`.
   - Register every design document in `<repo>/docs/README.md` (and in peer repos' `docs/README.md` if cross-repo).
2. **Design-First Flow Gate (`[PLANNED]` → `[AS-IS]`)**:
   - Before writing implementation code for any change touching a state lifecycle, failure mode, or communication flow, author or update the canonical Archify spec in `docs/architecture/specifications/flows/<name>.<type>.archify.json` (`<type>` = `sequence` | `lifecycle`) with `meta.title` and its `docs/README.md` row marked **`[PLANNED]`**.
   - **Completion Invariant (No Dangling `[PLANNED]`)**: Only after deterministic verification passes, flip `[PLANNED]` → `[AS-IS]` in both the `.archify.json` spec and `docs/README.md`, flip the design doc status to `Implemented`, and re-render interactive HTML + dual-theme PNG assets.
3. **Module Contract Invariant**:
   - Inspect `<module>/README.md` (`Interface`, `Invariants`, `Side Effects`, `Verification`) before modifying any Gradle module, and update it (plus root `README.md` and C4 Level 3 `.archify.json` if module topology changes) in the same changeset.

---

## 3. Post-Implementation Scaffolding Pruning

When flipping a design document from `Draft` / `In Review` to `Status: Implemented`:
- **Prune** transient execution scaffolding that will rot: `Detailed Implementation Plan`, `Verification Plan` step checklists, temporary migration logs, and phase-by-phase file edit lists.
- **Retain** durable architectural knowledge: `Overview`, `The Problem`, `Goals`, `Non-Goals`, `Critical User Journeys (CUJs)`, `Technical Plan (Architecture)`, `Alternatives Considered`, `Non-Functional Requirements`, and `Risks & Mitigations`.

---

## 4. Archify & Headless Chrome CLI

1. **Primary Command (Always Prefer)**:
   - `./scripts/sync-archify.sh` — auto-detects modified `.archify.json` specs, delivers interactive HTML, validates layout, and exports dark/light PNGs.
   - `./scripts/sync-archify.sh --check` — CI freshness check (enforced by `.github/workflows/ci.yml`).
2. **Manual Fallback** (only when debugging individual specs):
   ```bash
   node ~/.gemini/config/skills/archify/bin/archify.mjs deliver <type> <input.json> <output.html> --quality showcase --json
   node ~/.gemini/config/skills/archify/bin/archify.mjs check <output.html> --json
   ```
3. **Headless Chrome Resolution**: `sync-archify.sh` resolves `flatpak run --filesystem=host com.google.Chrome`, `~/.local/bin/google-chrome`, `google-chrome`, or `chromium`. If none is installed locally, interactive HTML generation still runs, while PNG export requires a Chromium/Chrome binary.

---

## 5. Ticket & Traceability Convention (Linear SSOT)

1. **Canonical ID**: Use the Linear issue key `CHAT-<num>` exclusively across all three repositories
   (`KMP-ChatContract`, `KMP-ChatBackend`, `Android-ChatApp`). Never embed manual `[P0-1]` or `(A4)` prefixes in issue
   titles.
2. **Git Branch** *(identical across all repos touched by a cross-repo issue)*: `<type>/chat-<num>-<short-slug>` (e.g.,
   `fix/chat-5-ws-cache-heartbeat`, `infra/chat-25-backend-docker`).
3. **Commit Message**: Conventional Commit + `(CHAT-<num>)` suffix, e.g.,
   `fix(chat): guard offline room cache (CHAT-5)`.
4. **Pull Request**: `[CHAT-<num>] <summary>` with `Closes CHAT-<num>` in the PR body (or `Refs CHAT-<num>` on upstream
   PRs and `Closes CHAT-<num>` on the final downstream PR).
5. **EGM Design Docs**: Include `| **Linear Ticket** | [CHAT-<num>](https://linear.app/neronguyen/issue/CHAT-<num>) |`
   in the design document metadata table.
