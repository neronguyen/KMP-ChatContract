package io.github.neronguyenvn.nerochat.contract.chat

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class ChatRoomDto(
    val id: String,
    val type: ChatRoomType,
    val name: String? = null,
    val creator: ChatParticipantDto,
    val participants: List<ChatParticipantDto>,
    val lastMessage: ChatMessageDto? = null,
    val lastActivityAt: Instant,
)
