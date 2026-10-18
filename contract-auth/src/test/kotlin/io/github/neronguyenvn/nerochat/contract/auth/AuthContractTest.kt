package io.github.neronguyenvn.nerochat.contract.auth

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `UserDto serializes and deserializes correctly`() {
        val user = UserDto(
            id = "user-123",
            email = "test@example.com",
            displayName = "Nero",
            isEmailVerified = true,
        )
        val serialized = json.encodeToString(user)
        val deserialized = json.decodeFromString<UserDto>(serialized)
        assertEquals(user, deserialized)
    }

    @Test
    fun `AuthenticatedUserDto serializes and deserializes correctly`() {
        val authUser = AuthenticatedUserDto(
            user = UserDto(
                id = "user-123",
                email = "test@example.com",
                displayName = "Nero",
                isEmailVerified = true,
            ),
            accessToken = "access-token-xyz",
            refreshToken = "refresh-token-abc",
        )
        val serialized = json.encodeToString(authUser)
        val deserialized = json.decodeFromString<AuthenticatedUserDto>(serialized)
        assertEquals(authUser, deserialized)
    }
}
