package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
) : Validatable<AuthValidationError.Login> {
    /**
     * Returns the errors from [AuthValidator.validateLogin], or an empty list if valid.
     */
    override fun validate(): List<AuthValidationError.Login> =
        AuthValidator.validateLogin(email = email, password = password)
}
