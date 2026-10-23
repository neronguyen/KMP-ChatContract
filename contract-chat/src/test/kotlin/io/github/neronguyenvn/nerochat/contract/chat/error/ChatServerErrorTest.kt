package io.github.neronguyenvn.nerochat.contract.chat.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ChatServerErrorTest {

    @Test
    fun `all declared ChatServerError objects are registered in entries`() {
        val discoveredObjects = findNestedErrorObjects(ChatServerError::class.java)

        // Ensure reflection actually found our objects (guards against refactoring breaking the test silently)
        assertTrue(discoveredObjects.isNotEmpty(), "Expected to find nested ChatServerError objects")
        assertEquals(
            discoveredObjects,
            ChatServerError.entries.toSet(),
            "Mismatch between declared data objects in ChatServerError and ChatServerError.entries"
        )
    }

    @Test
    fun `all ChatServerError entries have unique non-blank codes and resolve via fromCode`() {
        val entries = ChatServerError.entries
        val seenCodes = mutableSetOf<String>()

        for (error in entries) {
            assertTrue(error.code.isNotBlank(), "${error::class.simpleName} must not have a blank code")
            assertTrue(
                seenCodes.add(error.code),
                "Duplicate error code '${error.code}' found in ChatServerError.entries"
            )
            assertEquals(
                error,
                ChatServerError.fromCode(error.code),
                "fromCode('${error.code}') did not return expected instance $error"
            )
        }
    }

    private fun findNestedErrorObjects(root: Class<*>): Set<ChatServerError> {
        val results = mutableSetOf<ChatServerError>()

        fun traverse(clazz: Class<*>) {
            for (nested in clazz.declaredClasses) {
                if (ChatServerError::class.java.isAssignableFrom(nested) && !nested.isInterface) {
                    val instance = runCatching {
                        nested.getDeclaredField("INSTANCE").get(null) as? ChatServerError
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
