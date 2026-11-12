package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String,
) : Validatable<AuthValidationError.ResetPassword> {
    /**
     * Returns the errors from [AuthValidator.validateResetPassword], or an empty list if valid.
     */
    override fun validate(): List<AuthValidationError.ResetPassword> =
        AuthValidator.validateResetPassword(email = email, code = code, newPassword = newPassword)

    override fun toString(): String =
        "ResetPasswordRequest(email=$email, code=[REDACTED], newPassword=[REDACTED])"
}
