package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.auth.error.AuthErrors
import io.github.neronguyenvn.nerochat.contract.auth.error.AuthServerError
import io.github.neronguyenvn.nerochat.contract.auth.error.ChangePasswordError
import io.github.neronguyenvn.nerochat.contract.auth.error.LoginError
import io.github.neronguyenvn.nerochat.contract.auth.error.LogoutError
import io.github.neronguyenvn.nerochat.contract.auth.error.RefreshTokenError
import io.github.neronguyenvn.nerochat.contract.auth.error.RegisterError
import io.github.neronguyenvn.nerochat.contract.auth.error.ResetPasswordError
import io.github.neronguyenvn.nerochat.contract.auth.error.VerifyEmailError
import io.github.neronguyenvn.nerochat.contract.error.CommonErrors
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
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
        AuthServerError::class to AuthServerError,
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
    fun `no auth errors overlap with common errors`() {
        val commonCodes = CommonErrors.entries.map { it.code }.toSet()
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
