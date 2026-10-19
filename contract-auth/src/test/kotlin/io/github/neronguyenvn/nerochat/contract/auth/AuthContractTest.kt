package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthServerError
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `AuthServerError lookups and exhaustive endpoint sub-interfaces match correctly`() {
        assertEquals(
            AuthServerError.InvalidCredentials,
            AuthServerError.fromCode("INVALID_CREDENTIALS")
        )
        assertEquals(
            AuthServerError.EmailNotVerified,
            AuthServerError.fromCode("EMAIL_NOT_VERIFIED")
        )
        assertEquals(
            AuthServerError.RateLimitExceeded,
            AuthServerError.fromCode("RATE_LIMIT_EXCEEDED")
        )
        assertEquals(
            AuthServerError.ValidationFailed,
            AuthServerError.fromCode("VALIDATION_FAILED")
        )
        assertEquals(AuthServerError.Unauthorized, AuthServerError.fromCode("UNAUTHORIZED"))
        assertEquals(null, AuthServerError.fromCode("UNKNOWN_CODE"))

        val loginError: AuthServerError.Login = AuthServerError.RateLimitExceeded
        val message = when (loginError) {
            AuthServerError.InvalidCredentials -> "bad creds"
            AuthServerError.EmailNotVerified -> "unverified"
            AuthServerError.RateLimitExceeded -> "rate limited"
            AuthServerError.ValidationFailed -> "invalid input"
        }
        assertEquals("rate limited", message)

        val changePasswordError: AuthServerError.ChangePassword = AuthServerError.Unauthorized
        val changePasswordMessage = when (changePasswordError) {
            AuthServerError.Unauthorized -> "unauthorized"
            AuthServerError.WrongPassword -> "wrong password"
            AuthServerError.SamePassword -> "same password"
            AuthServerError.UserNotFound -> "user not found"
            AuthServerError.ValidationFailed -> "validation failed"
        }
        assertEquals("unauthorized", changePasswordMessage)
    }

    @Test
    fun `RegisterRequest serializes and validates correctly`() {
        val req = RegisterRequest("user@example.com", "User", "password123")
        val jsonStr = json.encodeToString(req)
        val deserialized = json.decodeFromString<RegisterRequest>(jsonStr)
        assertEquals(req, deserialized)
        assertTrue(req.validate().isEmpty())

        val invalid = RegisterRequest("", "", "")
        assertEquals(3, invalid.validate().size)
    }

    @Test
    fun `LoginRequest serializes and validates correctly`() {
        val req = LoginRequest("user@example.com", "password123")
        val jsonStr = json.encodeToString(req)
        val deserialized = json.decodeFromString<LoginRequest>(jsonStr)
        assertEquals(req, deserialized)
        assertTrue(req.validate().isEmpty())

        val invalid = LoginRequest("bad-email", "short")
        assertEquals(2, invalid.validate().size)
    }

    @Test
    fun `EmailRequest, RefreshTokenRequest, ResetPasswordRequest, ChangePasswordRequest validate correctly`() {
        assertTrue(EmailRequest("user@example.com").validate().isEmpty())
        assertEquals(1, EmailRequest("").validate().size)

        assertTrue(RefreshTokenRequest("refresh-token").validate().isEmpty())
        assertEquals(1, RefreshTokenRequest("").validate().size)

        assertTrue(ResetPasswordRequest("token-123", "password123").validate().isEmpty())
        assertEquals(2, ResetPasswordRequest("", "pass").validate().size)

        assertTrue(ChangePasswordRequest("oldPass123", "newPass123").validate().isEmpty())
        assertEquals(2, ChangePasswordRequest("", "").validate().size)
    }
}
