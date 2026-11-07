# Agent Instructions — KMP-ChatContract

## 1. Philosophy & Governance Gate
Before proposing any plan or writing code, read and strictly follow:
1. `docs/PHILOSOPHY.md` (especially Section 3: Decision Filter & Boundary Rules and Section 5: AI & Agentic Governance).
2. Architecture specifications:
   - Level 1 System Context: `docs/architecture/specifications/system-context.archify.json` (Viewer: `docs/architecture/interactive/c4-system-context.html`)
   - Level 2 Containers: `docs/architecture/specifications/containers.archify.json` (Viewer: `docs/architecture/interactive/c4-containers.html`)
3. Baseline Audit Snapshot: `docs/audit/2026-11-baseline-contract.md`

Every implementation plan must satisfy the **Mandatory Agent Planning Protocol** defined in `docs/PHILOSOPHY.md` Section 5 before execution (read it; it is not restated here).

### 1.1 Design-First Flow Gate (Tooling Binding for Protocol Step 3)
To satisfy Step 3 (`To-Be Flow & Lifecycle Spec`) of the Planning Protocol:
1. Draft a Mermaid sketch in the implementation plan for review, then author the canonical spec in `docs/architecture/specifications/flows/<name>.<type>.archify.json` (`<type>` = `sequence` | `lifecycle`) and register it in `docs/architecture/flows/README.md`.
2. Mark `meta.title` and the registry row `[PLANNED]` before writing code.
3. **Completion Invariant (No Dangling `[PLANNED]`)**: After Step 4 verification passes, flip `[PLANNED]` → `[AS-IS]` in both the `.archify.json` spec and `docs/architecture/flows/README.md`, re-render interactive HTML and image assets, and verify that no in-scope flow remains `[PLANNED]`.
4. Render and verify the interactive HTML diagram via:
   `node ~/.gemini/config/skills/archify/bin/archify.mjs deliver <type> <input.json> <output.html> --quality showcase --json && node ~/.gemini/config/skills/archify/bin/archify.mjs check <output.html> --json`
5. **Static Image Assets**: For diagrams embedded in `README.md` or markdown documentation, export and update dark and light static image assets in `docs/architecture/assets/<name>.png` and `docs/architecture/assets/<name>.light.png`.

### 1.2 Module Contracts & Design Registry (EGM)
1. **Design Registry**: Approved technical design documents live in `docs/designs/` and are indexed in `docs/designs/SITEMAP.md`. In an execution session, follow the approved design's implementation plan and verify any impacted flows against Section 1.1.
2. **Module Contract Invariant**: Every Gradle module maintains a machine-verifiable contract in its `README.md` (`Interface`, `Invariants`, `Side Effects`, `Verification`). Before modifying a module, inspect its `README.md`. Any change to public symbols, invariants, side effects, or dependencies must update that module's `README.md` (and parent blueprint if boundaries change) in the same changeset.

---

## 2. Module Map & Architectural Invariants

### 2.1 Allowed Dependency Graph
```text
:contract:common (zero internal deps)

:contract:auth ──api──> :contract:common
      │
      └──api──> :validation:auth ──api──> :validation:common

:contract:chat ──api──> :contract:common
      │
      └──api──> :validation:chat ──api──> :validation:common
```

- `:contract:common`: Zero internal dependencies. Shared error models (`ErrorCode`, `ServerError`, `ErrorContract`, `CommonServerError`, `ApiErrorDto`).
- `:validation:common`: Zero internal dependencies. Base validation primitives (`ValidationError`, `Validatable`, `EmailValidator`, `UuidValidator`).
- `:validation:auth`: Depends on `:validation:common`. Request and form validators (`AuthValidator`, `PasswordValidator`, `AuthValidationError`).
- `:validation:chat`: Depends on `:validation:common`. Chat validators (`ChatValidator`, `ChatValidationError`).
- `:contract:auth`: Depends on `:contract:common` and `:validation:auth`. Auth wire DTOs and `AuthServerError` sealed interfaces.
- `:contract:chat`: Depends on `:contract:common` and `:validation:chat`. Chat wire DTOs, WebSocket frame types (`WsClientMessage`, `WsServerEvent`), and `ChatServerError` sealed interfaces.

### 2.2 Boundary Invariants & Hard Rules
1. **Zero Framework Leakage**: No Spring, Android, Jackson, Retrofit, or database dependencies. Pure Kotlin standard library + `kotlinx.serialization` only.
2. **KMP Readiness (No `java.*` Standard Library Calls)**: Main sources must not call JVM-only standard library APIs (e.g. `java.lang.String.codePointCount`, `java.util.*`) so future migration to `org.jetbrains.kotlin.multiplatform` remains trivial.
3. **Flat `ErrorCode` Wire Format**: All `ServerError` instances must be payload-free `data object` singletons whose `.code` is `@JvmInline value class ErrorCode(val value: String)`. They serialize as flat JSON strings (e.g. `"AuthServerError.InvalidCredentials"`), never nested JSON objects.
4. **Golden Snapshot Integrity**: Every `ServerError` entry must be cataloged in its companion `ErrorContract` and verified against the golden error-code files (`common-error-codes-golden.txt`, `auth-error-codes-golden.txt`, `error-codes-golden.txt`).
5. **Request DTO Invariants**: All request DTOs must implement `Validatable<E>` and redact sensitive fields (passwords, tokens) in `toString()`.
6. **WebSocket Frame Discriminator**: Sealed hierarchies `WsClientMessage` and `WsServerEvent` must use `@JsonClassDiscriminator("type")`.

---

## 3. Verified Development & Verification Commands

All agent verification must be performed using these verified-green commands:

| Task / Purpose | Verified Command |
|---|---|
| Run all checks & tests | `./gradlew check --rerun-tasks` |
| Run all unit tests | `./gradlew test --rerun-tasks` |
| Check binary compatibility | `./gradlew apiCheck` |
| Dump public binary API baseline | `./gradlew apiDump` |
| List runnable tasks | `./gradlew tasks --all` |

*Note: `./gradlew` commands require access to host JDK 21+ (`toolchainVersion=21`). Never run publish or deployment commands.*


