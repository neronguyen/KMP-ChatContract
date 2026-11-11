# Agent Instructions — KMP-ChatContract

## 1. Philosophy & Documentation Governance Gate

Before proposing any plan or writing code, read and follow:

1. **Global Agent Rules**: `~/.gemini/config/AGENTS.md` (Git sign-off/attribution, unified EGM + Archify lifecycle,
   post-implementation scaffolding pruning, and Archify/Chrome CLI commands).
2. **Project Philosophy**: [`docs/PHILOSOPHY.md`](docs/PHILOSOPHY.md) (especially Section 3: Decision Filter & Boundary
   Rules and Section 5: AI & Agentic Governance).
3. **Unified Repository Documentation Index**: [`docs/README.md`](docs/README.md) (Single index for C4 Level 1/2 specs,
   cross-repository wire flow specs, and EGM design specs in `docs/designs/`).
4. **Module Contract Invariant**: Every Gradle module maintains a machine-verifiable contract in its
   `<module>/README.md` (`Interface`, `Invariants`, `Side Effects`, `Verification`). Inspect a module's `README.md`
   before modifying it, and update it (plus root `README.md` if topology changes) in the same changeset.

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

| Task / Purpose | Tier | Verified Command |
|---|---|---|
| **Fast Inner-Loop Feedback** | Tier 1 (Iterative) | `./scripts/verify.sh fast` |
| **Strict Clean Verification Gate** | Tier 2 (Pre-Commit / Gate) | `./scripts/verify.sh clean` |
| Fast unit tests across modules | Direct Gradle | `./gradlew test` |
| Check binary compatibility | Direct Gradle | `./gradlew apiCheck` |
| Dump public binary API baseline | Direct Gradle | `./gradlew apiDump` |
| List runnable tasks | Utility | `./gradlew tasks --all` |

> [!IMPORTANT]
> **Verification Tier Policy**:
> - Use **Tier 1 (`fast`)** for rapid iterative feedback during development loops.
> - **MUST** run and pass **Tier 2 (`clean`)** before committing any changes or flipping any flow spec from `[PLANNED]`
    to `[AS-IS]`.
