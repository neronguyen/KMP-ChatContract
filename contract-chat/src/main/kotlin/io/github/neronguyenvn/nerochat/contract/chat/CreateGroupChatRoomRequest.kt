package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidationError
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidator
import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupChatRoomRequest(
    val participantIds: List<String>,
    val name: String? = null,
) : Validatable<ChatValidationError.CreateGroupRoom> {
    override fun validate(): List<ChatValidationError.CreateGroupRoom> =
        ChatValidator.validateGroupRoom(participantIds = participantIds, name = name)
}
