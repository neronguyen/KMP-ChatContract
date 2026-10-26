# KMP-ChatContract

Pure-Kotlin multi-module contract repository containing shared DTOs, validation rules, and WebSocket message models for
NeroChat (shared between the Spring Boot backend and Android/Kotlin clients).

## Modules

### Contract
- `:contract:common`: Shared error models (`ServerError`, `CommonServerError`, `ApiErrorResponse`).
- `:contract:auth`: Authentication DTOs (`UserDto`, `AuthenticatedUserDto`) and error models (`AuthServerError`).
- `:contract:chat`: Chat DTOs (`ChatRoomDto`, `ChatParticipantDto`, `ChatMessageDto`, `ChatMessageSliceDto`, `ChatRoomType`), error models (`ChatServerError`), and WebSocket contracts (`WsClientMessage`, `WsServerEvent`).

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
