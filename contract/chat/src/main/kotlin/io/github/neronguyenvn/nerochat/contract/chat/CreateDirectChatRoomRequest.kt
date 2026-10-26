package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidationError
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidator
import kotlinx.serialization.Serializable

@Serializable
data class CreateDirectChatRoomRequest(
    val targetUserId: String,
    val message: String,
) : Validatable<ChatValidationError.CreateDirectRoom> {
    /**
     * Returns the errors from [ChatValidator.validateDirectRoom], or an empty list if valid.
     */
    override fun validate(): List<ChatValidationError.CreateDirectRoom> =
        ChatValidator.validateDirectRoom(targetUserId = targetUserId, message = message)
}
