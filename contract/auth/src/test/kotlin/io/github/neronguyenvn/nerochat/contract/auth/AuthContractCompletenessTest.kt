package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthServerError
import io.github.neronguyenvn.nerochat.contract.error.CommonServerError
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthContractCompletenessTest {

    private val allContracts: List<Pair<KClass<*>, ErrorContract<*>>> = listOf(
        AuthServerError.Register::class to AuthServerError.Register,
        AuthServerError.Login::class to AuthServerError.Login,
        AuthServerError.RefreshToken::class to AuthServerError.RefreshToken,
        AuthServerError.Logout::class to AuthServerError.Logout,
        AuthServerError.VerifyEmail::class to AuthServerError.VerifyEmail,
        AuthServerError.ResetPassword::class to AuthServerError.ResetPassword,
        AuthServerError.ChangePassword::class to AuthServerError.ChangePassword,
    )

    @Test
    fun `all auth contracts are complete and implementers are objects`() {
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
    fun `AuthServerError namespace contains all declared error objects and matches union of endpoint contracts`() {
        val declaredObjects = AuthServerError::class.nestedClasses
            .mapNotNull { it.objectInstance as? ServerError }
            .toSet()

        val contractUnion = allContracts.flatMap { it.second.entries }.toSet()
        assertEquals(declaredObjects, contractUnion, "All nested ServerError objects must match the union of all endpoint contracts")
    }

    @Test
    fun `no auth errors overlap with common errors`() {
        val commonCodes = CommonServerError.entries.map { it.code }.toSet()
        val authCodes = allContracts.flatMap { it.second.entries }.map { it.code }.toSet()
        val overlap = commonCodes.intersect(authCodes)
        assertTrue(overlap.isEmpty(), "Auth errors must not overlap with CommonServerError codes: $overlap")
    }

    @Test
    fun `auth errors match golden snapshot`() {
        val stream = javaClass.getResourceAsStream("/auth-error-codes-golden.txt")
        assertNotNull(stream, "Golden snapshot file /auth-error-codes-golden.txt must exist")
        val goldenCodes = stream.bufferedReader().readLines().filter { it.isNotBlank() }

        val actualCodes = allContracts.flatMap { it.second.entries }
            .map { it.code.value }
            .distinct()
            .sorted()
        assertEquals(goldenCodes, actualCodes, "Auth error codes must match golden snapshot")
    }
}
