# contract

## Overview
Root intermediate blueprint for shared compile-time wire contracts, sealed error hierarchies, and WebSocket protocols.

## Subdirectories & Boundaries
- [contract:common](./common/README.md) - Shared error primitives (`ErrorCode`, `ServerError`, `ErrorContract`, `CommonServerError`, `ApiErrorDto`).
- [contract:auth](./auth/README.md) - Authentication wire DTOs and `AuthServerError` sealed contracts.
- [contract:chat](./chat/README.md) - Chat DTOs, polymorphic WebSocket frames (`WsClientMessage`, `WsServerEvent`), and `ChatServerError` sealed contracts.
