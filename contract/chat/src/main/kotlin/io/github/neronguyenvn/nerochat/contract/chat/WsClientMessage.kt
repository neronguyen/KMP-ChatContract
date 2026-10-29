package io.github.neronguyenvn.nerochat.contract.chat

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
    ) : WsClientMessage()

    companion object {
        const val CLASS_DISCRIMINATOR = "type"
        const val NEW_MESSAGE = "new_message"
    }
}
