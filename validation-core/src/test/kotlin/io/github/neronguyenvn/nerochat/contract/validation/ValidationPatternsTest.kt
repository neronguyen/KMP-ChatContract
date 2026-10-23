package io.github.neronguyenvn.nerochat.contract.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationPatternsTest {

    @Test
    fun testEmailValidation() {
        assertTrue(ValidationPatterns.isValidEmail("user@example.com"))
        assertTrue(ValidationPatterns.isValidEmail("user.name+tag@sub.domain.org"))
        assertTrue(ValidationPatterns.isValidEmail("john.doe@company.co.uk"))

        assertFalse(ValidationPatterns.isValidEmail(null))
        assertFalse(ValidationPatterns.isValidEmail(""))
        assertFalse(ValidationPatterns.isValidEmail("   "))
        assertFalse(ValidationPatterns.isValidEmail("invalid-email"))
        assertFalse(ValidationPatterns.isValidEmail("user@localhost"))
        assertFalse(ValidationPatterns.isValidEmail("user@.com"))
        assertFalse(ValidationPatterns.isValidEmail("user@domain."))
        assertFalse(ValidationPatterns.isValidEmail("user@domain..com"))
        assertFalse(ValidationPatterns.isValidEmail("user name@example.com"))
        assertFalse(ValidationPatterns.isValidEmail("user..name@example.com"))
        assertFalse(ValidationPatterns.isValidEmail("user@-example.com"))
        assertFalse(ValidationPatterns.isValidEmail("user@example.-com"))
        assertFalse(ValidationPatterns.isValidEmail(".user@example.com"))
        assertFalse(ValidationPatterns.isValidEmail("user.@example.com"))
        assertFalse(ValidationPatterns.isValidEmail("user@münchen.de"))
        assertTrue(ValidationPatterns.isValidEmail("user@xn--mnchen-3ya.de"))
    }

    @Test
    fun testUuidValidation() {
        assertTrue(ValidationPatterns.isValidUuid("123e4567-e89b-12d3-a456-426614174000"))
        assertTrue(ValidationPatterns.isValidUuid("c81d4e2e-bcf2-11e6-869b-7df92533c26e"))

        assertFalse(ValidationPatterns.isValidUuid(null))
        assertFalse(ValidationPatterns.isValidUuid(""))
        assertFalse(ValidationPatterns.isValidUuid("not-a-uuid"))
        assertFalse(ValidationPatterns.isValidUuid("123e4567-e89b-12d3-a456-42661417400")) // too short
        assertFalse(ValidationPatterns.isValidUuid("123e4567-e89b-12d3-a456-4266141740000")) // too long
    }

    @Test
    fun testPasswordValidation() {
        assertTrue(ValidationPatterns.isValidPassword("password123"))
        assertTrue(ValidationPatterns.isValidPassword("VerySecure99"))

        assertFalse(ValidationPatterns.isValidPassword(null))
        assertFalse(ValidationPatterns.isValidPassword(""))
        assertFalse(ValidationPatterns.isValidPassword("pass1")) // < 10 chars
        assertFalse(ValidationPatterns.isValidPassword("abcdefghij")) // no digit
        assertFalse(ValidationPatterns.isValidPassword("1234567890")) // no letter
    }
}
