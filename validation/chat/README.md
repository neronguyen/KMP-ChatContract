# validation:chat

## Overview
Chat message, room creation, and participant validation rules implementing `Validatable` for direct messaging, group rooms, and WebSocket frames.

## Interface
- `[ChatValidator.kt:ChatValidator]`: Central singleton validator for direct/group room requests, participant additions, search, and message frames.
- `[ChatValidationError.kt:ChatValidationError]`: Sealed hierarchy of chat validation errors (`EmptyFirstMessage`, `MissingDirectRecipient`, `InvalidTargetUserId`, `EmptyParticipantIds`, `InvalidParticipantId`, `TooFewGroupParticipants`, `InvalidGroupParticipantId`, `EmptySearchEmail`, `InvalidSearchEmail`, `SelfParticipantNotAllowed`, `InvalidRoomId`, `InvalidMessageId`, `EmptyMessageContent`).

## Invariants
- Depends exclusively on `:validation:common`.
- Direct rooms require a non-blank valid UUID target user ID and non-blank initial message.
- Group rooms require at least two distinct valid UUID participant IDs.
- WebSocket message content cannot be blank.

## Side Effects
- None (pure CPU in-memory validation functions).

## Verification
- Run: `./gradlew :validation:chat:test --rerun-tasks`
