package io.github.neronguyenvn.nerochat.contract.auth

import io.github.neronguyenvn.nerochat.contract.validation.Validatable
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidationError
import io.github.neronguyenvn.nerochat.contract.validation.auth.AuthValidator
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
) : Validatable<AuthValidationError.RefreshToken> {
    override fun validate(): List<AuthValidationError.RefreshToken> =
        AuthValidator.validateRefreshToken(refreshToken = refreshToken)
}
