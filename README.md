# KMP-ChatContract

Pure-Kotlin multi-module contract repository containing shared DTOs, validation rules, and WebSocket message models for
NeroChat (shared between the Spring Boot backend and Android/Kotlin clients).

## Modules

### Contract
- `:contract:common`: Shared error models (`ErrorCode`, `ServerError`, `ErrorContract`, `CommonServerError`, `ApiErrorDto`).
- `:contract:auth`: Authentication DTOs (`UserDto`, `AuthenticatedUserDto`) and per-endpoint error contracts nested under `AuthServerError` namespace (`Register`, `Login`, `RefreshToken`, `Logout`, `VerifyEmail`, `ResetPassword`, `ChangePassword`).
- `:contract:chat`: Chat DTOs (`ChatRoomDto`, `ChatParticipantDto`, `ChatMessageDto`, `ChatMessageSliceDto`, `ChatRoomType`), per-endpoint error contracts nested under `ChatServerError` namespace (`GetParticipant`, `GetRoom`, `GetMessages`, `CreateRoom`, `AddParticipants`, `LeaveRoom`, `DeleteMessage`, `WebSocket`), and WebSocket contracts (`WsClientMessage`, `WsServerEvent`).

### Error Handling Architecture

The library defines a flat-wire, type-safe, reflection-free error contract system:

1. **Wire DTO**: `ApiErrorDto` wraps a flat `@Serializable @JvmInline value class ErrorCode(val value: String)`.
2. **Per-Endpoint Contracts**: Each endpoint defines a sealed interface (`AuthServerError.Login`, `ChatServerError.CreateRoom`, etc.) with a companion `ErrorContract` cataloging its entries.
3. **Client Classification**: Clients safely decode into `ApiErrorDto` and resolve the error against the endpoint's contract and common errors:
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
4. **Backend Enforcement**: Backend services return sealed results per endpoint contract (e.g. `Either<AuthServerError.Register, T>`), mapped directly to HTTP responses via `error.code`, while cross-cutting exceptions map to `CommonServerError` in `@ControllerAdvice`.

### Validation
- `:validation:common`: Shared validators (`EmailValidator`, `UuidValidator`, `ValidationError`).
- `:validation:auth`: Authentication validation (`AuthValidator`, `PasswordValidator`, `AuthValidationError`).
- `:validation:chat`: Chat validation (`ChatValidator`, `ChatValidationError`).

## Usage via Gradle Composite Build

In your `settings.gradle.kts`:

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
