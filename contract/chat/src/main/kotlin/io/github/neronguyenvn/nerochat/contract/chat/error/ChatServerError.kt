package io.github.neronguyenvn.nerochat.contract.chat.error

import io.github.neronguyenvn.nerochat.contract.error.ServerError
import kotlinx.serialization.Serializable

@Serializable
sealed interface ChatServerError : ServerError {
    // Per-endpoint & WebSocket error contracts
    @Serializable sealed interface GetRooms : ChatServerError
    @Serializable sealed interface GetParticipant : ChatServerError
    @Serializable sealed interface GetRoom : ChatServerError
    @Serializable sealed interface GetMessages : ChatServerError
    @Serializable sealed interface CreateRoom : ChatServerError
    @Serializable sealed interface AddParticipants : ChatServerError
    @Serializable sealed interface LeaveRoom : ChatServerError
    @Serializable sealed interface DeleteMessage : ChatServerError
    @Serializable sealed interface WebSocket : ChatServerError

    @Serializable data object ChatParticipantNotFound : GetParticipant, CreateRoom, AddParticipants, LeaveRoom
    @Serializable data object ChatRoomNotFound : GetRoom, AddParticipants, LeaveRoom, WebSocket
    @Serializable data object MessageNotFound : DeleteMessage
    @Serializable data object InvalidChatRoomSize : CreateRoom
    @Serializable data object ParticipantAlreadyInRoom : AddParticipants
    @Serializable data object Forbidden : GetMessages, AddParticipants, DeleteMessage
    @Serializable data object ValidationFailed : CreateRoom, AddParticipants
    @Serializable
    data object Unauthorized :
        GetRooms,
        GetParticipant,
        GetRoom,
        GetMessages,
        CreateRoom,
        AddParticipants,
        LeaveRoom,
        DeleteMessage
    @Serializable
    data object BadRequest :
        GetRoom,
        GetMessages,
        AddParticipants,
        LeaveRoom,
        DeleteMessage
    @Serializable data object DuplicateMessageId : WebSocket
    @Serializable data object InvalidJson : WebSocket
    @Serializable data object InvalidPayload : WebSocket
    @Serializable data object MessageSendFailed : WebSocket
}
