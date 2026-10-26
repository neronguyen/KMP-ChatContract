package io.github.neronguyenvn.nerochat.contract.validation.auth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordValidatorTest {

    @Test
    fun testValidPasswords() {
        assertTrue(PasswordValidator.isValidPassword("StrongPass1"))
        assertTrue(PasswordValidator.isValidPassword("Password123!"))
        assertTrue(PasswordValidator.isValidPassword("Pass1234567890"))
        // Supplementary Unicode characters / emojis
        assertTrue(PasswordValidator.isValidPassword("Pass12345🚀🎉"))
    }

    @Test
    fun testInvalidPasswords() {
        assertFalse(PasswordValidator.isValidPassword(null))
        assertFalse(PasswordValidator.isValidPassword(""))
        assertFalse(PasswordValidator.isValidPassword("   "))
        assertFalse(PasswordValidator.isValidPassword("Short1"))
        assertFalse(PasswordValidator.isValidPassword("NoDigitsHere!"))
        assertFalse(PasswordValidator.isValidPassword("12345678901"))
    }
}
