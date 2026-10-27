package io.github.neronguyenvn.nerochat.contract.chat

import kotlinx.serialization.Serializable

@Serializable
enum class ChatRoomType {
    DIRECT,
    GROUP
}
