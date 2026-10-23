package io.github.neronguyenvn.nerochat.contract.validation.chat

import io.github.neronguyenvn.nerochat.contract.validation.ValidationError

/**
 * Pure Kotlin validation errors for chat flows and requests.
 *
 * Each flow/endpoint has a dedicated sub-interface so client UI states and request
 * validators only need to handle the validation errors relevant to that flow.
 */
sealed interface ChatValidationError : ValidationError {

    /** Validation errors when searching for a participant by email in the New Chat flow. */
    sealed interface SearchParticipant : ChatValidationError

    /** Validation errors for creating a 1-on-1 direct chat room (`CreateDirectChatRoomRequest`). */
    sealed interface CreateDirectRoom : ChatValidationError

    /** Validation errors for creating a group chat room (`CreateGroupChatRoomRequest`). */
    sealed interface CreateGroupRoom : ChatValidationError

    /** Validation errors for adding participants to a group (`AddChatParticipantRequest`). */
    sealed interface AddParticipants : ChatValidationError

    data object EmptySearchEmail : SearchParticipant {
        override val field: String = "email"
        override val message: String = "Search email must not be blank"
    }

    data object InvalidSearchEmail : SearchParticipant {
        override val field: String = "email"
        override val message: String = "Invalid email format"
    }

    data object SelfParticipantNotAllowed : SearchParticipant {
        override val field: String = "email"
        override val message: String = "You cannot start a chat with yourself"
    }

    data object MissingDirectRecipient : CreateDirectRoom {
        override val field: String = "targetUserId"
        override val message: String = "Target user ID must not be blank"
    }

    data object InvalidTargetUserId : CreateDirectRoom {
        override val field: String = "targetUserId"
        override val message: String = "Target user ID must be a valid UUID"
    }

    data object EmptyFirstMessage : CreateDirectRoom {
        override val field: String = "message"
        override val message: String = "Message must not be blank"
    }

    data object TooFewGroupParticipants : CreateGroupRoom {
        override val field: String = "participantIds"
        override val message: String = "Group chat rooms must have at least 2 participants"
    }

    data object InvalidGroupParticipantId : CreateGroupRoom {
        override val field: String = "participantIds"
        override val message: String = "Participant ID must be a valid UUID"
    }

    data object EmptyParticipantIds : AddParticipants {
        override val field: String = "userIds"
        override val message: String = "Participant list must not be empty"
    }

    data object InvalidParticipantId : AddParticipants {
        override val field: String = "userIds"
        override val message: String = "Participant ID must be a valid UUID"
    }
}
