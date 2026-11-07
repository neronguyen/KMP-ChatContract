# contract:chat

## Overview
Chat domain wire DTOs, room/message request models implementing `Validatable`, bidirectional WebSocket message/event frames, and sealed `ChatServerError` hierarchies.

## Interface
- `[ChatRoomDto.kt:ChatRoomDto]`: Chat room wire model (`id`, `type: ChatRoomType`, `name`, `creator: ChatParticipantDto`, `participants`, `lastMessage`, `lastActivityAt`).
- `[ChatParticipantDto.kt:ChatParticipantDto]`: Room participant wire model (`userId`, `email`, `displayName`, `profilePictureUrl`).
- `[ChatMessageDto.kt:ChatMessageDto]`: Message wire model (`id`, `roomId`, `senderId`, `content`, `createdAt`, `deletedAt`).
- `[ChatMessageSliceDto.kt:ChatMessageSliceDto]`: Cursor-paginated message slice (`messages`, `hasMore`).
- `[ChatRoomType.kt:ChatRoomType]`: Enum for room categories (`DIRECT`, `GROUP`).
- `[CreateDirectChatRoomRequest.kt:CreateDirectChatRoomRequest]`: Validatable direct chat room creation request.
- `[CreateGroupChatRoomRequest.kt:CreateGroupChatRoomRequest]`: Validatable group room creation request.
- `[AddChatParticipantRequest.kt:AddChatParticipantRequest]`: Validatable participant addition request.
- `[WsClientMessage.kt:WsClientMessage]`: Polymorphic client-to-server WebSocket frames (`classDiscriminator = "type"`).
- `[WsServerEvent.kt:WsServerEvent]`: Polymorphic server-to-client WebSocket event frames (`classDiscriminator = "type"`).
- `[ChatServerError.kt:ChatServerError]`: Per-endpoint error hierarchies for `GetParticipant`, `GetRoom`, `GetMessages`, `CreateRoom`, `AddParticipants`, `LeaveRoom`, `DeleteMessage`, `WebSocket`.

## Invariants
- Depends on `:contract:common` and `:validation:chat`.
- WebSocket frames (`WsClientMessage`, `WsServerEvent`) strictly enforce `@JsonClassDiscriminator("type")`.
- Every `ChatServerError` entry must be cataloged in its companion `ErrorContract` and match `error-codes-golden.txt`.

## Side Effects
- None (pure data contracts and serialization schemas).

## Verification
- Run: `./gradlew :contract:chat:test --rerun-tasks`
