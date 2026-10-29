package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthErrors
import io.github.neronguyenvn.nerochat.contract.auth.error.ChangePasswordError
import io.github.neronguyenvn.nerochat.contract.auth.error.LoginError
import io.github.neronguyenvn.nerochat.contract.auth.error.LogoutError
import io.github.neronguyenvn.nerochat.contract.auth.error.RefreshTokenError
import io.github.neronguyenvn.nerochat.contract.auth.error.RegisterError
import io.github.neronguyenvn.nerochat.contract.auth.error.ResetPasswordError
import io.github.neronguyenvn.nerochat.contract.auth.error.VerifyEmailError
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
        RegisterError::class to RegisterError,
        LoginError::class to LoginError,
        RefreshTokenError::class to RefreshTokenError,
        LogoutError::class to LogoutError,
        VerifyEmailError::class to VerifyEmailError,
        ResetPasswordError::class to ResetPasswordError,
        ChangePasswordError::class to ChangePasswordError,
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
    fun `AuthErrors catalog contains all declared objects and matches union of endpoint contracts`() {
        val declaredObjects = AuthErrors::class.nestedClasses
            .mapNotNull { it.objectInstance as? ServerError }
            .toSet()

        val catalogEntries = AuthErrors.all.toSet()
        assertEquals(declaredObjects, catalogEntries, "AuthErrors.all must contain all nested ServerError objects in AuthErrors")

        val contractUnion = allContracts.flatMap { it.second.entries }.toSet()
        assertEquals(catalogEntries, contractUnion, "AuthErrors.all must match the union of all endpoint contract entries")
    }

    @Test
    fun `no auth errors overlap with common errors`() {
        val commonCodes = CommonServerError.entries.map { it.code }.toSet()
        val authCodes = AuthErrors.all.map { it.code }.toSet()
        val overlap = commonCodes.intersect(authCodes)
        assertTrue(overlap.isEmpty(), "Auth errors must not overlap with CommonServerError codes: $overlap")
    }

    @Test
    fun `auth errors match golden snapshot`() {
        val stream = javaClass.getResourceAsStream("/auth-error-codes-golden.txt")
        assertNotNull(stream, "Golden snapshot file /auth-error-codes-golden.txt must exist")
        val goldenCodes = stream.bufferedReader().readLines().filter { it.isNotBlank() }

        val actualCodes = AuthErrors.all.map { it.code.value }.sorted()
        assertEquals(goldenCodes, actualCodes, "Auth error codes must match golden snapshot")
    }
}
