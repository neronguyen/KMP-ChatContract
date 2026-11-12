package io.github.neronguyenvn.nerochat.contract.auth

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthGoldenWireTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `RegisterRequest matches golden wire format`() {
        val req = RegisterRequest(
            email = "alice@example.com",
            displayName = "Alice",
            password = "SecurePassword123!",
        )
        val expected = """{"email":"alice@example.com","displayName":"Alice","password":"SecurePassword123!"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<RegisterRequest>(expected))
    }

    @Test
    fun `LoginRequest matches golden wire format`() {
        val req = LoginRequest(
            email = "alice@example.com",
            password = "SecurePassword123!",
        )
        val expected = """{"email":"alice@example.com","password":"SecurePassword123!"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<LoginRequest>(expected))
    }

    @Test
    fun `EmailRequest matches golden wire format`() {
        val req = EmailRequest(email = "alice@example.com")
        val expected = """{"email":"alice@example.com"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<EmailRequest>(expected))
    }

    @Test
    fun `RefreshTokenRequest matches golden wire format`() {
        val req = RefreshTokenRequest(refreshToken = "refresh-token-12345")
        val expected = """{"refreshToken":"refresh-token-12345"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<RefreshTokenRequest>(expected))
    }

    @Test
    fun `VerifyEmailRequest matches golden wire format`() {
        val req = VerifyEmailRequest(
            email = "alice@example.com",
            code = "123456",
        )
        val expected = """{"email":"alice@example.com","code":"123456"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<VerifyEmailRequest>(expected))
    }

    @Test
    fun `ResetPasswordRequest matches golden wire format`() {
        val req = ResetPasswordRequest(
            email = "alice@example.com",
            code = "123456",
            newPassword = "NewPassword123!",
        )
        val expected = """{"email":"alice@example.com","code":"123456","newPassword":"NewPassword123!"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<ResetPasswordRequest>(expected))
    }

    @Test
    fun `ChangePasswordRequest matches golden wire format`() {
        val req = ChangePasswordRequest(
            oldPassword = "OldPassword123!",
            newPassword = "NewPassword123!",
        )
        val expected = """{"oldPassword":"OldPassword123!","newPassword":"NewPassword123!"}"""
        assertEquals(expected, json.encodeToString(req))
        assertEquals(req, json.decodeFromString<ChangePasswordRequest>(expected))
    }

    @Test
    fun `UserDto matches golden wire format`() {
        val dto = UserDto(
            id = "018f3a2b-8a7e-7000-8000-000000000001",
            email = "alice@example.com",
            displayName = "Alice",
            isEmailVerified = true,
        )
        val expected = """{"id":"018f3a2b-8a7e-7000-8000-000000000001","email":"alice@example.com","displayName":"Alice","isEmailVerified":true}"""
        assertEquals(expected, json.encodeToString(dto))
        assertEquals(dto, json.decodeFromString<UserDto>(expected))
    }

    @Test
    fun `AuthenticatedUserDto matches golden wire format`() {
        val user = UserDto(
            id = "018f3a2b-8a7e-7000-8000-000000000001",
            email = "alice@example.com",
            displayName = "Alice",
            isEmailVerified = true,
        )
        val dto = AuthenticatedUserDto(
            user = user,
            accessToken = "access-token-abc",
            refreshToken = "refresh-token-xyz",
        )
        val expected = """{"user":{"id":"018f3a2b-8a7e-7000-8000-000000000001","email":"alice@example.com","displayName":"Alice","isEmailVerified":true},"accessToken":"access-token-abc","refreshToken":"refresh-token-xyz"}"""
        assertEquals(expected, json.encodeToString(dto))
        assertEquals(dto, json.decodeFromString<AuthenticatedUserDto>(expected))
    }
}
