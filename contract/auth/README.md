# contract:auth

## Overview
Authentication wire DTOs, request payloads implementing `Validatable`, and sealed endpoint-specific error hierarchies under `AuthServerError`.

## Interface
- `[UserDto.kt:UserDto]`: Public user wire profile model (`id`, `email`, `displayName`, `isEmailVerified`).
- `[AuthenticatedUserDto.kt:AuthenticatedUserDto]`: Combined session DTO returning `UserDto`, `accessToken`, and `refreshToken`.
- `[RegisterRequest.kt:RegisterRequest]`: Self-validating registration wire payload (`email`, `password`, `displayName`).
- `[LoginRequest.kt:LoginRequest]`: Self-validating login wire payload (`email`, `password`).
- `[RefreshTokenRequest.kt:RefreshTokenRequest]`: Self-validating token refresh payload (`refreshToken`).
- `[EmailRequest.kt:EmailRequest]`: Self-validating verification/password-reset email request payload (`email`).
- `[ResetPasswordRequest.kt:ResetPasswordRequest]`: Self-validating password reset payload (`token`, `newPassword`).
- `[ChangePasswordRequest.kt:ChangePasswordRequest]`: Self-validating password change payload (`oldPassword`, `newPassword`).
- `[AuthServerError.kt:AuthServerError]`: Per-endpoint error hierarchies for `Register`, `Login`, `RefreshToken`, `Logout`, `VerifyEmail`, `ResetPassword`, `ChangePassword`.

## Invariants
- Depends on `:contract:common` and `:validation:auth`.
- Sensitive fields (passwords, tokens) are redacted in `toString()`.
- Every `AuthServerError` entry must be cataloged in its endpoint's `ErrorContract` companion and match `auth-error-codes-golden.txt`.

## Side Effects
- None (pure data contracts and serialization schemas).

## Verification
- Run: `./gradlew :contract:auth:test --rerun-tasks`
