package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.chat.error.ChatServerError
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class WsServerEvent {

    @Serializable
    @SerialName(NEW_MESSAGE)
    data class NewMessage(
        val message: ChatMessageDto,
    ) : WsServerEvent()

    @Serializable
    @SerialName(MESSAGE_DELETED)
    data class MessageDeleted(
        val roomId: String,
        val messageId: String,
    ) : WsServerEvent()

    @Serializable
    @SerialName(PARTICIPANT_JOINED)
    data class ParticipantJoined(
        val roomId: String,
        val newUsers: List<ChatParticipantDto>,
        val addedBy: ChatParticipantDto? = null,
    ) : WsServerEvent()

    @Serializable
    @SerialName(PARTICIPANT_LEFT)
    data class ParticipantLeft(
        val roomId: String,
        val leftUser: ChatParticipantDto,
        val kickedBy: ChatParticipantDto? = null,
    ) : WsServerEvent()

    @Serializable
    @SerialName(ERROR)
    data class Error(
        val error: ChatServerError.WebSocket,
        val message: String,
        val roomId: String? = null,
        val messageId: String? = null,
    ) : WsServerEvent()

    companion object {
        const val NEW_MESSAGE = "new_message"
        const val MESSAGE_DELETED = "message_deleted"
        const val PARTICIPANT_JOINED = "participant_joined"
        const val PARTICIPANT_LEFT = "participant_left"
        const val ERROR = "error"
    }
}
