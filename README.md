# KMP-NeroChatContract

Pure-Kotlin multi-module contract repository containing shared DTOs and WebSocket message models for
NeroChat (shared between the Spring Boot backend and Android/Kotlin clients).

## Modules

- `:contract-auth`: Authentication DTOs (`UserDto`, `AuthenticatedUserDto`).
- `:contract-chat`: Chat DTOs (`ChatRoomDto`, `ChatParticipantDto`, `ChatMessageDto`,
  `ChatMessageSliceDto`, `ChatRoomType`) and WebSocket contracts (`WsClientMessage`,
  `WsServerEvent`).

## Usage via Gradle Composite Build

In your `settings.gradle.kts`:

```kotlin
val contractRepoDir = file("../KMP-NeroChatContract")
if (contractRepoDir.exists()) {
    includeBuild(contractRepoDir) {
        dependencySubstitution {
            substitute(module("io.github.neronguyenvn.nerochat.contract:contract-auth")).using(
                project(":contract-auth")
            )
            substitute(module("io.github.neronguyenvn.nerochat.contract:contract-chat")).using(
                project(":contract-chat")
            )
        }
    }
}
```
