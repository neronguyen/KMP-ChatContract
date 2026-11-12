# KMP-ChatContract — Architecture & Design Registry

This is the unified documentation index for `KMP-ChatContract`, serving as the **Single Source of Truth (SSOT)** for C4
Level 1/2 architecture, cross-repository client–server wire flows, and Elephant-and-Goldfish Model (EGM) technical
design documents.

> **Governance & Authoring Rules**: Follow global agent lifecycle rules in `~/.gemini/config/AGENTS.md`, architectural
> philosophy in [`PHILOSOPHY.md`](./PHILOSOPHY.md), and repository boundary invariants in [`../AGENTS.md`](../AGENTS.md).

---

## 1. System & Container Architecture (C4 Level 1 & Level 2)

| Diagram                         | Level      | Status    | Specification (JSON)                                                                       | Interactive Viewer (HTML)                                                     | Static Assets (Dark / Light)                                                                             |
|---------------------------------|------------|-----------|--------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| **Chat System Context**         | C4 Level 1 | `[AS-IS]` | [`system-context.archify.json`](./architecture/specifications/system-context.archify.json) | [`c4-system-context.html`](./architecture/interactive/c4-system-context.html) | [Dark](./architecture/assets/SystemContext.png) · [Light](./architecture/assets/SystemContext.light.png) |
| **Chat Container Architecture** | C4 Level 2 | `[AS-IS]` | [`containers.archify.json`](./architecture/specifications/containers.archify.json)         | [`c4-containers.html`](./architecture/interactive/c4-containers.html)         | [Dark](./architecture/assets/Containers.png) · [Light](./architecture/assets/Containers.light.png)       |

---

## 2. Cross-Repository Wire Flows & Lifecycles (Master SSOT)

| Flow Name                         | Type       | Status    | Specification & Artifacts                                                                                                                                                                                                                                          | Linked Design Spec | Asserting Test Suite                                                     |
|-----------------------------------|------------|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|--------------------------------------------------------------------------|
| **WebSocket Lifecycle & Backoff** | `sequence` | `[AS-IS]` | [Spec](./architecture/specifications/flows/ws-lifecycle.sequence.archify.json) · [Viewer](./architecture/interactive/flows/ws-lifecycle.html) · [Dark](./architecture/assets/ws-lifecycle.png) · [Light](./architecture/assets/ws-lifecycle.light.png)             | —                  | `:core:network` MockWebServer WS Suite & `RealtimeConnectionManagerTest` |
| **Single-Flight Token Refresh**   | `sequence` | `[AS-IS]` | [Spec](./architecture/specifications/flows/token-refresh.sequence.archify.json) · [Viewer](./architecture/interactive/flows/token-refresh.html) · [Dark](./architecture/assets/token-refresh.png) · [Light](./architecture/assets/token-refresh.light.png)         | —                  | `TokenAuthenticatorTest` & `RetrofitAuthNetworkTest`                     |
| **Optimistic Send & Idempotency** | `sequence` | `[AS-IS]` | [Spec](./architecture/specifications/flows/optimistic-send.sequence.archify.json) · [Viewer](./architecture/interactive/flows/optimistic-send.html) · [Dark](./architecture/assets/optimistic-send.png) · [Light](./architecture/assets/optimistic-send.light.png) | —                  | `ChatMessageRepositoryTest` & Backend `ChatWebSocketHandlerTest`         |
| **Numeric OTP Verification & Lockout** | `sequence` | `[PLANNED]` | [Spec](./architecture/specifications/flows/otp-verification.sequence.archify.json) · [Viewer](./architecture/interactive/flows/otp-verification.html) · [Dark](./architecture/assets/otp-verification.png) · [Light](./architecture/assets/otp-verification.light.png) | [otp-email-verification](designs/otp-email-verification.md) | Backend `EmailVerificationServiceTest` & `:feature:auth` UI Tests |

---

## 3. Technical Design Documents (EGM)

| Design Document                                                                          | Date       | Status  | Target Modules                                                 | Linked Archify Specs                                                                                       | Summary                                                                              |
|------------------------------------------------------------------------------------------|------------|---------|----------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| [**6-Digit OTP Email Verification & Password Reset**](designs/otp-email-verification.md) | 2026-11-12 | `Draft` | `:contract:auth`, `:validation:auth`, `:user`, `:feature:auth` | [`otp-verification` (Planned)](./architecture/specifications/flows/otp-verification.sequence.archify.json) | Cross-repo migration from email deep links to 6-digit OTP backed purely by Redisson. |
