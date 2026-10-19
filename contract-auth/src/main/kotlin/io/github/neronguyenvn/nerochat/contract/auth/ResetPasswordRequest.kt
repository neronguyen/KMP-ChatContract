package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String,
) : Validatable<AuthValidationError.ResetPassword> {
    override fun validate(): List<AuthValidationError.ResetPassword> =
        AuthValidator.validateResetPassword(token = token, newPassword = newPassword)
}
