package io.github.neronguyenvn.nerochat.contract.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatParticipantDto(
    val userId: String,
    val email: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
)
