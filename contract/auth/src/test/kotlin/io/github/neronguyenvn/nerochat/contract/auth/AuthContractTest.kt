package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthServerError
import io.github.neronguyenvn.nerochat.contract.error.ApiErrorResponse
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
    fun `AuthServerError serializes polymorphically and exhaustive endpoint sub-interfaces match correctly`() {
        val errorResponse = ApiErrorResponse<AuthServerError>(
            error = AuthServerError.InvalidCredentials,
            message = "Invalid email or password",
        )
        val serialized = json.encodeToString(errorResponse)
        val deserialized = json.decodeFromString<ApiErrorResponse<AuthServerError>>(serialized)
        assertEquals(AuthServerError.InvalidCredentials, deserialized.error)

        val loginResponse = ApiErrorResponse<AuthServerError.Login>(
            error = AuthServerError.InvalidCredentials,
            message = "Invalid credentials",
        )
        val serializedLogin = json.encodeToString(loginResponse)
        val deserializedLogin = json.decodeFromString<ApiErrorResponse<AuthServerError.Login>>(serializedLogin)
        assertEquals(AuthServerError.InvalidCredentials, deserializedLogin.error)

        val loginError: AuthServerError.Login = deserializedLogin.error
        val message = when (loginError) {
            AuthServerError.InvalidCredentials -> "bad creds"
            AuthServerError.EmailNotVerified -> "unverified"
        }
        assertEquals("bad creds", message)

        val changePasswordError: AuthServerError.ChangePassword = AuthServerError.WrongPassword
        val changePasswordMessage = when (changePasswordError) {
            AuthServerError.WrongPassword -> "wrong password"
            AuthServerError.SamePassword -> "same password"
            AuthServerError.UserNotFound -> "user not found"
        }
        assertEquals("wrong password", changePasswordMessage)
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

        val shortPasswordReq = LoginRequest("user@example.com", "short")
        assertTrue(shortPasswordReq.validate().isEmpty())

        val invalid = LoginRequest("bad-email", "short")
        assertEquals(1, invalid.validate().size)
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

    @Test
    fun `sensitive auth DTOs redact passwords and tokens in toString`() {
        val authUser = AuthenticatedUserDto(
            user = UserDto("u1", "u1@example.com", "User 1", true),
            accessToken = "secret-access-token",
            refreshToken = "secret-refresh-token",
        )
        assertEquals(
            "AuthenticatedUserDto(user=UserDto(id=u1, email=u1@example.com, displayName=User 1, isEmailVerified=true), accessToken=[REDACTED], refreshToken=[REDACTED])",
            authUser.toString()
        )

        val reg = RegisterRequest("reg@example.com", "Reg User", "secret-password")
        assertEquals(
            "RegisterRequest(email=reg@example.com, displayName=Reg User, password=[REDACTED])",
            reg.toString()
        )

        val login = LoginRequest("login@example.com", "secret-password")
        assertEquals(
            "LoginRequest(email=login@example.com, password=[REDACTED])",
            login.toString()
        )

        val reset = ResetPasswordRequest("secret-token", "secret-new-password")
        assertEquals(
            "ResetPasswordRequest(token=[REDACTED], newPassword=[REDACTED])",
            reset.toString()
        )

        val change = ChangePasswordRequest("secret-old", "secret-new")
        assertEquals(
            "ChangePasswordRequest(oldPassword=[REDACTED], newPassword=[REDACTED])",
            change.toString()
        )

        val refresh = RefreshTokenRequest("secret-refresh-token")
        assertEquals(
            "RefreshTokenRequest(refreshToken=[REDACTED])",
            refresh.toString()
        )
    }
}
