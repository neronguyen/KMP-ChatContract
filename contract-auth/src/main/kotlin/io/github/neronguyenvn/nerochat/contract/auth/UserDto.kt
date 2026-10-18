package io.github.neronguyenvn.nerochat.contract.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val displayName: String,
    val isEmailVerified: Boolean,
)
