package io.github.neronguyenvn.nerochat.contract.chat

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class ChatMessageDto(
    val id: String,
    val roomId: String,
    val senderId: String,
    val content: String,
    val createdAt: Instant,
)
