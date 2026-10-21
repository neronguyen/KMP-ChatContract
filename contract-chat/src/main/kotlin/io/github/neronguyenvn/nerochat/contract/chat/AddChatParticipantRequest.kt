package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidationError
import io.github.neronguyenvn.nerochat.contract.validation.chat.ChatValidator
import kotlinx.serialization.Serializable

@Serializable
data class AddChatParticipantRequest(
    val userIds: List<String>,
) : Validatable<ChatValidationError.AddParticipants> {
    /**
     * Returns the errors from [ChatValidator.validateAddParticipants], or an empty list if valid.
     */
    override fun validate(): List<ChatValidationError.AddParticipants> =
        ChatValidator.validateAddParticipants(userIds = userIds)
}
