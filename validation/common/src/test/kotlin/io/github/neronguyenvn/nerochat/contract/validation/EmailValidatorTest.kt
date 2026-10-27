package io.github.neronguyenvn.nerochat.contract.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmailValidatorTest {

    @Test
    fun testValidEmails() {
        assertTrue(EmailValidator.isValidEmail("user@example.com"))
        assertTrue(EmailValidator.isValidEmail("user.name+tag@domain.co.uk"))
        assertTrue(EmailValidator.isValidEmail("a.b.c@example.org"))
        assertTrue(EmailValidator.isValidEmail("user@sub.domain.com"))
        assertTrue(EmailValidator.isValidEmail("user@xn--example.com"))
    }

    @Test
    fun testInvalidEmails() {
        assertFalse(EmailValidator.isValidEmail(null))
        assertFalse(EmailValidator.isValidEmail(""))
        assertFalse(EmailValidator.isValidEmail("   "))
        assertFalse(EmailValidator.isValidEmail("user"))
        assertFalse(EmailValidator.isValidEmail("user@"))
        assertFalse(EmailValidator.isValidEmail("@example.com"))
        assertFalse(EmailValidator.isValidEmail("user@.com"))
        assertFalse(EmailValidator.isValidEmail("user@com"))
        assertFalse(EmailValidator.isValidEmail("user..name@example.com"))
        assertFalse(EmailValidator.isValidEmail(".user@example.com"))
        assertFalse(EmailValidator.isValidEmail("user.@example.com"))
        assertFalse(EmailValidator.isValidEmail("user@-example.com"))
    }
}
