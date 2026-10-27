package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.chat.error.ChatServerError
import io.github.neronguyenvn.nerochat.contract.error.ApiErrorResponse
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
    fun `WsClientMessage polymorphic serialization and deserialization`() {
        val message = WsClientMessage.NewMessage(
            roomId = "room-123",
            content = "Hello world",
            messageId = "msg-abc",
        )

        val serialized = json.encodeToString<WsClientMessage>(message)
        assertTrue(serialized.contains("\"type\":\"new_message\""))

        val deserialized = json.decodeFromString<WsClientMessage>(serialized)
        assertEquals(message, deserialized)
    }

    @Test
    fun `WsServerEvent polymorphic serialization and deserialization`() {
        val now = Clock.System.now()
        val chatMessage = ChatMessageDto(
            id = "msg-123",
            roomId = "room-456",
            senderId = "user-789",
            content = "Hi there",
            createdAt = now,
        )
        val message = WsServerEvent.NewMessage(message = chatMessage)

        val serialized = json.encodeToString<WsServerEvent>(message)
        assertTrue(serialized.contains("\"type\":\"new_message\""))

        val deserialized = json.decodeFromString<WsServerEvent>(serialized)
        assertEquals(message, deserialized)

        val error = WsServerEvent.Error(error = ChatServerError.ChatRoomNotFound, message = "Not allowed")
        val serializedError = json.encodeToString<WsServerEvent>(error)
        assertTrue(serializedError.contains("\"type\":\"error\""))
        assertEquals(error, json.decodeFromString<WsServerEvent>(serializedError))

        val correlatedError = WsServerEvent.Error(
            error = ChatServerError.ChatRoomNotFound,
            message = "Room not found",
            roomId = "room-456",
            messageId = "msg-123",
        )
        val serializedCorrelated = json.encodeToString<WsServerEvent>(correlatedError)
        assertEquals(
            correlatedError,
            json.decodeFromString<WsServerEvent>(serializedCorrelated)
        )
    }

    @Test
    fun `ChatServerError serializes polymorphically and exhaustive sub-interfaces match correctly`() {
        val errorResponse = ApiErrorResponse<ChatServerError>(
            error = ChatServerError.ChatRoomNotFound,
            message = "Room not found",
        )
        val serialized = json.encodeToString(errorResponse)
        val deserialized = json.decodeFromString<ApiErrorResponse<ChatServerError>>(serialized)
        assertEquals(ChatServerError.ChatRoomNotFound, deserialized.error)

        val deleteResponse = ApiErrorResponse<ChatServerError.DeleteMessage>(
            error = ChatServerError.NotMessageSender,
            message = "Cannot delete message",
        )
        val serializedDelete = json.encodeToString(deleteResponse)
        val deserializedDelete = json.decodeFromString<ApiErrorResponse<ChatServerError.DeleteMessage>>(serializedDelete)
        assertEquals(ChatServerError.NotMessageSender, deserializedDelete.error)

        val deleteError: ChatServerError.DeleteMessage = deserializedDelete.error
        val result = when (deleteError) {
            ChatServerError.MessageNotFound -> "not found"
            ChatServerError.NotMessageSender -> "not sender"
        }
        assertEquals("not sender", result)

        val getMessagesError: ChatServerError.GetMessages = ChatServerError.ParticipantNotInRoom
        val msgResult = when (getMessagesError) {
            ChatServerError.ParticipantNotInRoom -> "not in room"
        }
        assertEquals("not in room", msgResult)
    }

    @Test
    fun `CreateDirectChatRoomRequest serializes and validates correctly`() {
        val validUuid = "123e4567-e89b-12d3-a456-426614174000"
        val req = CreateDirectChatRoomRequest(targetUserId = validUuid, message = "Hello")
        val jsonStr = json.encodeToString(req)
        val deserialized = json.decodeFromString<CreateDirectChatRoomRequest>(jsonStr)
        assertEquals(req, deserialized)
        assertTrue(req.validate().isEmpty())

        val invalid = CreateDirectChatRoomRequest(targetUserId = "not-a-uuid", message = "")
        assertEquals(2, invalid.validate().size)
    }

    @Test
    fun `CreateGroupChatRoomRequest serializes and validates correctly`() {
        val uuid1 = "123e4567-e89b-12d3-a456-426614174001"
        val uuid2 = "123e4567-e89b-12d3-a456-426614174002"

        // Name is optional
        val reqWithoutName = CreateGroupChatRoomRequest(participantIds = listOf(uuid1, uuid2))
        val jsonStr = json.encodeToString(reqWithoutName)
        val deserialized = json.decodeFromString<CreateGroupChatRoomRequest>(jsonStr)
        assertEquals(reqWithoutName, deserialized)
        assertTrue(reqWithoutName.validate().isEmpty())

        val reqWithName = CreateGroupChatRoomRequest(name = "Team", participantIds = listOf(uuid1, uuid2))
        assertTrue(reqWithName.validate().isEmpty())

        val invalid = CreateGroupChatRoomRequest(participantIds = listOf(uuid1))
        assertEquals(1, invalid.validate().size)
    }

    @Test
    fun `AddChatParticipantRequest serializes and validates correctly`() {
        val uuid1 = "123e4567-e89b-12d3-a456-426614174001"
        val req = AddChatParticipantRequest(userIds = listOf(uuid1))
        val jsonStr = json.encodeToString(req)
        val deserialized = json.decodeFromString<AddChatParticipantRequest>(jsonStr)
        assertEquals(req, deserialized)
        assertTrue(req.validate().isEmpty())

        val invalid = AddChatParticipantRequest(userIds = emptyList())
        assertEquals(1, invalid.validate().size)
    }
}
