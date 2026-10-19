package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class EmailRequest(
    val email: String,
) : Validatable<AuthValidationError.Email> {
    override fun validate(): List<AuthValidationError.Email> =
        AuthValidator.validateEmailRequest(email = email)
}
