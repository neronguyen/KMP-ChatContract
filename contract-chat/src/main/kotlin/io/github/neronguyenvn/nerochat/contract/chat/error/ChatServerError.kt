package io.github.neronguyenvn.nerochat.contract.chat.error

import io.github.neronguyenvn.nerochat.contract.error.ServerError

sealed interface ChatServerError : ServerError {
    override val code: String

    // Per-endpoint & WebSocket error contracts
    sealed interface GetRooms : ChatServerError
    sealed interface GetParticipant : ChatServerError
    sealed interface GetRoom : ChatServerError
    sealed interface GetMessages : ChatServerError
    sealed interface CreateRoom : ChatServerError
    sealed interface AddParticipants : ChatServerError
    sealed interface LeaveRoom : ChatServerError
    sealed interface DeleteMessage : ChatServerError
    sealed interface WebSocket : ChatServerError

    data object ChatParticipantNotFound : GetParticipant, CreateRoom, AddParticipants, LeaveRoom {
        override val code = "CHAT_PARTICIPANT_NOT_FOUND"
    }

    data object ChatRoomNotFound : GetRoom, AddParticipants, LeaveRoom, WebSocket {
        override val code = "CHAT_ROOM_NOT_FOUND"
    }

    data object MessageNotFound : DeleteMessage {
        override val code = "MESSAGE_NOT_FOUND"
    }

    data object InvalidChatRoomSize : CreateRoom {
        override val code = "INVALID_CHAT_ROOM_SIZE"
    }

    data object ParticipantAlreadyInRoom : AddParticipants {
        override val code = "PARTICIPANT_ALREADY_IN_ROOM"
    }

    data object Forbidden : GetMessages, AddParticipants, DeleteMessage {
        override val code = ServerError.FORBIDDEN
    }

    data object ValidationFailed : CreateRoom, AddParticipants {
        override val code = ServerError.VALIDATION_FAILED
    }

    data object Unauthorized :
        GetRooms,
        GetParticipant,
        GetRoom,
        GetMessages,
        CreateRoom,
        AddParticipants,
        LeaveRoom,
        DeleteMessage {
        override val code = ServerError.UNAUTHORIZED
    }

    data object BadRequest :
        GetRoom,
        GetMessages,
        AddParticipants,
        LeaveRoom,
        DeleteMessage {
        override val code = ServerError.BAD_REQUEST
    }

    data object DuplicateMessageId : WebSocket {
        override val code = "DUPLICATE_MESSAGE_ID"
    }

    data object InvalidJson : WebSocket {
        override val code = "INVALID_JSON"
    }

    data object InvalidPayload : WebSocket {
        override val code = "INVALID_PAYLOAD"
    }

    data object MessageSendFailed : WebSocket {
        override val code = "MESSAGE_SEND_FAILED"
    }

    companion object {
        val entries: List<ChatServerError> = listOf(
            ChatParticipantNotFound,
            ChatRoomNotFound,
            MessageNotFound,
            InvalidChatRoomSize,
            ParticipantAlreadyInRoom,
            Forbidden,
            ValidationFailed,
            Unauthorized,
            BadRequest,
            DuplicateMessageId,
            InvalidJson,
            InvalidPayload,
            MessageSendFailed,
        )

        fun fromCode(code: String): ChatServerError? = entries.find { it.code == code }
    }
}
