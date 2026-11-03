package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidationError
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidator
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator(WsClientMessage.CLASS_DISCRIMINATOR)
sealed class WsClientMessage {

    @Serializable
    @SerialName(NEW_MESSAGE)
    data class NewMessage(
        val roomId: String,
        val content: String,
        val messageId: String? = null,
    ) : WsClientMessage(), Validatable<ChatValidationError.WebSocket> {
        override fun validate(): List<ChatValidationError.WebSocket> =
            ChatValidator.validateNewMessage(
                roomId = roomId,
                content = content,
                messageId = messageId,
            )
    }

    companion object {
        const val CLASS_DISCRIMINATOR = "type"
        const val NEW_MESSAGE = "new_message"
    }
}
