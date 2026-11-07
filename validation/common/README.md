# validation:common

## Overview
Pure Kotlin validation primitives and interfaces providing the foundation for cross-platform form and wire payload validation across client and server.

## Interface
- `[EmailValidator.kt:EmailValidator]`: Email format regex validation and string normalization.
- `[UuidValidator.kt:UuidValidator]`: UUID v4/v7 regex format verification.
- `[ValidationError.kt:Validatable]`: Core contract interface for self-validating requests (`validate(): List<ValidationError>`).
- `[ValidationError.kt:ValidationError]`: Base interface for field-level validation errors (`field`, `message`).

## Invariants
- Zero third-party dependencies; uses only Kotlin standard library.
- Zero reflection; pure functional validators.
- KMP ready: no direct `java.*` calls in validation logic.

## Side Effects
- None (pure CPU in-memory validation functions).

## Verification
- Run: `./gradlew :validation:common:test --rerun-tasks`
