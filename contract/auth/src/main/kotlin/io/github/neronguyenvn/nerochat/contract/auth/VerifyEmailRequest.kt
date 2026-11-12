package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String,
) : Validatable<AuthValidationError.VerifyEmail> {
    /**
     * Returns the errors from [AuthValidator.validateVerifyEmail], or an empty list if valid.
     */
    override fun validate(): List<AuthValidationError.VerifyEmail> =
        AuthValidator.validateVerifyEmail(email = email, code = code)

    override fun toString(): String =
        "VerifyEmailRequest(email=$email, code=[REDACTED])"
}
