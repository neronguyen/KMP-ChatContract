package io.github.neronguyenvn.nerochat.contract.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
) {
    override fun toString(): String =
        "AuthenticatedUserDto(user=$user, accessToken=[REDACTED], refreshToken=[REDACTED])"
}
