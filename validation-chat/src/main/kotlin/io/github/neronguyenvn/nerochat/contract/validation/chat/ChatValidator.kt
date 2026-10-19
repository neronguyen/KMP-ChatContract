package io.github.neronguyenvn.nerochat.contract.validation.chat

import io.github.neronguyenvn.nerochat.contract.validation.ValidationPatterns

/**
 * Pure Kotlin validator for chat inputs, forms, and request payloads.
 */
object ChatValidator {

    fun validateSearchEmail(
        email: String?,
        currentUserEmail: String? = null,
    ): ChatValidationError.SearchParticipant? {
        if (email.isNullOrBlank()) return ChatValidationError.EmptySearchEmail
        if (!ValidationPatterns.isValidEmail(email)) return ChatValidationError.InvalidSearchEmail
        if (currentUserEmail != null && email.trim().equals(currentUserEmail.trim(), ignoreCase = true)) {
            return ChatValidationError.SelfParticipantNotAllowed
        }
        return null
    }

    fun validateDirectRoom(
        targetUserId: String?,
        message: String?,
    ): List<ChatValidationError.CreateDirectRoom> {
        val errors = mutableListOf<ChatValidationError.CreateDirectRoom>()
        if (targetUserId.isNullOrBlank()) {
            errors.add(ChatValidationError.MissingDirectRecipient)
        } else if (!ValidationPatterns.isValidUuid(targetUserId)) {
            errors.add(ChatValidationError.InvalidTargetUserId)
        }

        if (message.isNullOrBlank()) {
            errors.add(ChatValidationError.EmptyFirstMessage)
        }
        return errors
    }

    fun validateGroupRoom(
        participantIds: List<String>?,
        @Suppress("UNUSED_PARAMETER") name: String? = null,
    ): List<ChatValidationError.CreateGroupRoom> {
        val errors = mutableListOf<ChatValidationError.CreateGroupRoom>()
        if (participantIds.isNullOrEmpty() || participantIds.size < 2) {
            errors.add(ChatValidationError.TooFewGroupParticipants)
        } else {
            for (id in participantIds) {
                if (!ValidationPatterns.isValidUuid(id)) {
                    errors.add(ChatValidationError.InvalidGroupParticipantId)
                    break
                }
            }
        }
        return errors
    }

    fun validateAddParticipants(
        userIds: List<String>?,
    ): List<ChatValidationError.AddParticipants> {
        val errors = mutableListOf<ChatValidationError.AddParticipants>()
        if (userIds.isNullOrEmpty()) {
            errors.add(ChatValidationError.EmptyParticipantIds)
        } else {
            for (id in userIds) {
                if (!ValidationPatterns.isValidUuid(id)) {
                    errors.add(ChatValidationError.InvalidParticipantId)
                    break
                }
            }
        }
        return errors
    }
}
