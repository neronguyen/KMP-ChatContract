package io.github.neronguyenvn.nerochat.contract.validation.chat

import io.github.neronguyenvn.nerochat.contract.validation.EmailValidator
import io.github.neronguyenvn.nerochat.contract.validation.UuidValidator

/**
 * Pure Kotlin validator for chat inputs, forms, and request payloads.
 */
object ChatValidator {

    /**
     * Returns the first error for a null or blank email, invalid email format, or the user's own email,
     * or `null` if valid. Format validation uses the untrimmed input.
     *
     * @param currentUserEmail when nonnull, rejects a match after trimming both emails and ignoring case.
     */
    fun validateSearchEmail(
        email: String?,
        currentUserEmail: String? = null,
    ): ChatValidationError.SearchParticipant? {
        if (email.isNullOrBlank()) return ChatValidationError.EmptySearchEmail
        if (!EmailValidator.isValidEmail(email)) return ChatValidationError.InvalidSearchEmail
        if (currentUserEmail != null && email.trim().equals(currentUserEmail.trim(), ignoreCase = true)) {
            return ChatValidationError.SelfParticipantNotAllowed
        }
        return null
    }

    /**
     * Checks for a nonblank UUID recipient and a nonblank first message.
     * Returns at most one error per field in recipient, message order, or an empty list.
     * Null and blank inputs produce the corresponding missing-recipient or empty-message errors.
     */
    fun validateDirectRoom(
        targetUserId: String?,
        message: String?,
    ): List<ChatValidationError.CreateDirectRoom> {
        val errors = mutableListOf<ChatValidationError.CreateDirectRoom>()
        if (targetUserId.isNullOrBlank()) {
            errors.add(ChatValidationError.MissingDirectRecipient)
        } else if (!UuidValidator.isValidUuid(targetUserId)) {
            errors.add(ChatValidationError.InvalidTargetUserId)
        }

        if (message.isNullOrBlank()) {
            errors.add(ChatValidationError.EmptyFirstMessage)
        }
        return errors
    }

    /**
     * Returns a list containing `TooFewGroupParticipants` for a null list or fewer than two entries;
     * otherwise contains `InvalidGroupParticipantId` if any entry fails [UuidValidator.isValidUuid].
     * Returns an empty list if valid. Duplicate IDs count as separate entries.
     */
    fun validateGroupRoom(
        participantIds: List<String>?,
    ): List<ChatValidationError.CreateGroupRoom> {
        if (participantIds == null || participantIds.size < 2) {
            return listOf(ChatValidationError.TooFewGroupParticipants)
        }
        for (id in participantIds) {
            if (!UuidValidator.isValidUuid(id)) {
                return listOf(ChatValidationError.InvalidGroupParticipantId)
            }
        }
        return emptyList()
    }

    /**
     * Returns a list containing `EmptyParticipantIds` for a null or empty list, or one `InvalidParticipantId`
     * if any entry fails [UuidValidator.isValidUuid]. Returns an empty list if valid.
     * Duplicate IDs are accepted.
     */
    fun validateAddParticipants(
        userIds: List<String>?,
    ): List<ChatValidationError.AddParticipants> {
        if (userIds.isNullOrEmpty()) {
            return listOf(ChatValidationError.EmptyParticipantIds)
        }
        for (id in userIds) {
            if (!UuidValidator.isValidUuid(id)) {
                return listOf(ChatValidationError.InvalidParticipantId)
            }
        }
        return emptyList()
    }
}
