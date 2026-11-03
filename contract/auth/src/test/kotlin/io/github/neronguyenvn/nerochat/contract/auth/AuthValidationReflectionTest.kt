package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.ValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthValidationReflectionTest {

    @Test
    fun `all AuthValidationError leaves are payload-free singleton objects`() {
        val leaves = mutableListOf<KClass<*>>()

        fun collectLeaves(kClass: KClass<*>) {
            val subclasses = kClass.sealedSubclasses
            if (subclasses.isEmpty()) {
                leaves.add(kClass)
            } else {
                for (sub in subclasses) {
                    collectLeaves(sub)
                }
            }
        }

        collectLeaves(AuthValidationError::class)

        assertTrue(leaves.isNotEmpty(), "AuthValidationError must have concrete error leaves")
        for (leaf in leaves) {
            assertNotNull(
                leaf.objectInstance,
                "Validation error leaf ${leaf.qualifiedName} must be a payload-free object"
            )
            val instance = leaf.objectInstance as? ValidationError
            assertNotNull(instance, "${leaf.qualifiedName} must implement ValidationError")
            assertTrue(instance.field.isNotBlank(), "Field must not be blank for ${leaf.simpleName}")
            assertTrue(instance.message.isNotBlank(), "Message must not be blank for ${leaf.simpleName}")
        }
    }

    @Test
    fun `validation error fields strictly correspond to DTO member property names`() {
        val mappings: List<Pair<KClass<*>, KClass<out Validatable<*>>>> = listOf(
            AuthValidationError.Register::class to RegisterRequest::class,
            AuthValidationError.Login::class to LoginRequest::class,
            AuthValidationError.Email::class to EmailRequest::class,
            AuthValidationError.RefreshToken::class to RefreshTokenRequest::class,
            AuthValidationError.ResetPassword::class to ResetPasswordRequest::class,
            AuthValidationError.ChangePassword::class to ChangePasswordRequest::class,
        )

        for ((errorScope, dtoClass) in mappings) {
            val dtoProperties = dtoClass.memberProperties.map { it.name }.toSet()

            // Find all concrete error objects implementing this error scope
            val associatedErrors = AuthValidationError::class.nestedClasses
                .mapNotNull { it.objectInstance as? ValidationError }
                .filter { errorScope.isInstance(it) }

            assertTrue(
                associatedErrors.isNotEmpty(),
                "Error scope ${errorScope.simpleName} must have associated validation error instances"
            )

            for (err in associatedErrors) {
                assertTrue(
                    dtoProperties.contains(err.field),
                    "Error field '${err.field}' on ${err::class.simpleName} does not match any property on ${dtoClass.simpleName} ($dtoProperties)"
                )
            }
        }
    }
}
