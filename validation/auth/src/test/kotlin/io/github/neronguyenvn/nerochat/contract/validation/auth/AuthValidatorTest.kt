package io.github.neronguyenvn.nerochat.contract.validation.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthValidatorTest {

    @Test
    fun testValidateDisplayName() {
        assertNull(AuthValidator.validateDisplayName("User"))
        assertNull(AuthValidator.validateDisplayName("John Doe"))

        assertEquals(AuthValidationError.EmptyDisplayName, AuthValidator.validateDisplayName(null))
        assertEquals(AuthValidationError.EmptyDisplayName, AuthValidator.validateDisplayName(""))
        assertEquals(AuthValidationError.EmptyDisplayName, AuthValidator.validateDisplayName("   "))
        assertEquals(AuthValidationError.EmptyDisplayName, AuthValidator.validateDisplayName(" User"))
        assertEquals(AuthValidationError.EmptyDisplayName, AuthValidator.validateDisplayName("User "))
        assertEquals(AuthValidationError.EmptyDisplayName, AuthValidator.validateDisplayName("  John Doe  "))
    }

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
    fun testValidateRegisterUntrimmedDisplayName() {
        val errors = AuthValidator.validateRegister(
            email = "user@example.com",
            displayName = " User ",
            password = "password123",
        )
        assertEquals(1, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyDisplayName))
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

        val validShortPassword = AuthValidator.validateLogin("user@example.com", "pass")
        assertTrue(validShortPassword.isEmpty())

        val invalidEmail = AuthValidator.validateLogin("invalid-email", "pass")
        assertEquals(1, invalidEmail.size)
        assertTrue(invalidEmail.contains(AuthValidationError.InvalidEmail))

        val emptyFields = AuthValidator.validateLogin("", "")
        assertEquals(2, emptyFields.size)
        assertTrue(emptyFields.contains(AuthValidationError.EmptyEmail))
        assertTrue(emptyFields.contains(AuthValidationError.EmptyPassword))
    }

    @Test
    fun testValidateRefreshToken() {
        assertTrue(AuthValidator.validateRefreshToken("valid-token").isEmpty())
        val errors = AuthValidator.validateRefreshToken("")
        assertEquals(1, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyRefreshToken))
    }

    @Test
    fun testValidateOtpCode() {
        assertNull(AuthValidator.validateOtpCode("123456"))
        assertNull(AuthValidator.validateOtpCode("000000"))
        assertNull(AuthValidator.validateOtpCode("999999"))

        assertEquals(AuthValidationError.EmptyOtpCode, AuthValidator.validateOtpCode(null))
        assertEquals(AuthValidationError.EmptyOtpCode, AuthValidator.validateOtpCode(""))
        assertEquals(AuthValidationError.EmptyOtpCode, AuthValidator.validateOtpCode("   "))

        assertEquals(AuthValidationError.InvalidOtpLength, AuthValidator.validateOtpCode("12345"))
        assertEquals(AuthValidationError.InvalidOtpLength, AuthValidator.validateOtpCode("1234567"))

        assertEquals(AuthValidationError.NonNumericOtp, AuthValidator.validateOtpCode("12a456"))
        assertEquals(AuthValidationError.NonNumericOtp, AuthValidator.validateOtpCode("abcdef"))
    }

    @Test
    fun testValidateVerifyEmail() {
        assertTrue(AuthValidator.validateVerifyEmail("alice@example.com", "123456").isEmpty())

        val errors = AuthValidator.validateVerifyEmail("", "12")
        assertEquals(2, errors.size)
        assertTrue(errors.contains(AuthValidationError.EmptyEmail))
        assertTrue(errors.contains(AuthValidationError.InvalidOtpLength))
    }

    @Test
    fun testValidateResetPasswordWithOtp() {
        assertTrue(AuthValidator.validateResetPassword("alice@example.com", "123456", "newPassword1").isEmpty())

        val errors = AuthValidator.validateResetPassword("invalid-email", "abc", "weak")
        assertEquals(3, errors.size)
        assertTrue(errors.contains(AuthValidationError.InvalidEmail))
        assertTrue(errors.contains(AuthValidationError.InvalidOtpLength))
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
