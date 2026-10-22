package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val displayName: String,
    val password: String,
) : Validatable<AuthValidationError.Register> {
    /**
     * Returns the errors from [AuthValidator.validateRegister], or an empty list if valid.
     */
    override fun validate(): List<AuthValidationError.Register> =
        AuthValidator.validateRegister(email = email, displayName = displayName, password = password)

    override fun toString(): String =
        "RegisterRequest(email=$email, displayName=$displayName, password=[REDACTED])"
}
