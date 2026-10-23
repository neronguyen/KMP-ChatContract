package io.github.neronguyenvn.nerochat.contract.validation.chat

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChatValidatorTest {

    @Test
    fun testValidateSearchEmail() {
        assertNull(ChatValidator.validateSearchEmail("user@example.com", "me@example.com"))

        assertEquals(
            ChatValidationError.EmptySearchEmail,
            ChatValidator.validateSearchEmail("", "me@example.com")
        )
        assertEquals(
            ChatValidationError.InvalidSearchEmail,
            ChatValidator.validateSearchEmail("not-email", "me@example.com")
        )
        assertEquals(
            ChatValidationError.SelfParticipantNotAllowed,
            ChatValidator.validateSearchEmail("me@example.com", "me@example.com")
        )
    }

    @Test
    fun testValidateDirectRoom() {
        val validUuid = "123e4567-e89b-12d3-a456-426614174000"
        assertTrue(ChatValidator.validateDirectRoom(validUuid, "Hello").isEmpty())

        val missingRecipient = ChatValidator.validateDirectRoom("", "Hello")
        assertEquals(1, missingRecipient.size)
        assertTrue(missingRecipient.contains(ChatValidationError.MissingDirectRecipient))

        val invalidRecipient = ChatValidator.validateDirectRoom("bad-uuid", "Hello")
        assertEquals(1, invalidRecipient.size)
        assertTrue(invalidRecipient.contains(ChatValidationError.InvalidTargetUserId))

        val emptyMessage = ChatValidator.validateDirectRoom(validUuid, "   ")
        assertEquals(1, emptyMessage.size)
        assertTrue(emptyMessage.contains(ChatValidationError.EmptyFirstMessage))
    }

    @Test
    fun testValidateGroupRoom() {
        val uuid1 = "123e4567-e89b-12d3-a456-426614174001"
        val uuid2 = "123e4567-e89b-12d3-a456-426614174002"

        // participantIds >= 2
        assertTrue(ChatValidator.validateGroupRoom(listOf(uuid1, uuid2)).isEmpty())

        val nullList = ChatValidator.validateGroupRoom(null)
        assertEquals(1, nullList.size)
        assertTrue(nullList.contains(ChatValidationError.TooFewGroupParticipants))

        val emptyList = ChatValidator.validateGroupRoom(emptyList())
        assertEquals(1, emptyList.size)
        assertTrue(emptyList.contains(ChatValidationError.TooFewGroupParticipants))

        val tooFew = ChatValidator.validateGroupRoom(listOf(uuid1))
        assertEquals(1, tooFew.size)
        assertTrue(tooFew.contains(ChatValidationError.TooFewGroupParticipants))

        val invalidId = ChatValidator.validateGroupRoom(listOf(uuid1, "bad-uuid"))
        assertEquals(1, invalidId.size)
        assertTrue(invalidId.contains(ChatValidationError.InvalidGroupParticipantId))
    }

    @Test
    fun testValidateAddParticipants() {
        val uuid1 = "123e4567-e89b-12d3-a456-426614174001"
        assertTrue(ChatValidator.validateAddParticipants(listOf(uuid1)).isEmpty())

        val empty = ChatValidator.validateAddParticipants(emptyList())
        assertEquals(1, empty.size)
        assertTrue(empty.contains(ChatValidationError.EmptyParticipantIds))

        val invalid = ChatValidator.validateAddParticipants(listOf("invalid-uuid"))
        assertEquals(1, invalid.size)
        assertTrue(invalid.contains(ChatValidationError.InvalidParticipantId))
    }
}
