package io.github.neronguyenvn.nerochat.contract.auth.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthServerErrorTest {

    @Test
    fun `all declared AuthServerError objects are registered in entries`() {
        val discoveredObjects = findNestedErrorObjects(AuthServerError::class.java)

        // Ensure reflection actually found our objects (guards against refactoring breaking the test silently)
        assertTrue(discoveredObjects.isNotEmpty(), "Expected to find nested AuthServerError objects")
        assertEquals(
            discoveredObjects,
            AuthServerError.entries.toSet(),
            "Mismatch between declared data objects in AuthServerError and AuthServerError.entries"
        )
    }

    @Test
    fun `all AuthServerError entries have unique non-blank codes and resolve via fromCode`() {
        val entries = AuthServerError.entries
        val seenCodes = mutableSetOf<String>()

        for (error in entries) {
            assertTrue(error.code.isNotBlank(), "${error::class.simpleName} must not have a blank code")
            assertTrue(
                seenCodes.add(error.code),
                "Duplicate error code '${error.code}' found in AuthServerError.entries"
            )
            assertEquals(
                error,
                AuthServerError.fromCode(error.code),
                "fromCode('${error.code}') did not return expected instance $error"
            )
        }
    }

    private fun findNestedErrorObjects(root: Class<*>): Set<AuthServerError> {
        val results = mutableSetOf<AuthServerError>()

        fun traverse(clazz: Class<*>) {
            for (nested in clazz.declaredClasses) {
                if (AuthServerError::class.java.isAssignableFrom(nested) && !nested.isInterface) {
                    val instance = runCatching {
                        nested.getDeclaredField("INSTANCE").get(null) as? AuthServerError
                    }.getOrNull()

                    assertNotNull(
                        instance,
                        "Concrete subclass ${nested.simpleName} must be a Kotlin 'object' or 'data object'"
                    )
                    results.add(instance)
                }
                // Recurse in case error objects are ever nested inside endpoint sub-interfaces
                traverse(nested)
            }
        }

        traverse(root)
        return results
    }
}
