package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthErrors
import io.github.neronguyenvn.nerochat.contract.auth.error.AuthServerError
import io.github.neronguyenvn.nerochat.contract.auth.error.ChangePasswordError
import io.github.neronguyenvn.nerochat.contract.auth.error.LoginError
import io.github.neronguyenvn.nerochat.contract.auth.error.RegisterError
import io.github.neronguyenvn.nerochat.contract.error.ApiErrorResponse
import io.github.neronguyenvn.nerochat.contract.error.ApiFailure
import io.github.neronguyenvn.nerochat.contract.error.toFailure
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
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
    fun `AuthServerError serializes and exhaustive endpoint contracts match correctly`() {
        val errorResponse = ApiErrorResponse(
            error = AuthErrors.InvalidCredentials,
            message = "Invalid email or password",
        )
        val serialized = json.encodeToString(errorResponse)
        assertTrue(
            serialized.contains("\"error\":\"AuthServerError.InvalidCredentials\""),
            "Expected wire format to contain 'error':'AuthServerError.InvalidCredentials' but was: $serialized"
        )
        val deserialized = json.decodeFromString<ApiErrorResponse>(serialized)
        assertEquals(AuthErrors.InvalidCredentials.code, deserialized.error)
        assertEquals(AuthErrors.InvalidCredentials, AuthServerError.fromCode(deserialized.error))

        val loginResponse = ApiErrorResponse(
            error = AuthErrors.InvalidCredentials,
            message = "Invalid credentials",
        )
        val serializedLogin = json.encodeToString(loginResponse)
        assertTrue(
            serializedLogin.contains("\"error\":\"AuthServerError.InvalidCredentials\""),
            "Expected wire format to contain 'error':'AuthServerError.InvalidCredentials' but was: $serializedLogin"
        )
        val deserializedLogin = json.decodeFromString<ApiErrorResponse>(serializedLogin)
        assertEquals(AuthErrors.InvalidCredentials.code, deserializedLogin.error)

        val loginError: LoginError? = LoginError.fromCode(deserializedLogin.error)
        val message = when (loginError) {
            AuthErrors.InvalidCredentials -> "bad creds"
            AuthErrors.EmailNotVerified -> "unverified"
            null -> "unknown"
        }
        assertEquals("bad creds", message)

        val changePasswordError: ChangePasswordError? = ChangePasswordError.fromCode(AuthErrors.WrongPassword.code)
        val changePasswordMessage = when (changePasswordError) {
            AuthErrors.WrongPassword -> "wrong password"
            AuthErrors.SamePassword -> "same password"
            AuthErrors.UserNotFound -> "user not found"
            null -> "unknown"
        }
        assertEquals("wrong password", changePasswordMessage)

        // Test classification via toFailure
        val failure = deserializedLogin.toFailure(LoginError, 401)
        assertIs<ApiFailure.Business<LoginError>>(failure)
        assertEquals(AuthErrors.InvalidCredentials, failure.error)
        assertEquals("Invalid credentials", failure.details.message)
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
