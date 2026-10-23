package io.github.neronguyenvn.nerochat.contract.validation.auth

import io.github.neronguyenvn.nerochat.contract.validation.ValidationPatterns

/**
 * Pure Kotlin validator for authentication inputs, forms, and request payloads.
 */
object AuthValidator {

    /**
     * Returns `EmptyEmail` for null or blank input, `InvalidEmail` if
     * [ValidationPatterns.isValidEmail] rejects it, or `null` if valid.
     */
    fun validateEmail(email: String?): AuthValidationError.Email? {
        if (email.isNullOrBlank()) return AuthValidationError.EmptyEmail
        if (!ValidationPatterns.isValidEmail(email)) return AuthValidationError.InvalidEmail
        return null
    }

    /**
     * Returns `EmptyDisplayName` for null or blank input, or `null` otherwise.
     */
    fun validateDisplayName(displayName: String?): AuthValidationError.EmptyDisplayName? {
        if (displayName.isNullOrBlank()) return AuthValidationError.EmptyDisplayName
        return null
    }

    /**
     * Returns `EmptyPassword` for null or blank input, `InvalidPassword` if
     * [ValidationPatterns.isValidPassword] rejects it, or `null` if valid.
     */
    fun validatePassword(password: String?): AuthValidationError.Password? {
        if (password.isNullOrBlank()) return AuthValidationError.EmptyPassword
        if (!ValidationPatterns.isValidPassword(password)) return AuthValidationError.InvalidPassword
        return null
    }

    /**
     * Checks email format, a nonblank display name, and [ValidationPatterns.isValidPassword].
     * Returns at most one error per field in email, display name, password order, or an empty list.
     * Null and blank inputs produce the corresponding empty-field errors.
     */
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

    /**
     * Checks email format and non-blank password for login inputs.
     * Returns at most one error per field in email, password order, or an empty list.
     * Null and blank inputs produce the corresponding empty-field errors.
     */
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
        }
        return errors
    }

    /**
     * Returns the error from [validateEmail] as a single-element list, or an empty list if valid.
     */
    fun validateEmailRequest(email: String?): List<AuthValidationError.Email> =
        listOfNotNull(validateEmail(email))

    /**
     * Returns a list containing `EmptyRefreshToken` for a null or blank token, or an empty list otherwise.
     */
    fun validateRefreshToken(refreshToken: String?): List<AuthValidationError.RefreshToken> {
        if (refreshToken.isNullOrBlank()) return listOf(AuthValidationError.EmptyRefreshToken)
        return emptyList()
    }

    /**
     * Checks that [token] is nonblank and [newPassword] satisfies [ValidationPatterns.isValidPassword].
     * Returns at most one error per field in token, password order, or an empty list.
     * Null and blank inputs produce the corresponding empty-field errors.
     */
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

    /**
     * Checks that [oldPassword] is nonblank and [newPassword] satisfies [ValidationPatterns.isValidPassword].
     * Returns at most one error per field in old-password, new-password order, or an empty list.
     * Null and blank inputs produce the corresponding empty-field errors.
     */
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
