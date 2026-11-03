package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.chat.error.ChatServerError
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ChatGoldenWireTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `CreateDirectChatRoomRequest matches golden wire format`() {
        val req = CreateDirectChatRoomRequest(
            targetUserId = "018f3a2b-8a7e-7000-8000-000000000002",
            message = "Hello there!",
        )
        val expected = """{"targetUserId":"018f3a2b-8a7e-7000-8000-000000000002","message":"Hello there!"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<CreateDirectChatRoomRequest>(expected))
    }

    @Test
    fun `CreateGroupChatRoomRequest matches golden wire format`() {
        val req = CreateGroupChatRoomRequest(
            participantIds = listOf("018f3a2b-8a7e-7000-8000-000000000002", "018f3a2b-8a7e-7000-8000-000000000003"),
            name = "Project Team",
        )
        val expected = """{"participantIds":["018f3a2b-8a7e-7000-8000-000000000002","018f3a2b-8a7e-7000-8000-000000000003"],"name":"Project Team"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<CreateGroupChatRoomRequest>(expected))
    }

    @Test
    fun `AddChatParticipantRequest matches golden wire format`() {
        val req = AddChatParticipantRequest(
            userIds = listOf("018f3a2b-8a7e-7000-8000-000000000004"),
        )
        val expected = """{"userIds":["018f3a2b-8a7e-7000-8000-000000000004"]}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<AddChatParticipantRequest>(expected))
    }

    @Test
    fun `ChatMessageDto and ChatMessageSliceDto match golden wire format`() {
        val msg = ChatMessageDto(
            id = "018f3a2b-8a7e-7000-8000-000000000010",
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            senderId = "018f3a2b-8a7e-7000-8000-000000000001",
            content = "Golden message",
            createdAt = Instant.parse("2026-11-03T20:00:00Z"),
        )
        val expectedMsg = """{"id":"018f3a2b-8a7e-7000-8000-000000000010","roomId":"018f3a2b-8a7e-7000-8000-000000000020","senderId":"018f3a2b-8a7e-7000-8000-000000000001","content":"Golden message","createdAt":"2026-11-03T20:00:00Z"}"""
        assertEquals(expectedMsg, json.encodeToString(msg))
        assertEquals(msg, json.decodeFromString<ChatMessageDto>(expectedMsg))

        val slice = ChatMessageSliceDto(
            messages = listOf(msg),
            hasMore = false,
        )
        val expectedSlice = """{"messages":[$expectedMsg],"hasMore":false}"""
        assertEquals(expectedSlice, json.encodeToString(slice))
        assertEquals(slice, json.decodeFromString<ChatMessageSliceDto>(expectedSlice))
    }

    @Test
    fun `ChatRoomDto matches golden wire format`() {
        val creator = ChatParticipantDto(
            userId = "018f3a2b-8a7e-7000-8000-000000000001",
            email = "alice@example.com",
            displayName = "Alice",
            profilePictureUrl = null,
        )
        val room = ChatRoomDto(
            id = "018f3a2b-8a7e-7000-8000-000000000020",
            type = ChatRoomType.DIRECT,
            name = null,
            creator = creator,
            participants = listOf(creator),
            lastMessage = null,
            lastActivityAt = Instant.parse("2026-11-03T20:00:00Z"),
        )
        val expected = """{"id":"018f3a2b-8a7e-7000-8000-000000000020","type":"DIRECT","creator":{"userId":"018f3a2b-8a7e-7000-8000-000000000001","email":"alice@example.com","displayName":"Alice"},"participants":[{"userId":"018f3a2b-8a7e-7000-8000-000000000001","email":"alice@example.com","displayName":"Alice"}],"lastActivityAt":"2026-11-03T20:00:00Z"}"""
        assertEquals(expected, json.encodeToString(room))
        assertEquals(room, json.decodeFromString<ChatRoomDto>(expected))
    }

    @Test
    fun `WsClientMessage polymorphic golden wire format`() {
        val msg: WsClientMessage = WsClientMessage.NewMessage(
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            content = "Hello via WS",
            messageId = "018f3a2b-8a7e-7000-8000-000000000099",
        )
        val expected = """{"type":"new_message","roomId":"018f3a2b-8a7e-7000-8000-000000000020","content":"Hello via WS","messageId":"018f3a2b-8a7e-7000-8000-000000000099"}"""
        assertEquals(expected, json.encodeToString<WsClientMessage>(msg))
        assertEquals(msg, json.decodeFromString<WsClientMessage>(expected))
    }

    @Test
    fun `WsServerEvent polymorphic golden wire formats for all subtypes`() {
        val chatMessage = ChatMessageDto(
            id = "018f3a2b-8a7e-7000-8000-000000000010",
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            senderId = "018f3a2b-8a7e-7000-8000-000000000001",
            content = "Broadcast message",
            createdAt = Instant.parse("2026-11-03T20:00:00Z"),
        )
        val user = ChatParticipantDto(
            userId = "018f3a2b-8a7e-7000-8000-000000000002",
            email = "bob@example.com",
            displayName = "Bob",
            profilePictureUrl = null,
        )

        // 1. NewMessage
        val eventNewMessage: WsServerEvent = WsServerEvent.NewMessage(message = chatMessage)
        val expectedNewMessage = """{"type":"new_message","message":{"id":"018f3a2b-8a7e-7000-8000-000000000010","roomId":"018f3a2b-8a7e-7000-8000-000000000020","senderId":"018f3a2b-8a7e-7000-8000-000000000001","content":"Broadcast message","createdAt":"2026-11-03T20:00:00Z"}}"""
        assertEquals(expectedNewMessage, json.encodeToString<WsServerEvent>(eventNewMessage))
        assertEquals(eventNewMessage, json.decodeFromString<WsServerEvent>(expectedNewMessage))

        // 2. MessageDeleted
        val eventDeleted: WsServerEvent = WsServerEvent.MessageDeleted(
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            messageId = "018f3a2b-8a7e-7000-8000-000000000010",
        )
        val expectedDeleted = """{"type":"message_deleted","roomId":"018f3a2b-8a7e-7000-8000-000000000020","messageId":"018f3a2b-8a7e-7000-8000-000000000010"}"""
        assertEquals(expectedDeleted, json.encodeToString<WsServerEvent>(eventDeleted))
        assertEquals(eventDeleted, json.decodeFromString<WsServerEvent>(expectedDeleted))

        // 3. ParticipantJoined
        val eventJoined: WsServerEvent = WsServerEvent.ParticipantJoined(
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            newUsers = listOf(user),
            addedBy = null,
        )
        val expectedJoined = """{"type":"participant_joined","roomId":"018f3a2b-8a7e-7000-8000-000000000020","newUsers":[{"userId":"018f3a2b-8a7e-7000-8000-000000000002","email":"bob@example.com","displayName":"Bob"}]}"""
        assertEquals(expectedJoined, json.encodeToString<WsServerEvent>(eventJoined))
        assertEquals(eventJoined, json.decodeFromString<WsServerEvent>(expectedJoined))

        // 4. ParticipantLeft
        val eventLeft: WsServerEvent = WsServerEvent.ParticipantLeft(
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            leftUser = user,
            kickedBy = null,
        )
        val expectedLeft = """{"type":"participant_left","roomId":"018f3a2b-8a7e-7000-8000-000000000020","leftUser":{"userId":"018f3a2b-8a7e-7000-8000-000000000002","email":"bob@example.com","displayName":"Bob"}}"""
        assertEquals(expectedLeft, json.encodeToString<WsServerEvent>(eventLeft))
        assertEquals(eventLeft, json.decodeFromString<WsServerEvent>(expectedLeft))

        // 5. Error
        val eventError: WsServerEvent = WsServerEvent.Error(
            error = ChatServerError.ChatRoomNotFound,
            message = "Room not found",
            roomId = "018f3a2b-8a7e-7000-8000-000000000020",
            messageId = "018f3a2b-8a7e-7000-8000-000000000010",
        )
        val expectedError = """{"type":"error","error":"ChatServerError.ChatRoomNotFound","message":"Room not found","roomId":"018f3a2b-8a7e-7000-8000-000000000020","messageId":"018f3a2b-8a7e-7000-8000-000000000010"}"""
        assertEquals(expectedError, json.encodeToString<WsServerEvent>(eventError))
        assertEquals(eventError, json.decodeFromString<WsServerEvent>(expectedError))
    }
}
