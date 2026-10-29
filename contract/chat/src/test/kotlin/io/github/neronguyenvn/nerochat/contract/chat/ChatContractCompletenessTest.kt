package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthServerError
import io.github.neronguyenvn.nerochat.contract.chat.error.ChatServerError
import io.github.neronguyenvn.nerochat.contract.error.CommonServerError
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ChatContractCompletenessTest {

    private val allContracts: List<Pair<KClass<*>, ErrorContract<*>>> = listOf(
        ChatServerError.GetParticipant::class to ChatServerError.GetParticipant,
        ChatServerError.GetRoom::class to ChatServerError.GetRoom,
        ChatServerError.GetMessages::class to ChatServerError.GetMessages,
        ChatServerError.CreateRoom::class to ChatServerError.CreateRoom,
        ChatServerError.AddParticipants::class to ChatServerError.AddParticipants,
        ChatServerError.LeaveRoom::class to ChatServerError.LeaveRoom,
        ChatServerError.DeleteMessage::class to ChatServerError.DeleteMessage,
        ChatServerError.WebSocket::class to ChatServerError.WebSocket,
    )

    @Test
    fun `all chat contracts are complete and implementers are objects`() {
        for ((kClass, contract) in allContracts) {
            val subclasses = kClass.sealedSubclasses
            for (sub in subclasses) {
                assertNotNull(
                    sub.objectInstance,
                    "Subtype ${sub.qualifiedName} of ${kClass.simpleName} must be a payload-free object"
                )
            }
            val expectedEntries = subclasses.mapNotNull { it.objectInstance }.toSet()
            val actualEntries = contract.entries.toSet()
            assertEquals(
                expectedEntries,
                actualEntries,
                "Contract ${kClass.simpleName} entries must match its sealedSubclasses exactly"
            )
        }
    }

    @Test
    fun `ChatServerError namespace contains all declared error objects and matches union of endpoint contracts`() {
        val declaredObjects = ChatServerError::class.nestedClasses
            .mapNotNull { it.objectInstance as? ServerError }
            .toSet()

        val contractUnion = allContracts.flatMap { it.second.entries }.toSet()
        assertEquals(declaredObjects, contractUnion, "All nested ServerError objects must match the union of all endpoint contracts")
    }

    @Test
    fun `no chat errors overlap with common errors`() {
        val commonCodes = CommonServerError.entries.map { it.code }.toSet()
        val chatCodes = allContracts.flatMap { it.second.entries }.map { it.code }.toSet()
        val overlap = commonCodes.intersect(chatCodes)
        assertTrue(overlap.isEmpty(), "Chat errors must not overlap with CommonServerError codes: $overlap")
    }

    @Test
    fun `global error codes across all catalogs are unique`() {
        val authCodes = AuthServerError::class.nestedClasses
            .mapNotNull { (it.objectInstance as? ServerError)?.code?.value }
        val chatCodes = ChatServerError::class.nestedClasses
            .mapNotNull { (it.objectInstance as? ServerError)?.code?.value }
        val commonCodes = CommonServerError.entries.map { it.code.value }

        val allCodes = commonCodes + authCodes + chatCodes
        assertEquals(allCodes.size, allCodes.distinct().size, "All error codes must be globally unique")
        assertEquals(23, allCodes.size, "Expected exactly 23 error codes across the entire system")
    }

    @Test
    fun `all global error codes match golden snapshot`() {
        val stream = javaClass.getResourceAsStream("/error-codes-golden.txt")
        assertNotNull(stream, "Golden snapshot file /error-codes-golden.txt must exist")
        val goldenCodes = stream.bufferedReader().readLines().filter { it.isNotBlank() }

        val authCodes = AuthServerError::class.nestedClasses
            .mapNotNull { (it.objectInstance as? ServerError)?.code?.value }
        val chatCodes = ChatServerError::class.nestedClasses
            .mapNotNull { (it.objectInstance as? ServerError)?.code?.value }
        val commonCodes = CommonServerError.entries.map { it.code.value }

        val actualCodes = (commonCodes + authCodes + chatCodes).sorted()
        assertEquals(goldenCodes, actualCodes, "Global error codes must match golden snapshot")
    }
}
