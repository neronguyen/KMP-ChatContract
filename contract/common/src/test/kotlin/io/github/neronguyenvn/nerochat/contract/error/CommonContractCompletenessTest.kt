package io.github.neronguyenvn.nerochat.contract.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CommonContractCompletenessTest {

    @Test
    fun `CommonServerError completeness and object verification`() {
        val subclasses = CommonServerError::class.sealedSubclasses
        for (subclass in subclasses) {
            assertNotNull(
                subclass.objectInstance,
                "Subtype ${subclass.qualifiedName} must be a payload-free object"
            )
        }

        val entries = CommonServerError.entries.toSet()
        val declaredSubclasses = subclasses.mapNotNull { it.objectInstance }.toSet()
        assertEquals(declaredSubclasses, entries, "CommonServerError entries must match its sealed subclasses")
    }

    @Test
    fun `CommonServerError golden snapshot check`() {
        val stream = javaClass.getResourceAsStream("/common-error-codes-golden.txt")
        assertNotNull(stream, "Golden snapshot file /common-error-codes-golden.txt must exist")
        val goldenCodes = stream.bufferedReader().readLines().filter { it.isNotBlank() }

        val actualCodes = CommonServerError.entries.map { it.code.value }.sorted()
        assertEquals(goldenCodes, actualCodes, "Common error codes must match golden snapshot")
    }
}
