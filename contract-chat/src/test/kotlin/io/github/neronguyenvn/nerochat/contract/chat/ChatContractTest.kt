package io.github.neronguyenvn.nerochat.contract.chat

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ChatContractTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `ChatRoomDto serializes and deserializes correctly`() {
        val now = Clock.System.now()
        val creator = ChatParticipantDto(
            userId = "user-1",
            email = "user1@example.com",
            displayName = "User 1",
            profilePictureUrl = null,
        )
        val room = ChatRoomDto(
            id = "room-1",
            type = ChatRoomType.DIRECT,
            name = null,
            creator = creator,
            participants = listOf(creator),
            lastMessage = null,
            lastActivityAt = now,
        )

        val serialized = json.encodeToString(room)
        val deserialized = json.decodeFromString<ChatRoomDto>(serialized)
        assertEquals(room, deserialized)
    }

    @Test
    fun `IncomingWsMessage polymorphic serialization and deserialization`() {
        val message = IncomingWsMessage.NewMessage(
            roomId = "room-123",
            content = "Hello world",
            messageId = "msg-abc",
        )

        val serialized = json.encodeToString<IncomingWsMessage>(message)
        assertTrue(serialized.contains("\"type\":\"new_message\""))

        val deserialized = json.decodeFromString<IncomingWsMessage>(serialized)
        assertEquals(message, deserialized)
    }

    @Test
    fun `OutgoingWsMessage polymorphic serialization and deserialization`() {
        val now = Clock.System.now()
        val chatMessage = ChatMessageDto(
            id = "msg-123",
            roomId = "room-456",
            senderId = "user-789",
            content = "Hi there",
            createdAt = now,
        )
        val message = OutgoingWsMessage.NewMessage(message = chatMessage)

        val serialized = json.encodeToString<OutgoingWsMessage>(message)
        assertTrue(serialized.contains("\"type\":\"new_message\""))

        val deserialized = json.decodeFromString<OutgoingWsMessage>(serialized)
        assertEquals(message, deserialized)

        val error = OutgoingWsMessage.Error(code = "UNAUTHORIZED", message = "Not allowed")
        val serializedError = json.encodeToString<OutgoingWsMessage>(error)
        assertTrue(serializedError.contains("\"type\":\"error\""))
        assertEquals(error, json.decodeFromString<OutgoingWsMessage>(serializedError))
    }
}
