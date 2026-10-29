package io.github.neronguyenvn.nerochat.contract.chat

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthErrors
import io.github.neronguyenvn.nerochat.contract.chat.error.AddParticipantsError
import io.github.neronguyenvn.nerochat.contract.chat.error.ChatErrors
import io.github.neronguyenvn.nerochat.contract.chat.error.CreateRoomError
import io.github.neronguyenvn.nerochat.contract.chat.error.DeleteMessageError
import io.github.neronguyenvn.nerochat.contract.chat.error.GetMessagesError
import io.github.neronguyenvn.nerochat.contract.chat.error.GetParticipantError
import io.github.neronguyenvn.nerochat.contract.chat.error.GetRoomError
import io.github.neronguyenvn.nerochat.contract.chat.error.LeaveRoomError
import io.github.neronguyenvn.nerochat.contract.chat.error.WebSocketError
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
        GetParticipantError::class to GetParticipantError,
        GetRoomError::class to GetRoomError,
        GetMessagesError::class to GetMessagesError,
        CreateRoomError::class to CreateRoomError,
        AddParticipantsError::class to AddParticipantsError,
        LeaveRoomError::class to LeaveRoomError,
        DeleteMessageError::class to DeleteMessageError,
        WebSocketError::class to WebSocketError,
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
    fun `ChatErrors catalog contains all declared objects and matches union of endpoint contracts`() {
        val declaredObjects = ChatErrors::class.nestedClasses
            .mapNotNull { it.objectInstance as? ServerError }
            .toSet()

        val catalogEntries = ChatErrors.all.toSet()
        assertEquals(declaredObjects, catalogEntries, "ChatErrors.all must contain all nested ServerError objects in ChatErrors")

        val contractUnion = allContracts.flatMap { it.second.entries }.toSet()
        assertEquals(catalogEntries, contractUnion, "ChatErrors.all must match the union of all endpoint contract entries")
    }

    @Test
    fun `no chat errors overlap with common errors`() {
        val commonCodes = CommonServerError.entries.map { it.code }.toSet()
        val chatCodes = ChatErrors.all.map { it.code }.toSet()
        val overlap = commonCodes.intersect(chatCodes)
        assertTrue(overlap.isEmpty(), "Chat errors must not overlap with CommonServerError codes: $overlap")
    }

    @Test
    fun `global error codes across all catalogs are unique`() {
        val allCodes = (CommonServerError.entries + AuthErrors.all + ChatErrors.all).map { it.code.value }
        assertEquals(allCodes.size, allCodes.distinct().size, "All error codes must be globally unique")
        assertEquals(23, allCodes.size, "Expected exactly 23 error codes across the entire system")
    }

    @Test
    fun `all global error codes match golden snapshot`() {
        val stream = javaClass.getResourceAsStream("/error-codes-golden.txt")
        assertNotNull(stream, "Golden snapshot file /error-codes-golden.txt must exist")
        val goldenCodes = stream.bufferedReader().readLines().filter { it.isNotBlank() }

        val actualCodes = (CommonServerError.entries + AuthErrors.all + ChatErrors.all)
            .map { it.code.value }
            .sorted()

        assertEquals(goldenCodes, actualCodes, "Global error codes must match golden snapshot")
    }
}
