# validation

## Overview
Root intermediate blueprint for pure Kotlin validation libraries providing shared validator interfaces and domain-specific rules.

## Subdirectories & Boundaries
- [validation:common](./common/README.md) - Core validation primitives (`Validatable`, `ValidationError`, `EmailValidator`, `UuidValidator`).
- [validation:auth](./auth/README.md) - Authentication validators (`AuthValidator`, `PasswordValidator`, `AuthValidationError`).
- [validation:chat](./chat/README.md) - Chat domain validators (`ChatValidator`, `ChatValidationError`).
