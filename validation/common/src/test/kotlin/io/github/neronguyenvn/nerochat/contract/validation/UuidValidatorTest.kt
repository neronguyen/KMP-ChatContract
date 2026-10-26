package io.github.neronguyenvn.nerochat.contract.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UuidValidatorTest {

    @Test
    fun testValidUuids() {
        assertTrue(UuidValidator.isValidUuid("123e4567-e89b-12d3-a456-426614174000"))
        assertTrue(UuidValidator.isValidUuid("123E4567-E89B-12D3-A456-426614174000"))
    }

    @Test
    fun testInvalidUuids() {
        assertFalse(UuidValidator.isValidUuid(null))
        assertFalse(UuidValidator.isValidUuid(""))
        assertFalse(UuidValidator.isValidUuid("   "))
        assertFalse(UuidValidator.isValidUuid("123e4567-e89b-12d3-a456-42661417400"))
        assertFalse(UuidValidator.isValidUuid("123e4567-e89b-12d3-a456-4266141740000"))
        assertFalse(UuidValidator.isValidUuid("123e4567e89b12d3a456426614174000"))
        assertFalse(UuidValidator.isValidUuid("not-a-uuid-at-all"))
    }
}
