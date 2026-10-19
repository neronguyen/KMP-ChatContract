package io.github.neronguyenvn.nerochat.contract.validation.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthValidatorTest {

    @Test
    fun testValidateRegisterSuccess() {
        val errors = AuthValidator.validateRegister(
            email = "user@example.com",
            displayName = "User",
            password = "password123",
        )
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateRegisterErrors() {
        val errors = AuthValidator.validateRegister(
            email = "",
            displayName = "",
            password = "",
        )
        assertEquals(3, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyEmail))
        assertTrue(errors.contains(AuthValidationError.EmptyDisplayName))
        assertTrue(errors.contains(AuthValidationError.EmptyPassword))
    }

    @Test
    fun testValidateRegisterInvalidEmailAndPassword() {
        val errors = AuthValidator.validateRegister(
            email = "not-an-email",
            displayName = "Valid Name",
            password = "short",
        )
        assertEquals(2, errors.size)
        assertTrue(errors.contains(AuthValidationError.InvalidEmail))
        assertTrue(errors.contains(AuthValidationError.InvalidPassword))
    }

    @Test
    fun testValidateLogin() {
        val valid = AuthValidator.validateLogin("user@example.com", "password123")
        assertTrue(valid.isEmpty())

        val invalid = AuthValidator.validateLogin("invalid-email", "pass")
        assertEquals(2, invalid.size)
        assertTrue(invalid.contains(AuthValidationError.InvalidEmail))
        assertTrue(invalid.contains(AuthValidationError.InvalidPassword))
    }

    @Test
    fun testValidateRefreshToken() {
        assertTrue(AuthValidator.validateRefreshToken("valid-token").isEmpty())
        val errors = AuthValidator.validateRefreshToken("")
        assertEquals(1, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyRefreshToken))
    }

    @Test
    fun testValidateResetPassword() {
        assertTrue(AuthValidator.validateResetPassword("token123", "newPassword1").isEmpty())

        val errors = AuthValidator.validateResetPassword("", "weak")
        assertEquals(2, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyResetToken))
        assertTrue(errors.contains(AuthValidationError.InvalidNewPassword))
    }

    @Test
    fun testValidateChangePassword() {
        assertTrue(AuthValidator.validateChangePassword("oldPass123", "newPass123").isEmpty())

        val errors = AuthValidator.validateChangePassword("", "")
        assertEquals(2, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyOldPassword))
        assertTrue(errors.contains(AuthValidationError.EmptyNewPassword))
    }
}
