# Baseline Audit — `contract` (`KMP-ChatContract`)

- **Date**: `2026-11-02`
- **ROLE**: `contract`
- **Repository**: `/home/nero/Projects/KMP-ChatContract`
- **Git Commit Hash**: `d98170d33843a0a0cb8af648886b43fb42b686e7`
- **Branch**: `main`
- **Working Tree Clean**: `true` (`git status --short` returned empty output prior to creating this report)

---

## 1. Module map & boundaries

### 1.1 Declared Gradle Subprojects & Coordinates

Defined in `settings.gradle.kts:15-22` (`rootProject.name = "KMP-ChatContract"`) and published via `build.gradle.kts:10-23`:
- Default version: `0.1.0` (`build.gradle.kts:10-12`, overridden via `-Pversion=...`).
- Note: `gradle/libs.versions.toml:4` declares `contract = "0.0.1"`, which is unreferenced anywhere in the repo.

| Gradle Path | Maven Coordinates (`group:artifact:version`) | Plugins (`build.gradle.kts`) | Main Dependencies | Test Dependencies |
| :--- | :--- | :--- | :--- | :--- |
| `:validation:common` | `io.github.neronguyenvn.nerochat.validation:common:0.1.0` | `java-library`, `org.jetbrains.kotlin.jvm` (`validation/common/build.gradle.kts:1-4`) | *(none)* | `kotlin("test")` (`validation/common/build.gradle.kts:11`) |
| `:validation:auth` | `io.github.neronguyenvn.nerochat.validation:auth:0.1.0` | `java-library`, `org.jetbrains.kotlin.jvm` (`validation/auth/build.gradle.kts:1-4`) | `api(projects.validation.common)` (`validation/auth/build.gradle.kts:11`) | `kotlin("test")` (`validation/auth/build.gradle.kts:13`) |
| `:validation:chat` | `io.github.neronguyenvn.nerochat.validation:chat:0.1.0` | `java-library`, `org.jetbrains.kotlin.jvm` (`validation/chat/build.gradle.kts:1-4`) | `api(projects.validation.common)` (`validation/chat/build.gradle.kts:11`) | `kotlin("test")` (`validation/chat/build.gradle.kts:13`) |
| `:contract:common` | `io.github.neronguyenvn.nerochat.contract:common:0.1.0` | `java-library`, `org.jetbrains.kotlin.jvm`, `org.jetbrains.kotlin.plugin.serialization` (`contract/common/build.gradle.kts:1-5`) | `api(projects.validation.common)`, `implementation(libs.kotlinx.serialization.json)` (`contract/common/build.gradle.kts:11-14`) | `kotlin("test")`, `kotlin("reflect")` (`contract/common/build.gradle.kts:15-16`) |
| `:contract:auth` | `io.github.neronguyenvn.nerochat.contract:auth:0.1.0` | `java-library`, `org.jetbrains.kotlin.jvm`, `org.jetbrains.kotlin.plugin.serialization` (`contract/auth/build.gradle.kts:1-5`) | `api(projects.contract.common)`, `api(projects.validation.auth)`, `implementation(libs.kotlinx.serialization.json)` (`contract/auth/build.gradle.kts:11-15`) | `kotlin("test")`, `kotlin("reflect")` (`contract/auth/build.gradle.kts:16-17`) |
| `:contract:chat` | `io.github.neronguyenvn.nerochat.contract:chat:0.1.0` | `java-library`, `org.jetbrains.kotlin.jvm`, `org.jetbrains.kotlin.plugin.serialization` (`contract/chat/build.gradle.kts:1-5`) | `api(projects.contract.common)`, `api(projects.validation.chat)`, `implementation(libs.kotlinx.serialization.json)` (`contract/chat/build.gradle.kts:11-15`) | `testImplementation(projects.contract.auth)`, `kotlin("test")`, `kotlin("reflect")` (`contract/chat/build.gradle.kts:16-18`) |

### 1.2 Intended Dependency Direction

```text
:contract:auth ──api──> :contract:common ──api──> :validation:common
      │                                                  ▲
      └──api──> :validation:auth ──────────api───────────┘

:contract:chat ──api──> :contract:common ──api──> :validation:common
      │                                                  ▲
      └──api──> :validation:chat ──────────api───────────┘
```

### 1.3 Boundary Violations & Structural Findings

1. **Zero framework leakage in main sources (Clean)**: No Spring, Android, Jackson, Retrofit, or database libraries exist in `gradle/libs.versions.toml:1-11` or any `build.gradle.kts`. The only external runtime library is `org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0` (`gradle/libs.versions.toml:3,7`).
2. **Not Kotlin Multiplatform (JVM-only plugins despite repo name)**: Although the repository is named `KMP-ChatContract`, every module applies `org.jetbrains.kotlin.jvm` (`jvmToolchain(21)`) and `java-library` (`build.gradle.kts:6,29`, `contract/*/build.gradle.kts:1-9`, `validation/*/build.gradle.kts:1-8`), not `org.jetbrains.kotlin.multiplatform`.
3. **Cross-domain test coupling (`:contract:chat` -> `:contract:auth`)**: `contract/chat/build.gradle.kts:16` adds `testImplementation(projects.contract.auth)` so `ChatContractCompletenessTest` (`contract/chat/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatContractCompletenessTest.kt:66-92`) can assert global uniqueness and golden-snapshot matching across `CommonServerError`, `AuthServerError`, and `ChatServerError`.
4. **`kotlinx-serialization-json` declared as `implementation` instead of `api`**: In `contract/common/build.gradle.kts:13`, `contract/auth/build.gradle.kts:14`, and `contract/chat/build.gradle.kts:14`, `libs.kotlinx.serialization.json` is `implementation`, even though generated `@Serializable` companions expose `KSerializer` in public ABI and `README.md:83` expects consumers to call `json.decodeFromString<ApiErrorDto>(...)`.
5. **Stale pre-split directory tree under `contract/src` and `contract/build`**: Empty directories remain at `contract/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/{auth/error,chat/error}` alongside stale test results from `2026-10-28` in `contract/build/test-results/test/TEST-io.github.neronguyenvn.nerochat.contract.error.ServerErrorGoldenJsonTest.xml`, even though `:contract` is not an included project in `settings.gradle.kts:17-22`.
6. **How `backend` and `android` consume the contract**: `README.md:110-128` documents consumption via Gradle `includeBuild("../KMP-ChatContract")` dependency substitution or GitHub Packages (`build.gradle.kts:68-75`). Actual consumption and version pinning in `KMP-ChatBackend` and `Android-ChatApp` is **cross-repo, not verified** from this repo alone (see Section 11).

---

## 2. Existing tooling

### 2.1 Build, Test, and CI Configuration

- **Gradle Wrapper**: Gradle `9.2.0` (`gradle/wrapper/gradle-wrapper.properties:3`).
- **Daemon JVM Toolchain mismatch**: `gradle/gradle-daemon-jvm.properties:12` pins `toolchainVersion=25` with `api.foojay.io` download URLs (`gradle/gradle-daemon-jvm.properties:2-11`), whereas all 6 modules configure `kotlin { jvmToolchain(21) }` (`contract/*/build.gradle.kts:7-9`, `validation/*/build.gradle.kts:6-8`) and GitHub Actions workflows install JDK 21 (`java-version: '21'` in `.github/workflows/ci.yml:22-27` and `.github/workflows/publish.yml:27-32`). In any environment without a full JDK 25 installed and without network access to `api.foojay.io` (such as a network-isolated sandbox), every `./gradlew` command fails immediately during daemon startup.
- **Static Analysis / Formatting / Binary Compatibility**: None configured. No Detekt, Ktlint, Spotless, Konsist, or Kotlin Binary Compatibility Validator plugins exist in `gradle/libs.versions.toml` or `build.gradle.kts`.
- **CI Workflows**:
  - `.github/workflows/ci.yml:32-36`: runs `./gradlew check` and `./gradlew publishToMavenLocal` on push/PR to `main`.
  - `.github/workflows/publish.yml:58-88`: runs `./gradlew check`, `./gradlew publish "-Pversion=$VERSION"`, and `actions/delete-package-versions@v5` for `package-name: [common, auth, chat]`.

### 2.2 Command Status Inventory

| Command | Status | Evidence / Output Excerpt |
| :--- | :--- | :--- |
| `./gradlew tasks --all` *(in network-isolated sandbox without `/home/nero/.jdks`)* | **FAILS** | Exit `1`: `Unable to download toolchain matching the requirements ({languageVersion=25, vendor=any vendor, implementation=vendor-specific, nativeImageCapable=false}) from 'https://api.foojay.io/disco/v3.0/ids/fa1e318c287360478e3c83a9a3ef1007/redirect'` |
| `./gradlew tasks --all` *(host environment with `/home/nero/.jdks/jbr-25.0.4.1` accessible)* | **VERIFIED-GREEN** | Exit `0`: `BUILD SUCCESSFUL in 12s (1 actionable task: 1 executed)` |
| `./gradlew check --rerun-tasks` *(host environment)* | **VERIFIED-GREEN** | Exit `0`: `BUILD SUCCESSFUL in 33s (27 actionable tasks: 27 executed)` |
| `./gradlew test --rerun-tasks` *(host environment)* | **VERIFIED-GREEN** | Exit `0`: `BUILD SUCCESSFUL in 32s (27 actionable tasks: 27 executed)` |
| `./gradlew publishToMavenLocal` | **UNTESTED** | Excluded by audit read-only rule (writes artifacts to `~/.m2/repository`). Defined in `.github/workflows/ci.yml:36`. |
| `./gradlew publish "-Pversion=..."` | **UNTESTED** | Excluded by audit read-only rule (publishes to GitHub Packages `https://maven.pkg.github.com/neronguyen/KMP-ChatContract`). Defined in `.github/workflows/publish.yml:66`. |

---

## 3. Contract SSOT

### 3a. Contract inventory

> **Reuse note**: Copy this entire `3a` section as `INPUT_CONTRACT_INVENTORY` when auditing `backend` and `android`.

#### A. Wire Error Mechanism (`:contract:common`)

- **`ErrorCode`**: `@JvmInline @Serializable value class ErrorCode(val value: String)` (`contract/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/error/ErrorCode.kt:10-14`). Serializes directly as a flat JSON string (e.g. `"AuthServerError.InvalidCredentials"`), never a nested JSON object.
- **`ServerError`**: `interface ServerError { val code: ErrorCode }` (`contract/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/error/ServerError.kt:6-8`).
- **`ErrorContract<E : ServerError>`**: `open class ErrorContract<E : ServerError>(val entries: List<E>)` with `fromCode(code: ErrorCode): E?` and `fromCode(code: String): E?` (`contract/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/error/ErrorContract.kt:6-11`).
- **`ApiErrorDto`** (`contract/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/error/ApiErrorDto.kt:5-23`):
  - Primary `@Serializable` constructor:
    - `val error: ErrorCode` (flat JSON string wire code)
    - `val message: String` (human-readable message)
    - `val fieldErrors: Map<String, String> = emptyMap()` (per-field validation error map; defaults to `{}`)
    - `val retryAfterSeconds: Long? = null` (optional rate-limit backoff hint; defaults to `null`)
  - Secondary convenience constructor: `ApiErrorDto(error: ServerError, message: String, fieldErrors: Map<String, String> = emptyMap(), retryAfterSeconds: Long? = null)` delegates `error.code` to primary constructor (`ApiErrorDto.kt:12-22`).

#### B. Error Domains, Endpoint Error Contracts & Wire Error Codes (23 Total)

All error subtypes are payload-free `data object` singletons. Global count is locked at 23 by `ChatContractCompletenessTest.kt:75` and `contract/chat/src/test/resources/error-codes-golden.txt:1-23`.

| Domain Object / Interface | Endpoint Error Contract Interface | Allowed `data object` Entries | Wire `ErrorCode.value` Strings | Source File |
| :--- | :--- | :--- | :--- | :--- |
| `CommonServerError` (cross-cutting) | `CommonServerError` (`companion object : ErrorContract<CommonServerError>`) | `BadRequest`, `ValidationFailed`, `Unauthorized`, `RateLimitExceeded`, `InternalServerError` | `CommonServerError.BadRequest`<br>`CommonServerError.ValidationFailed`<br>`CommonServerError.Unauthorized`<br>`CommonServerError.RateLimitExceeded`<br>`CommonServerError.InternalServerError` | `contract/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/error/CommonServerError.kt:6-32` |
| `AuthServerError` | `AuthServerError.Register` | `UserAlreadyExists` | `AuthServerError.UserAlreadyExists` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:8-10,57-59` |
| `AuthServerError` | `AuthServerError.Login` | `InvalidCredentials`, `EmailNotVerified` | `AuthServerError.InvalidCredentials`<br>`AuthServerError.EmailNotVerified` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:12-19,61-67` |
| `AuthServerError` | `AuthServerError.RefreshToken` | `InvalidToken`, `UserNotFound` | `AuthServerError.InvalidToken`<br>`AuthServerError.UserNotFound` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:21-28,69-75` |
| `AuthServerError` | `AuthServerError.Logout` | `InvalidToken` | `AuthServerError.InvalidToken` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:30-32,69-71` |
| `AuthServerError` | `AuthServerError.VerifyEmail` | `InvalidToken` | `AuthServerError.InvalidToken` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:34-36,69-71` |
| `AuthServerError` | `AuthServerError.ResetPassword` | `InvalidToken`, `SamePassword` | `AuthServerError.InvalidToken`<br>`AuthServerError.SamePassword` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:38-45,69-71,81-83` |
| `AuthServerError` | `AuthServerError.ChangePassword` | `WrongPassword`, `SamePassword`, `UserNotFound` | `AuthServerError.WrongPassword`<br>`AuthServerError.SamePassword`<br>`AuthServerError.UserNotFound` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/error/AuthServerError.kt:47-55,73-83` |
| `ChatServerError` | `ChatServerError.GetParticipant` | `ChatParticipantNotFound` | `ChatServerError.ChatParticipantNotFound` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:8-10,70-72` |
| `ChatServerError` | `ChatServerError.GetRoom` | `ChatRoomNotFound` | `ChatServerError.ChatRoomNotFound` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:12-14,74-76` |
| `ChatServerError` | `ChatServerError.GetMessages` | `ParticipantNotInRoom` | `ChatServerError.ParticipantNotInRoom` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:16-18,90-92` |
| `ChatServerError` | `ChatServerError.CreateRoom` | `ChatParticipantNotFound`, `InvalidChatRoomSize` | `ChatServerError.ChatParticipantNotFound`<br>`ChatServerError.InvalidChatRoomSize` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:20-27,70-72,82-84` |
| `ChatServerError` | `ChatServerError.AddParticipants` | `ChatParticipantNotFound`, `ChatRoomNotFound`, `ParticipantAlreadyInRoom`, `ParticipantNotInRoom` | `ChatServerError.ChatParticipantNotFound`<br>`ChatServerError.ChatRoomNotFound`<br>`ChatServerError.ParticipantAlreadyInRoom`<br>`ChatServerError.ParticipantNotInRoom` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:29-38,70-76,86-92` |
| `ChatServerError` | `ChatServerError.LeaveRoom` | `ChatParticipantNotFound`, `ChatRoomNotFound` | `ChatServerError.ChatParticipantNotFound`<br>`ChatServerError.ChatRoomNotFound` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:40-47,70-76` |
| `ChatServerError` | `ChatServerError.DeleteMessage` | `MessageNotFound`, `NotMessageSender` | `ChatServerError.MessageNotFound`<br>`ChatServerError.NotMessageSender` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:49-56,78-80,94-96` |
| `ChatServerError` | `ChatServerError.WebSocket` | `ChatRoomNotFound`, `DuplicateMessageId`, `InvalidJson`, `InvalidPayload`, `MessageSendFailed` | `ChatServerError.ChatRoomNotFound`<br>`ChatServerError.DuplicateMessageId`<br>`ChatServerError.InvalidJson`<br>`ChatServerError.InvalidPayload`<br>`ChatServerError.MessageSendFailed` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/error/ChatServerError.kt:58-68,74-76,98-112` |

#### C. Request & Response Wire DTOs

| Module | Class / Enum | Kind | Fields & Types | Validation / Special Behavior | File Path |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `:contract:common` | `ApiErrorDto` | Error Response DTO | `error: ErrorCode`, `message: String`, `fieldErrors: Map<String, String> = emptyMap()`, `retryAfterSeconds: Long? = null` | Secondary constructor accepts `ServerError` | `contract/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/error/ApiErrorDto.kt:5-23` |
| `:contract:auth` | `UserDto` | Response DTO | `id: String`, `email: String`, `displayName: String`, `isEmailVerified: Boolean` | `@Serializable` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/UserDto.kt:5-11` |
| `:contract:auth` | `AuthenticatedUserDto` | Response DTO | `user: UserDto`, `accessToken: String`, `refreshToken: String` | Redacts `accessToken` & `refreshToken` in `toString()` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/AuthenticatedUserDto.kt:5-13` |
| `:contract:auth` | `RegisterRequest` | Request DTO | `email: String`, `displayName: String`, `password: String` | `Validatable<AuthValidationError.Register>`; redacts `password` in `toString()` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/RegisterRequest.kt:8-22` |
| `:contract:auth` | `LoginRequest` | Request DTO | `email: String`, `password: String` | `Validatable<AuthValidationError.Login>`; redacts `password` in `toString()` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/LoginRequest.kt:8-21` |
| `:contract:auth` | `EmailRequest` | Request DTO | `email: String` | `Validatable<AuthValidationError.Email>` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/EmailRequest.kt:8-17` |
| `:contract:auth` | `RefreshTokenRequest` | Request DTO | `refreshToken: String` | `Validatable<AuthValidationError.RefreshToken>`; redacts `refreshToken` in `toString()` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/RefreshTokenRequest.kt:8-20` |
| `:contract:auth` | `ResetPasswordRequest` | Request DTO | `token: String`, `newPassword: String` | `Validatable<AuthValidationError.ResetPassword>`; redacts `token` & `newPassword` in `toString()` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/ResetPasswordRequest.kt:8-21` |
| `:contract:auth` | `ChangePasswordRequest` | Request DTO | `oldPassword: String`, `newPassword: String` | `Validatable<AuthValidationError.ChangePassword>`; redacts `oldPassword` & `newPassword` in `toString()` | `contract/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/auth/ChangePasswordRequest.kt:8-21` |
| `:contract:chat` | `ChatRoomType` | Enum | `DIRECT`, `GROUP` | `@Serializable` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatRoomType.kt:5-9` |
| `:contract:chat` | `ChatParticipantDto` | Response DTO | `userId: String`, `email: String`, `displayName: String`, `profilePictureUrl: String? = null` | `@Serializable` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatParticipantDto.kt:5-11` |
| `:contract:chat` | `ChatMessageDto` | Response DTO | `id: String`, `roomId: String`, `senderId: String`, `content: String`, `createdAt: kotlin.time.Instant` | `@OptIn(ExperimentalTime::class) @Serializable` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatMessageDto.kt:7-15` |
| `:contract:chat` | `ChatMessageSliceDto` | Response DTO | `messages: List<ChatMessageDto>`, `hasMore: Boolean` | `@Serializable` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatMessageSliceDto.kt:5-9` |
| `:contract:chat` | `ChatRoomDto` | Response DTO | `id: String`, `type: ChatRoomType`, `name: String? = null`, `creator: ChatParticipantDto`, `participants: List<ChatParticipantDto>`, `lastMessage: ChatMessageDto? = null`, `lastActivityAt: kotlin.time.Instant` | `@OptIn(ExperimentalTime::class) @Serializable` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatRoomDto.kt:7-17` |
| `:contract:chat` | `CreateDirectChatRoomRequest` | Request DTO | `targetUserId: String`, `message: String` | `Validatable<ChatValidationError.CreateDirectRoom>` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/CreateDirectChatRoomRequest.kt:8-18` |
| `:contract:chat` | `CreateGroupChatRoomRequest` | Request DTO | `participantIds: List<String>`, `name: String? = null` | `Validatable<ChatValidationError.CreateGroupRoom>` (`name` is not validated) | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/CreateGroupChatRoomRequest.kt:8-18` |
| `:contract:chat` | `AddChatParticipantRequest` | Request DTO | `userIds: List<String>` | `Validatable<ChatValidationError.AddParticipants>` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/AddChatParticipantRequest.kt:8-17` |

#### D. WebSocket Frame Types (`:contract:chat`)

Both hierarchies use `@JsonClassDiscriminator("type")`.

| Hierarchy | Subtype | `@SerialName` (`type` discriminator) | Fields & Types | Notes / File Path |
| :--- | :--- | :--- | :--- | :--- |
| `WsClientMessage` | `WsClientMessage.NewMessage` | `"new_message"` | `roomId: String`, `content: String`, `messageId: String? = null` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/WsClientMessage.kt:8-25` |
| `WsServerEvent` | `WsServerEvent.NewMessage` | `"new_message"` | `message: ChatMessageDto` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/WsServerEvent.kt:15-19` |
| `WsServerEvent` | `WsServerEvent.MessageDeleted` | `"message_deleted"` | `roomId: String`, `messageId: String` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/WsServerEvent.kt:21-26` |
| `WsServerEvent` | `WsServerEvent.ParticipantJoined` | `"participant_joined"` | `roomId: String`, `newUsers: List<ChatParticipantDto>`, `addedBy: ChatParticipantDto? = null` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/WsServerEvent.kt:28-34` |
| `WsServerEvent` | `WsServerEvent.ParticipantLeft` | `"participant_left"` | `roomId: String`, `leftUser: ChatParticipantDto`, `kickedBy: ChatParticipantDto? = null` | `contract/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/chat/WsServerEvent.kt:36-42` |
| `WsServerEvent` | `WsServerEvent.Error` | `"error"` | `error: ErrorCode`, `message: String`, `roomId: String? = null`, `messageId: String? = null` | Secondary constructor takes `error: ChatServerError.WebSocket` (`WsServerEvent.kt:44-63`). Note: does not include `fieldErrors` or `retryAfterSeconds`. |

#### E. Shared Validation Rules & `ValidationError` Catalog (`:validation:*`)

- `ValidationError` (`validation/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/validation/ValidationError.kt:9-12`): `val field: String`, `val message: String`.
- `Validatable<out E : ValidationError>` (`ValidationError.kt:18-23`): `fun validate(): List<E>`.
- `EmailValidator` (`validation/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/validation/EmailValidator.kt:6-29`): `EMAIL_REGEX`, `normalize(email) = email.trim().lowercase()`, `isValidEmail(email)` (does not trim).
- `UuidValidator` (`validation/common/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/validation/UuidValidator.kt:6-21`): `UUID_REGEX` (8-4-4-4-12 hex), `isValidUuid(uuid)`.
- `PasswordValidator` (`validation/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/validation/auth/PasswordValidator.kt:6-29`): `MIN_PASSWORD_LENGTH = 10` code points, at least 1 `Char.isLetter()` and 1 `Char.isDigit()`.
- `AuthValidationError` (`validation/auth/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/validation/auth/AuthValidationError.kt:11-85`) & `AuthValidator` (`AuthValidator.kt:8-144`):
  - `EmptyEmail` (`field="email"`), `InvalidEmail` (`field="email"`), `EmptyDisplayName` (`field="displayName"`, rejects untrimmed strings too at `AuthValidator.kt:24`), `EmptyPassword` (`field="password"`), `InvalidPassword` (`field="password"`), `EmptyRefreshToken` (`field="refreshToken"`), `EmptyResetToken` (`field="token"`), `EmptyOldPassword` (`field="oldPassword"`), `EmptyNewPassword` (`field="newPassword"`), `InvalidNewPassword` (`field="newPassword"`).
- `ChatValidationError` (`validation/chat/src/main/kotlin/io/github/neronguyenvn/nerochat/contract/validation/chat/ChatValidationError.kt:11-74`) & `ChatValidator` (`ChatValidator.kt:9-88`):
  - `EmptySearchEmail` (`field="email"`), `InvalidSearchEmail` (`field="email"`), `SelfParticipantNotAllowed` (`field="email"`), `MissingDirectRecipient` (`field="targetUserId"`), `InvalidTargetUserId` (`field="targetUserId"`), `EmptyFirstMessage` (`field="message"`), `TooFewGroupParticipants` (`field="participantIds"`, requires `size >= 2`, allows duplicate UUIDs at `ChatValidator.kt:54-67`), `InvalidGroupParticipantId` (`field="participantIds"`), `EmptyParticipantIds` (`field="userIds"`, allows duplicate UUIDs at `ChatValidator.kt:73-86`), `InvalidParticipantId` (`field="userIds"`).

#### F. Serialization Configuration & Golden Snapshot Coverage

- **Shared `Json` configuration**: None exported in `src/main`. Each test class creates its own private `Json { ignoreUnknownKeys = true }` (`CommonContractTest.kt:10`, `AuthContractTest.kt:12`, `ChatContractTest.kt:15-17`).
- **Golden error-code list files (EXIST)**:
  - `contract/common/src/test/resources/common-error-codes-golden.txt` (5 codes, checked in `CommonContractCompletenessTest.kt:25-32`).
  - `contract/auth/src/test/resources/auth-error-codes-golden.txt` (7 codes, checked in `AuthContractCompletenessTest.kt:64-74`).
  - `contract/chat/src/test/resources/error-codes-golden.txt` (all 23 global codes, checked in `ChatContractCompletenessTest.kt:79-92`).
- **Golden JSON payload tests (PARTIAL / MISSING FOR DTO FIELDS)**:
  - `CommonContractTest.kt:22-25,49`, `AuthContractTest.kt:51-54`, and `ChatContractTest.kt:52,72,78-82,105-108` assert substring presence of `"error":"..."` and `"type":"..."`, plus one literal JSON string for forward compatibility (`CommonContractTest.kt:49`).
  - There are **no golden JSON snapshot tests** locking the exact serialized JSON field names/structure of `UserDto`, `AuthenticatedUserDto`, `RegisterRequest`, `LoginRequest`, `EmailRequest`, `RefreshTokenRequest`, `ResetPasswordRequest`, `ChangePasswordRequest`, `ChatRoomDto`, `ChatParticipantDto`, `ChatMessageDto`, `ChatMessageSliceDto`, `CreateDirectChatRoomRequest`, `CreateGroupChatRoomRequest`, `AddChatParticipantRequest`, `WsClientMessage`, or `WsServerEvent`.

---

## 4. Test inventory

All tests are JUnit 5 (`useJUnitPlatform()`) unit tests using `kotlin.test` and `kotlin-reflect`. No mocks are used anywhere in the repository.

| Module | Test Class | Type | `@Test` Count | What It Exercises | Passes If Wire Field Names Change? |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `:contract:common` | `CommonContractCompletenessTest` (`contract/common/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/error/CommonContractCompletenessTest.kt:7-33`) | Unit (Reflection + Golden) | 2 | Verifies all `CommonServerError` subclasses are `data object`s, registered in `CommonServerError.entries`, and match `/common-error-codes-golden.txt`. | **No** for error code strings; **Yes** for `ApiErrorDto` field names. |
| `:contract:common` | `CommonContractTest` (`contract/common/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/error/CommonContractTest.kt:8-56`) | Unit (Serialization) | 3 | `ApiErrorDto` round-trip, substring `"error":"CommonServerError.BadRequest"`, and literal JSON `{"error":"FutureService.BrandNewError","message":"Something new","fieldErrors":{}}` decoding. | **No** for `error`, `message`, `fieldErrors` (locked by line 49 literal JSON); **Yes** if `retryAfterSeconds` is renamed (`null` omitted by default). |
| `:contract:auth` | `AuthContractCompletenessTest` (`contract/auth/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/auth/AuthContractCompletenessTest.kt:13-75`) | Unit (Reflection + Golden) | 4 | Verifies all 7 `AuthServerError` endpoint contracts match their `sealedSubclasses`, are `data object`s, do not overlap `CommonServerError`, and match `/auth-error-codes-golden.txt`. | **No** for `AuthServerError` wire codes; **Yes** for all Auth DTO field names. |
| `:contract:auth` | `AuthContractTest` (`contract/auth/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/auth/AuthContractTest.kt:10-174`) | Unit (Serialization + Validation + Redaction) | 7 | Symmetric `encodeToString`/`decodeFromString` round-trips for `UserDto`, `AuthenticatedUserDto`, `RegisterRequest`, `LoginRequest`; `validate()` checks for all 6 auth request DTOs; `toString()` redaction checks. | **YES (Flagged)**: symmetric round-trip `decodeFromString(encodeToString(dto))` still passes if any property in `UserDto`, `AuthenticatedUserDto`, `RegisterRequest`, or `LoginRequest` is renamed, and `EmailRequest`, `RefreshTokenRequest`, `ResetPasswordRequest`, `ChangePasswordRequest` have no serialization test at all. |
| `:contract:chat` | `ChatContractCompletenessTest` (`contract/chat/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatContractCompletenessTest.kt:14-93`) | Unit (Reflection + Golden) | 5 | Verifies all 8 `ChatServerError` endpoint contracts match their `sealedSubclasses`, are `data object`s, do not overlap `CommonServerError`, and all 23 global error codes are unique and match `/error-codes-golden.txt`. | **No** for error code strings; **Yes** for all Chat DTO & WS frame field names. |
| `:contract:chat` | `ChatContractTest` (`contract/chat/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/chat/ChatContractTest.kt:13-187`) | Unit (Serialization + Validation) | 7 | Symmetric round-trips for `ChatRoomDto`, `WsClientMessage.NewMessage`, `WsServerEvent.{NewMessage,Error}`, `CreateDirectChatRoomRequest`, `CreateGroupChatRoomRequest`, `AddChatParticipantRequest`; checks `"type":"new_message"`, `"type":"error"`, `"error":"ChatServerError..."`. | **YES (Flagged)**: symmetric round-trips still pass if non-discriminator fields (`roomId`, `senderId`, `userId`, `targetUserId`, `participantIds`, `userIds`, etc.) are renamed; `ChatMessageSliceDto` and `WsServerEvent.{MessageDeleted,ParticipantJoined,ParticipantLeft}` have no serialization test at all. |
| `:validation:common` | `EmailValidatorTest` (`validation/common/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/validation/EmailValidatorTest.kt:8-41`) | Unit | 3 | `EmailValidator.normalize` and valid/invalid email regex cases. | **Yes** (does not test wire format). |
| `:validation:common` | `UuidValidatorTest` (`validation/common/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/validation/UuidValidatorTest.kt:7-25`) | Unit | 2 | Valid and invalid 8-4-4-4-12 UUID strings. | **Yes** (does not test wire format). |
| `:validation:auth` | `AuthValidatorTest` (`validation/auth/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/validation/auth/AuthValidatorTest.kt:8-114`) | Unit | 9 | Display name, register, login, refresh token, reset password, and change password validation rules. | **Yes** (does not check `ValidationError.field` wire names against DTO property names). |
| `:validation:auth` | `PasswordValidatorTest` (`validation/auth/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/validation/auth/PasswordValidatorTest.kt:7-27`) | Unit | 2 | Valid/invalid passwords including supplementary Unicode code points. | **Yes** (does not test wire format). |
| `:validation:chat` | `ChatValidatorTest` (`validation/chat/src/test/kotlin/io/github/neronguyenvn/nerochat/contract/validation/chat/ChatValidatorTest.kt:8-124`) | Unit | 4 | Search email, direct room, group room, and add participants validation rules. | **Yes** (does not check `ValidationError.field` wire names against DTO property names). |

---

## 5. Flow inventory

n/a for this role

---

## 6. UI inventory (android)

n/a for this role

---

## 7. Runtime & state mechanics

n/a for this role

---

## 8. Current verification capability

- **Tier 1 (sub-10s headless check)**:
  - **Exists, with daemon-startup caveat**: The sum of JUnit test execution times across all 11 test suites (48 test cases) is **1.001 seconds** (`validation:common` `0.048s`, `validation:auth` `0.100s`, `validation:chat` `0.056s`, `contract:common` `0.436s`, `contract:auth` `0.366s`, `contract:chat` `0.095s` from `build/test-results/test/*.xml`).
  - However, cold Gradle daemon startup with `--rerun-tasks` took **32s–33s** (`./gradlew check --rerun-tasks`), and running inside a sandboxed environment without `/home/nero/.jdks/jbr-25.0.4.1` **fails immediately** (`exit 1`) because `gradle/gradle-daemon-jvm.properties:12` demands `toolchainVersion=25` and tries to reach `https://api.foojay.io`.
- **Tier 1.5 (Testcontainers / Robolectric)**:
  - **n/a (Pure Kotlin library)**: This repository has no database, HTTP server, or Android runtime.
- **Tier 2 (Boot real runtime and drive a real flow)**:
  - **Cannot be done from this repo alone**: `KMP-ChatContract` is a compile-time library (`build.gradle.kts:29-30`) with no executable entrypoint, server, or client transport. End-to-end runtime verification requires `KMP-ChatBackend` and/or `Android-ChatApp`.

---

## 9. Lever backlog

Ranked from fastest/foundational Tier 1 guardrails in `contract` outward:

| Rank | Gap | Proposed Lever | Source Finding | Size | Dependencies | Owner Repo |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `./gradlew check` fails in sandboxed/offline environments with JDK 21 because `gradle/gradle-daemon-jvm.properties:12` pins `toolchainVersion=25` while modules & CI use JDK 21 | Align `gradle/gradle-daemon-jvm.properties` to JDK 21 (`./gradlew updateDaemonJvm --jvm-version=21`) and enable `org.gradle.configuration-cache=true` in `gradle.properties` for sub-5s Tier 1 runs | Section 2.1, Section 8 (`gradle/gradle-daemon-jvm.properties:12`, `.github/workflows/ci.yml:25`) | **S** | None | `contract` |
| 2 | Symmetric `encodeToString`/`decodeFromString` tests pass even if DTO property names or JSON structure change; 6 DTOs/frames have zero serialization tests | Add **Golden JSON Wire Snapshot Tests** asserting exact JSON strings (field names, defaults, `Instant` ISO-8601 format, discriminator keys) for every DTO in `:contract:auth` and `:contract:chat` and every `WsClientMessage` / `WsServerEvent` subtype | Section 3a.F, Section 4 (`AuthContractTest.kt:15-42`, `ChatContractTest.kt:20-96`) | **S** | None | `contract` |
| 3 | `ValidationError.field` strings (`"email"`, `"targetUserId"`, `"participantIds"`, `"userIds"`, etc.) are hardcoded strings that could drift from `@Serializable` DTO property names | Add a **Reflection Completeness Test** asserting that every `Validatable<E>` request DTO's possible `ValidationError.field` values match an actual property name on that DTO class (`KClass.memberProperties`) | Section 3a.E, Section 4 (`AuthValidationError.kt:34-84`, `ChatValidationError.kt:25-73`) | **S** | None | `contract` |
| 4 | `ValidationError` sealed hierarchies (`AuthValidationError.*`, `ChatValidationError.*`) lack the reflection completeness guardrails that `ServerError` has | Add **Validation Contract Completeness Tests** (or Konsist rules) verifying all `AuthValidationError` and `ChatValidationError` sealed subtypes are `data object`s and covered by `AuthValidatorTest` / `ChatValidatorTest` | Section 4 (`AuthValidationError.kt`, `ChatValidationError.kt`) | **S** | None | `contract` |
| 5 | Breaking wire or binary changes to public contract classes are not automatically detected before publishing | Add **Kotlin Binary Compatibility Validator (`org.jetbrains.kotlinx.binary-compatibility-validator`)** (`apiCheck` wired into `check`) across `:contract:*` and `:validation:*` | Section 2.1 (`build.gradle.kts`) | **S** | None | `contract` |
| 6 | Stale pre-split `contract/src` empty directories and `contract/build` artifacts remain on disk; `.github/workflows/publish.yml:76-80` only prunes 3 package names | Clean up orphaned `contract/src` directory and update `.github/workflows/publish.yml` cleanup matrix to cover all published package coordinates | Section 1.3, Section 2.1 (`publish.yml:76-80`) | **S** | None | `contract` |

---

## 10. As-is facts for c4.md

1. **`docs/architecture/c4.md` does not exist**: C4 Level 1 and Level 2 architecture specifications live at `docs/architecture/specifications/system-context.archify.json` and `docs/architecture/specifications/containers.archify.json`, with rendered outputs in `docs/architecture/interactive/c4-{system-context,containers}.html` and `docs/architecture/assets/*.png`.
2. **This repo's C4 representation**:
   - In `docs/architecture/specifications/containers.archify.json:38-63`, this repo is modeled as container `shared_contract` (`label: "Shared Contract"`, `sublabel: "Kotlin Multi-Module"`, `tag: "Compile-Time Artifact"`).
   - Outbound compile-time connections in `containers.archify.json:239-255`:
     - `shared_contract -> android_app`: `"Shared DTOs & validators"` (dashed).
     - `shared_contract -> backend_api`: `"Shared DTOs & validators"` (dashed).
   - Runtime wire protocol arrow (`containers.archify.json:257-262`):
     - `android_app -> backend_api`: `"JSON/HTTPS & WSS"`.
3. **Discrepancies between documentation/C4 specs and this repo's actual code**:
   - **`README.md:16` vs `gradle/libs.versions.toml`**: `README.md:16` states the tech stack is `Pure Kotlin 2.4, kotlinx.serialization, kotlinx-datetime`, but `kotlinx-datetime` is not a dependency; `ChatMessageDto.kt:4-5` and `ChatRoomDto.kt:4-5` use `kotlin.time.Instant` from the Kotlin 2.4 standard library.
   - **`containers.archify.json:8-12` & `system-context.archify.json:8-12` source links**: Both Archify specifications in this repo set `"repository": { "url": "git@github.com:neronguyen/KMP-NeroChatBackend.git" }` and all `sources` paths (`chat/src/main/...`, `app/src/main/...`, `notification/src/main/...`) point to files in `KMP-ChatBackend`, not files in `KMP-ChatContract`.
   - **Repo naming vs Gradle plugin**: Named `KMP-ChatContract`, but uses `org.jetbrains.kotlin.jvm` (`jvmToolchain(21)`) rather than Kotlin Multiplatform (`org.jetbrains.kotlin.multiplatform`).

---

## 11. Open questions for me

1. **Daemon JVM version (`25` vs `21`)**: `gradle/gradle-daemon-jvm.properties:12` specifies `toolchainVersion=25`, whereas all module `build.gradle.kts` files target `jvmToolchain(21)` and `.github/workflows/ci.yml` sets up JDK 21. Should `gradle-daemon-jvm.properties` be updated to `21` so Gradle runs offline/sandboxed with the project's JDK 21 toolchain?
2. **KMP vs pure JVM target**: Is `KMP-ChatContract` intended to stay `org.jetbrains.kotlin.jvm` (since both Android and Spring Boot consume JVM bytecode), or should it migrate to `org.jetbrains.kotlin.multiplatform`?
3. **Endpoint mapping for `EmailRequest` and `UserDto`**: `AuthServerError` defines `Register`, `Login`, `RefreshToken`, `Logout`, `VerifyEmail`, `ResetPassword`, and `ChangePassword` (`AuthServerError.kt:8-55`), while request DTOs include `EmailRequest` (`EmailRequest.kt:9-11`). Which backend endpoints consume `EmailRequest` (e.g., forgot-password / resend-verification) and why do they not have dedicated `AuthServerError` sub-interfaces? (**cross-repo, not verified**)
4. **Duplicate UUIDs in `CreateGroupChatRoomRequest` and `AddChatParticipantRequest`**: `ChatValidator.validateGroupRoom` (`ChatValidator.kt:54-67`) and `ChatValidator.validateAddParticipants` (`ChatValidator.kt:73-86`) explicitly allow duplicate UUIDs in `participantIds` / `userIds` (tested in `ChatValidatorTest.kt:84-85,109-110`). Is deduplication expected on the backend, or should the contract validator reject duplicate participant IDs?
5. **`CreateGroupChatRoomRequest.name` validation**: `CreateGroupChatRoomRequest.kt:14` states `[name] is not validated` (so blank/whitespace-only group names pass contract validation). Is that intentional?
6. **Cross-repo items not verified from `KMP-ChatContract`**:
   - How `KMP-ChatBackend` and `Android-ChatApp` consume `KMP-ChatContract` (composite build vs published version, and whether `0.0.1` or `0.1.0` is referenced) — **cross-repo, not verified**.
   - Whether `KMP-ChatBackend` and `Android-ChatApp` configure `kotlinx.serialization.json.Json` identically (e.g. `ignoreUnknownKeys`, `encodeDefaults`, `explicitNulls`, `classDiscriminator`) — **cross-repo, not verified**.
   - Whether any REST controller, WebSocket handler, or Android data source defines duplicate DTOs or bypasses `ApiErrorDto` / `ErrorCode` — **cross-repo, not verified**.

### 11.1 Resolved Decisions (Post-Audit Alignment)

1. **Daemon JVM version (`25` vs `21`)**: Align `gradle/gradle-daemon-jvm.properties` to `toolchainVersion=21` (matching module `jvmToolchain(21)` and CI JDK 21) and enable `org.gradle.configuration-cache=true` in `gradle.properties`.
2. **KMP vs pure JVM target**: Keep `org.jetbrains.kotlin.jvm` for now (both consumers are JVM) and add a check banning `java.*` APIs in main sources (replacing `String.codePointCount` in `PasswordValidator.kt:17`) so future KMP migration stays trivial.
3. **`EmailRequest` Endpoints (`forgot-password` & `resend-verification`)**: Intentional anti-enumeration design—both endpoints return `204 No Content` or `CommonServerError` only (verified in `KMP-ChatBackend` audit); document this explicitly in `AuthServerError` KDoc.
4. **Duplicate UUIDs in `ChatValidator`**: Require at least 2 distinct UUIDs in `ChatValidator.validateGroupRoom` (`participantIds.toSet().size < 2` -> `TooFewGroupParticipants`), while allowing/deduplicating duplicates in `validateAddParticipants`.
5. **`CreateGroupChatRoomRequest.name` validation**: Keep `name` optional/unvalidated in the contract; `KMP-ChatBackend` (`ChatRoomService.kt:155`) trims and normalizes blank strings to `null`.
6. **WebSocket `WsClientMessage.NewMessage` validation (from `backend` audit)**: Implement `Validatable` on `WsClientMessage.NewMessage` (`roomId` UUID, optional `messageId` UUID, non-blank `content`) so `ChatWebSocketHandler` can deterministically emit `ChatServerError.WebSocket.InvalidPayload`.
7. **Cross-repo items verified in `backend` & `android` audits**:
   - Both `KMP-ChatBackend` and `Android-ChatApp` pin `contract = "0.3.1"` in `gradle/libs.versions.toml` and default to `includeBuild("../KMP-ChatContract")` when the sibling folder exists (`useLocalContract != "false"`).
   - Both use `kotlinx.serialization.json.Json` with `classDiscriminator = "type"` (`WsServerEvent.CLASS_DISCRIMINATOR`) for WebSocket frames and flat `ErrorCode` strings on `ApiErrorDto`.

---

## Commands run

| # | Command | Exit Code | Notes |
| :--- | :--- | :--- | :--- |
| 1 | `git -C /home/nero/Projects/KMP-ChatContract rev-parse HEAD && git -C /home/nero/Projects/KMP-ChatContract branch --show-current && git -C /home/nero/Projects/KMP-ChatContract status --short` | `0` | Commit `d98170d33843a0a0cb8af648886b43fb42b686e7`, branch `main`, clean working tree |
| 2 | `find /home/nero/Projects/KMP-ChatContract -maxdepth 6 -not -path '*/.git*' -not -path '*/.gradle*' -not -path '*/build*' -not -path '*/.idea*' -not -path '*/.kotlin*' \| sort` | `0` | Directory structure inspection |
| 3 | `find /home/nero/Projects/KMP-ChatContract -type f -not -path '*/.git/*' -not -path '*/.gradle/*' -not -path '*/build/*' -not -path '*/.idea/*' -not -path '*/.kotlin/*' \| sort` | `0` | Full source/config file listing |
| 4 | `find /home/nero/Projects/KMP-ChatContract/contract/src -maxdepth 10` | `0` | Found empty leftover pre-split test directories |
| 5 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract tasks --all` *(sandboxed)* | `1` | Failed in sandbox: `toolchainVersion=25` in `gradle-daemon-jvm.properties` attempted `api.foojay.io` download |
| 6 | `java -version && ls -la /usr/lib/jvm ~/.gradle/jdks 2>&1 \|\| true` | `0` | Confirmed `/usr/lib/jvm/java-25-openjdk` is JRE-only and `~/.gradle/jdks` has JDK 17 & 21 |
| 7 | `ls -la /usr/lib/jvm/java-25-openjdk/bin && ls -la /home/nero/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2/` | `0` | Confirmed `/usr/lib/jvm/java-25-openjdk/bin` has no `javac` |
| 8 | `ls -la ~/.jdks ~/.sdkman /opt 2>&1 \|\| true` *(sandboxed)* | `0` | `~/.jdks` not mounted in sandbox |
| 9 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract check -Dorg.gradle.java.home=/home/nero/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2` *(sandboxed)* | `1` | `gradle-daemon-jvm.properties` (`toolchainVersion=25`) overrides `-Dorg.gradle.java.home` |
| 10 | `ls -la /home/nero/.gradle/daemon 2>&1 \|\| true` | `0` | Checked Gradle daemon directories |
| 11 | `head -n 20 $(ls -t /home/nero/.gradle/daemon/9.2.0/*.out.log \| head -n 2)` | `0` | Confirmed host daemon uses `/home/nero/.jdks/jbr-25.0.4.1` |
| 12 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract tasks --all` *(unsandboxed)* | `0` | `BUILD SUCCESSFUL in 12s` |
| 13 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract check --rerun-tasks` *(sandboxed)* | `1` | Failed in sandbox (`toolchainVersion=25` invisible inside sandbox) |
| 14 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract check --rerun-tasks` *(unsandboxed)* | `0` | `BUILD SUCCESSFUL in 33s (27 actionable tasks: 27 executed)` |
| 15 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract test --rerun-tasks` *(sandboxed)* | `1` | Failed in sandbox (`toolchainVersion=25` invisible inside sandbox) |
| 16 | `/home/nero/Projects/KMP-ChatContract/gradlew -p /home/nero/Projects/KMP-ChatContract test --rerun-tasks` *(unsandboxed)* | `0` | `BUILD SUCCESSFUL in 32s (27 actionable tasks: 27 executed)` |
| 17 | `find /home/nero/Projects/KMP-ChatContract -path '*/build/test-results/test/*.xml' -exec head -n 5 {} +` | `0` | Inspected JUnit XML test counts and durations |
| 18 | `git -C /home/nero/Projects/KMP-ChatContract status --short` | `0` | Verified clean working tree before writing audit file |
