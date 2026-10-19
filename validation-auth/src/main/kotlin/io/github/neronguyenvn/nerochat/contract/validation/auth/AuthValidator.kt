package io.github.neronguyenvn.nerochat.contract.validation.auth

import io.github.neronguyenvn.nerochat.contract.validation.ValidationPatterns

/**
 * Pure Kotlin validator for authentication inputs, forms, and request payloads.
 */
object AuthValidator {

    fun validateEmail(email: String?): AuthValidationError.Email? {
        if (email.isNullOrBlank()) return AuthValidationError.EmptyEmail
        if (!ValidationPatterns.isValidEmail(email)) return AuthValidationError.InvalidEmail
        return null
    }

    fun validateDisplayName(displayName: String?): AuthValidationError.EmptyDisplayName? {
        if (displayName.isNullOrBlank()) return AuthValidationError.EmptyDisplayName
        return null
    }

    fun validatePassword(password: String?): AuthValidationError.Password? {
        if (password.isNullOrBlank()) return AuthValidationError.EmptyPassword
        if (!ValidationPatterns.isValidPassword(password)) return AuthValidationError.InvalidPassword
        return null
    }

    fun validateRegister(
        email: String?,
        displayName: String?,
        password: String?,
    ): List<AuthValidationError.Register> {
        val errors = mutableListOf<AuthValidationError.Register>()
        if (email.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyEmail)
        } else if (!ValidationPatterns.isValidEmail(email)) {
            errors.add(AuthValidationError.InvalidEmail)
        }

        if (displayName.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyDisplayName)
        }

        if (password.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyPassword)
        } else if (!ValidationPatterns.isValidPassword(password)) {
            errors.add(AuthValidationError.InvalidPassword)
        }
        return errors
    }

    fun validateLogin(
        email: String?,
        password: String?,
    ): List<AuthValidationError.Login> {
        val errors = mutableListOf<AuthValidationError.Login>()
        if (email.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyEmail)
        } else if (!ValidationPatterns.isValidEmail(email)) {
            errors.add(AuthValidationError.InvalidEmail)
        }

        if (password.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyPassword)
        } else if (!ValidationPatterns.isValidPassword(password)) {
            errors.add(AuthValidationError.InvalidPassword)
        }
        return errors
    }

    fun validateEmailRequest(email: String?): List<AuthValidationError.Email> =
        listOfNotNull(validateEmail(email))

    fun validateRefreshToken(refreshToken: String?): List<AuthValidationError.RefreshToken> {
        if (refreshToken.isNullOrBlank()) return listOf(AuthValidationError.EmptyRefreshToken)
        return emptyList()
    }

    fun validateResetPassword(
        token: String?,
        newPassword: String?,
    ): List<AuthValidationError.ResetPassword> {
        val errors = mutableListOf<AuthValidationError.ResetPassword>()
        if (token.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyResetToken)
        }
        if (newPassword.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyNewPassword)
        } else if (!ValidationPatterns.isValidPassword(newPassword)) {
            errors.add(AuthValidationError.InvalidNewPassword)
        }
        return errors
    }

    fun validateChangePassword(
        oldPassword: String?,
        newPassword: String?,
    ): List<AuthValidationError.ChangePassword> {
        val errors = mutableListOf<AuthValidationError.ChangePassword>()
        if (oldPassword.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyOldPassword)
        }
        if (newPassword.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyNewPassword)
        } else if (!ValidationPatterns.isValidPassword(newPassword)) {
            errors.add(AuthValidationError.InvalidNewPassword)
        }
        return errors
    }
}
