# contract:common

## Overview
Shared error primitives, flat wire error models, and catalog contracts for type-safe client-server error handling.

## Interface
- `[ErrorCode.kt:ErrorCode]`: Flat `@JvmInline value class` serializing error codes as flat strings (`"Namespace.Code"`).
- `[ServerError.kt:ServerError]`: Base interface for all singleton server error codes (`code: ErrorCode`).
- `[ErrorContract.kt:ErrorContract]`: Reflection-free error catalog providing `fromCode(code: String): ServerError?`.
- `[CommonServerError.kt:CommonServerError]`: Shared server errors (`BadRequest`, `Unauthorized`, `ValidationFailed`, `RateLimitExceeded`, `InternalServerError`).
- `[ApiErrorDto.kt:ApiErrorDto]`: Unified HTTP/WebSocket error response wire payload (`error`, `message`, `fieldErrors`, `retryAfterSeconds`).

## Invariants
- Zero internal dependencies; depends only on `libs.kotlinx.serialization.json` and Kotlin standard library.
- Error codes must serialize as flat JSON strings, never nested objects.
- All errors cataloged in `CommonServerError` companion must match `common-error-codes-golden.txt`.

## Side Effects
- None (pure data contracts and serialization schemas).

## Verification
- Run: `./gradlew :contract:common:test --rerun-tasks`
