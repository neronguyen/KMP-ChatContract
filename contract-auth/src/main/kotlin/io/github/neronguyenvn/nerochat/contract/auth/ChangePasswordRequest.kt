package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
) : Validatable<AuthValidationError.ChangePassword> {
    override fun validate(): List<AuthValidationError.ChangePassword> =
        AuthValidator.validateChangePassword(oldPassword = oldPassword, newPassword = newPassword)
}
