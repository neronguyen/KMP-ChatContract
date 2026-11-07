# validation:auth

## Overview
Authentication and credential validation rules implementing `Validatable` for login, registration, password updates, and token operations.

## Interface
- `[AuthValidator.kt:AuthValidator]`: Central singleton validator for registration, login, refresh token, password reset, and password change requests.
- `[PasswordValidator.kt:PasswordValidator]`: Password complexity and minimum length enforcement (`MIN_PASSWORD_LENGTH = 10`).
- `[AuthValidationError.kt:AuthValidationError]`: Sealed hierarchy of authentication validation errors (`EmptyEmail`, `InvalidEmail`, `EmptyPassword`, `InvalidPassword`, `EmptyDisplayName`, `EmptyRefreshToken`, `EmptyResetToken`, `InvalidNewPassword`, `EmptyOldPassword`, `EmptyNewPassword`).

## Invariants
- Depends exclusively on `:validation:common`.
- Passwords must be at least 10 characters containing at least one digit and one letter.
- Email inputs are validated against `EmailValidator.isValidEmail` (whitespace is not permitted).

## Side Effects
- None (pure CPU in-memory validation functions).

## Verification
- Run: `./gradlew :validation:auth:test --rerun-tasks`
