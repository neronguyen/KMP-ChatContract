# KMP-ChatContract

Pure-Kotlin multi-module contract repository containing shared wire DTOs, sealed endpoint error contracts, validation
rules, and WebSocket message models for the end-to-end chat project.

> **Architecture & Philosophy**
> - Read [docs/PHILOSOPHY.md](docs/PHILOSOPHY.md) for the architectural principles, decision filters, and AI agent
    governance rules governing this project.
> - **Medium
    Series**: [Part 1 — End-to-End Type Safety: Sharing Pure Kotlin Contracts Between Android and Spring Boot](https://medium.com/@neronguyen/end-to-end-type-safety-sharing-pure-kotlin-contracts-between-android-and-spring-boot-part-1-8c45e3ab6720)

## Related Repositories

| Repository                                                                           | Role                                                                    | C4 Architecture Level                                   | Tech Stack                                                                    |
|:-------------------------------------------------------------------------------------|:------------------------------------------------------------------------|:--------------------------------------------------------|:------------------------------------------------------------------------------|
| **[KMP-ChatContract](https://github.com/neronguyen/KMP-ChatContract)** *(this repo)* | Compile-time wire schema, error contracts, and shared validators        | **Level 1** (System Context) & **Level 2** (Containers) | Pure Kotlin 2.4, `kotlinx.serialization`, `kotlinx-datetime`                  |
| **[KMP-ChatBackend](https://github.com/neronguyen/KMP-NeroChatBackend)**             | Modular monolith REST & WebSocket server                                | **Level 3** (Backend Components)                        | Kotlin, Spring Boot 4, PostgreSQL, Redis (Redisson), RabbitMQ                 |
| **[Android-ChatApp](https://github.com/neronguyen/Android-NeroChatApp)**             | Native Android client with offline-first persistence and real-time sync | **Level 3** (Android Components)                        | Jetpack Compose, Navigation 3, Retrofit, OkHttp, Room, DataStore, Hilt, Arrow |

---

## System Architecture (C4 Model)

> **Interactive Architecture Viewers** (clone locally to explore with pan/zoom, search, and source links):
> - **Level 1 — System Context**: [
    `docs/architecture/interactive/c4-system-context.html`](docs/architecture/interactive/c4-system-context.html)
> - **Level 2 — Containers**: [
    `docs/architecture/interactive/c4-containers.html`](docs/architecture/interactive/c4-containers.html)
> - **Level 3 — Component Diagrams**: See [`KMP-ChatBackend`](https://github.com/neronguyen/KMP-NeroChatBackend) and [
    `Android-ChatApp`](https://github.com/neronguyen/Android-NeroChatApp)

### Level 1 — System Context

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="docs/architecture/assets/SystemContext.png">
  <source media="(prefers-color-scheme: light)" srcset="docs/architecture/assets/SystemContext.light.png">
  <img alt="Chat System Context (C4 Level 1)" src="docs/architecture/assets/SystemContext.png">
</picture>

### Level 2 — Containers

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="docs/architecture/assets/Containers.png">
  <source media="(prefers-color-scheme: light)" srcset="docs/architecture/assets/Containers.light.png">
  <img alt="Chat Container Architecture (C4 Level 2)" src="docs/architecture/assets/Containers.png">
</picture>

---

## Modules

### Contract (`:contract:*`)

- `:contract:common`: Shared error primitives (`ErrorCode`, `ServerError`, `ErrorContract`, `CommonServerError`,
  `ApiErrorDto`).
- `:contract:auth`: Authentication DTOs (`UserDto`, `AuthenticatedUserDto`, request models) and per-endpoint error
  contracts nested under `AuthServerError` (`Register`, `Login`, `RefreshToken`, `Logout`, `VerifyEmail`,
  `ResetPassword`, `ChangePassword`).
- `:contract:chat`: Chat DTOs (`ChatRoomDto`, `ChatParticipantDto`, `ChatMessageDto`, `ChatMessageSliceDto`,
  `ChatRoomType`), per-endpoint error contracts nested under `ChatServerError` (`GetParticipant`, `GetRoom`,
  `GetMessages`, `CreateRoom`, `AddParticipants`, `LeaveRoom`, `DeleteMessage`, `WebSocket`), and WebSocket contracts
  (`WsClientMessage`, `WsServerEvent`).

### Validation (`:validation:*`)

- `:validation:common`: Shared validation primitives (`Validatable`, `ValidationError`, `EmailValidator`,
  `UuidValidator`).
- `:validation:auth`: Authentication request validators (`AuthValidator`, `PasswordValidator`, `AuthValidationError`).
- `:validation:chat`: Chat request validators (`ChatValidator`, `ChatValidationError`).

---

## Error Handling Architecture

The library defines a flat-wire, type-safe, reflection-free error contract system:

1. **Wire DTO**: `ApiErrorDto` wraps a flat `@Serializable @JvmInline value class ErrorCode(val value: String)`.
2. **Per-Endpoint Contracts**: Each endpoint defines a sealed interface (`AuthServerError.Login`,
   `ChatServerError.CreateRoom`, etc.) with a companion `ErrorContract` cataloging its entries.
3. **Client Classification**: Clients decode error bodies into `ApiErrorDto` and resolve the wire code against the
   endpoint's contract and cross-cutting common errors:
   ```kotlin
   val errorDto = json.decodeFromString<ApiErrorDto>(jsonBody)
   val businessError: AuthServerError.Login? = AuthServerError.Login.fromCode(errorDto.error)
   val commonError: CommonServerError? = CommonServerError.fromCode(errorDto.error)

   when {
       businessError != null -> when (businessError) {
           AuthServerError.InvalidCredentials -> showCredentialsError()
           AuthServerError.EmailNotVerified -> showVerificationRequired()
       }
       commonError != null -> handleCommonError(commonError)
       else -> handleUnknownError(errorDto.error)
   }
   ```
4. **Backend Enforcement**: Backend services and `@ControllerAdvice` handlers map domain exceptions directly to
   `ServerError` instances (`ApiErrorDto(error = AuthServerError.InvalidCredentials, ...)`), while guardrail tests
   verify contract completeness, uniqueness, and wire snapshots.

---

## Verification & Local Development

Run all contract and validation test suites (including reflection completeness and golden wire-format guardrails):

```bash
./gradlew check
```

### Usage via Gradle Composite Build

Both `KMP-ChatBackend` and `Android-ChatApp` automatically substitute published contract coordinates with this local
repository when cloned as a sibling directory (`../KMP-ChatContract`):

```kotlin
val contractRepoDir = file("../KMP-ChatContract")
if (contractRepoDir.exists()) {
    includeBuild(contractRepoDir) {
        dependencySubstitution {
            substitute(module("io.github.neronguyenvn.nerochat.contract:common")).using(project(":contract:common"))
            substitute(module("io.github.neronguyenvn.nerochat.contract:auth")).using(project(":contract:auth"))
            substitute(module("io.github.neronguyenvn.nerochat.contract:chat")).using(project(":contract:chat"))
            substitute(module("io.github.neronguyenvn.nerochat.validation:common")).using(project(":validation:common"))
            substitute(module("io.github.neronguyenvn.nerochat.validation:auth")).using(project(":validation:auth"))
            substitute(module("io.github.neronguyenvn.nerochat.validation:chat")).using(project(":validation:chat"))
        }
    }
}
```
