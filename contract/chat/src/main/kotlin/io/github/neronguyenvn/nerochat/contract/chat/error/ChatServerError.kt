package io.github.neronguyenvn.nerochat.contract.chat.error

import io.github.neronguyenvn.nerochat.contract.error.ErrorCode
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError

// Base chat error interface for broad matching across any chat error
sealed interface ChatServerError : ServerError {
    companion object : ErrorContract<ChatServerError>(
        listOf(
            ChatErrors.ChatParticipantNotFound,
            ChatErrors.ChatRoomNotFound,
            ChatErrors.MessageNotFound,
            ChatErrors.InvalidChatRoomSize,
            ChatErrors.ParticipantAlreadyInRoom,
            ChatErrors.ParticipantNotInRoom,
            ChatErrors.NotMessageSender,
            ChatErrors.DuplicateMessageId,
            ChatErrors.InvalidJson,
            ChatErrors.InvalidPayload,
            ChatErrors.MessageSendFailed,
        )
    )
}

// Top-level per-endpoint contracts
sealed interface GetParticipantError : ServerError {
    companion object : ErrorContract<GetParticipantError>(listOf(ChatErrors.ChatParticipantNotFound))
}

sealed interface GetRoomError : ServerError {
    companion object : ErrorContract<GetRoomError>(listOf(ChatErrors.ChatRoomNotFound))
}

sealed interface GetMessagesError : ServerError {
    companion object : ErrorContract<GetMessagesError>(listOf(ChatErrors.ParticipantNotInRoom))
}

sealed interface CreateRoomError : ServerError {
    companion object : ErrorContract<CreateRoomError>(
        listOf(
            ChatErrors.ChatParticipantNotFound,
            ChatErrors.InvalidChatRoomSize,
        )
    )
}

sealed interface AddParticipantsError : ServerError {
    companion object : ErrorContract<AddParticipantsError>(
        listOf(
            ChatErrors.ChatParticipantNotFound,
            ChatErrors.ChatRoomNotFound,
            ChatErrors.ParticipantAlreadyInRoom,
            ChatErrors.ParticipantNotInRoom,
        )
    )
}

sealed interface LeaveRoomError : ServerError {
    companion object : ErrorContract<LeaveRoomError>(
        listOf(
            ChatErrors.ChatParticipantNotFound,
            ChatErrors.ChatRoomNotFound,
        )
    )
}

sealed interface DeleteMessageError : ServerError {
    companion object : ErrorContract<DeleteMessageError>(
        listOf(
            ChatErrors.MessageNotFound,
            ChatErrors.NotMessageSender,
        )
    )
}

sealed interface WebSocketError : ServerError {
    companion object : ErrorContract<WebSocketError>(
        listOf(
            ChatErrors.ChatRoomNotFound,
            ChatErrors.DuplicateMessageId,
            ChatErrors.InvalidJson,
            ChatErrors.InvalidPayload,
            ChatErrors.MessageSendFailed,
        )
    )
}

// Error Catalog
object ChatErrors {
    data object ChatParticipantNotFound : GetParticipantError, CreateRoomError, AddParticipantsError, LeaveRoomError, ChatServerError {
        override val code = ErrorCode("ChatServerError.ChatParticipantNotFound")
    }

    data object ChatRoomNotFound : GetRoomError, AddParticipantsError, LeaveRoomError, WebSocketError, ChatServerError {
        override val code = ErrorCode("ChatServerError.ChatRoomNotFound")
    }

    data object MessageNotFound : DeleteMessageError, ChatServerError {
        override val code = ErrorCode("ChatServerError.MessageNotFound")
    }

    data object InvalidChatRoomSize : CreateRoomError, ChatServerError {
        override val code = ErrorCode("ChatServerError.InvalidChatRoomSize")
    }

    data object ParticipantAlreadyInRoom : AddParticipantsError, ChatServerError {
        override val code = ErrorCode("ChatServerError.ParticipantAlreadyInRoom")
    }

    data object ParticipantNotInRoom : GetMessagesError, AddParticipantsError, ChatServerError {
        override val code = ErrorCode("ChatServerError.ParticipantNotInRoom")
    }

    data object NotMessageSender : DeleteMessageError, ChatServerError {
        override val code = ErrorCode("ChatServerError.NotMessageSender")
    }

    data object DuplicateMessageId : WebSocketError, ChatServerError {
        override val code = ErrorCode("ChatServerError.DuplicateMessageId")
    }

    data object InvalidJson : WebSocketError, ChatServerError {
        override val code = ErrorCode("ChatServerError.InvalidJson")
    }

    data object InvalidPayload : WebSocketError, ChatServerError {
        override val code = ErrorCode("ChatServerError.InvalidPayload")
    }

    data object MessageSendFailed : WebSocketError, ChatServerError {
        override val code = ErrorCode("ChatServerError.MessageSendFailed")
    }

    val all: List<ChatServerError> = listOf(
        ChatParticipantNotFound,
        ChatRoomNotFound,
        MessageNotFound,
        InvalidChatRoomSize,
        ParticipantAlreadyInRoom,
        ParticipantNotInRoom,
        NotMessageSender,
        DuplicateMessageId,
        InvalidJson,
        InvalidPayload,
        MessageSendFailed,
    )
}
