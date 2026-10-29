package io.github.neronguyenvn.nerochat.contract.chat.error

import io.github.neronguyenvn.nerochat.contract.error.ErrorCode
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError

object ChatServerError {
    sealed interface GetParticipant : ServerError {
        companion object : ErrorContract<GetParticipant>(listOf(ChatParticipantNotFound))
    }

    sealed interface GetRoom : ServerError {
        companion object : ErrorContract<GetRoom>(listOf(ChatRoomNotFound))
    }

    sealed interface GetMessages : ServerError {
        companion object : ErrorContract<GetMessages>(listOf(ParticipantNotInRoom))
    }

    sealed interface CreateRoom : ServerError {
        companion object : ErrorContract<CreateRoom>(
            listOf(
                ChatParticipantNotFound,
                InvalidChatRoomSize,
            )
        )
    }

    sealed interface AddParticipants : ServerError {
        companion object : ErrorContract<AddParticipants>(
            listOf(
                ChatParticipantNotFound,
                ChatRoomNotFound,
                ParticipantAlreadyInRoom,
                ParticipantNotInRoom,
            )
        )
    }

    sealed interface LeaveRoom : ServerError {
        companion object : ErrorContract<LeaveRoom>(
            listOf(
                ChatParticipantNotFound,
                ChatRoomNotFound,
            )
        )
    }

    sealed interface DeleteMessage : ServerError {
        companion object : ErrorContract<DeleteMessage>(
            listOf(
                MessageNotFound,
                NotMessageSender,
            )
        )
    }

    sealed interface WebSocket : ServerError {
        companion object : ErrorContract<WebSocket>(
            listOf(
                ChatRoomNotFound,
                DuplicateMessageId,
                InvalidJson,
                InvalidPayload,
                MessageSendFailed,
            )
        )
    }

    data object ChatParticipantNotFound : GetParticipant, CreateRoom, AddParticipants, LeaveRoom {
        override val code = ErrorCode("ChatServerError.ChatParticipantNotFound")
    }

    data object ChatRoomNotFound : GetRoom, AddParticipants, LeaveRoom, WebSocket {
        override val code = ErrorCode("ChatServerError.ChatRoomNotFound")
    }

    data object MessageNotFound : DeleteMessage {
        override val code = ErrorCode("ChatServerError.MessageNotFound")
    }

    data object InvalidChatRoomSize : CreateRoom {
        override val code = ErrorCode("ChatServerError.InvalidChatRoomSize")
    }

    data object ParticipantAlreadyInRoom : AddParticipants {
        override val code = ErrorCode("ChatServerError.ParticipantAlreadyInRoom")
    }

    data object ParticipantNotInRoom : GetMessages, AddParticipants {
        override val code = ErrorCode("ChatServerError.ParticipantNotInRoom")
    }

    data object NotMessageSender : DeleteMessage {
        override val code = ErrorCode("ChatServerError.NotMessageSender")
    }

    data object DuplicateMessageId : WebSocket {
        override val code = ErrorCode("ChatServerError.DuplicateMessageId")
    }

    data object InvalidJson : WebSocket {
        override val code = ErrorCode("ChatServerError.InvalidJson")
    }

    data object InvalidPayload : WebSocket {
        override val code = ErrorCode("ChatServerError.InvalidPayload")
    }

    data object MessageSendFailed : WebSocket {
        override val code = ErrorCode("ChatServerError.MessageSendFailed")
    }
}
