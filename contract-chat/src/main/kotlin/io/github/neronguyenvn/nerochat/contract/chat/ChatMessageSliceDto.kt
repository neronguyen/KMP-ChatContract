package io.github.neronguyenvn.nerochat.contract.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageSliceDto(
    val messages: List<ChatMessageDto>,
    val hasMore: Boolean,
)
