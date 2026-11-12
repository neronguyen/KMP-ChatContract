package io.github.neronguyenvn.nerochat.contract.validation.auth

import io.github.neronguyenvn.nerochat.contract.validation.EmailValidator

/**
 * Pure Kotlin validator for authentication inputs, forms, and request payloads.
 */
object AuthValidator {

    /**
     * Returns `EmptyEmail` for null or blank input, `InvalidEmail` if
     * [EmailValidator.isValidEmail] rejects it, or `null` if valid.
     */
    fun validateEmail(email: String?): AuthValidationError.Email? {
        if (email.isNullOrBlank()) return AuthValidationError.EmptyEmail
        if (!EmailValidator.isValidEmail(email)) return AuthValidationError.InvalidEmail
        return null
    }

    /**
     * Returns `EmptyDisplayName` for null, blank, or untrimmed input, or `null` if valid.
     */
    fun validateDisplayName(displayName: String?): AuthValidationError.EmptyDisplayName? {
        if (displayName.isNullOrBlank() || displayName != displayName.trim()) return AuthValidationError.EmptyDisplayName
        return null
    }

    /**
     * Returns `EmptyPassword` for null or blank input, `InvalidPassword` if
     * [PasswordValidator.isValidPassword] rejects it, or `null` if valid.
     */
    fun validatePassword(password: String?): AuthValidationError.Password? {
        if (password.isNullOrBlank()) return AuthValidationError.EmptyPassword
        if (!PasswordValidator.isValidPassword(password)) return AuthValidationError.InvalidPassword
        return null
    }

    /**
     * Checks email format, a nonblank display name, and [PasswordValidator.isValidPassword].
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
        } else if (!EmailValidator.isValidEmail(email)) {
            errors.add(AuthValidationError.InvalidEmail)
        }

        validateDisplayName(displayName)?.let {
            errors.add(it)
        }

        if (password.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyPassword)
        } else if (!PasswordValidator.isValidPassword(password)) {
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
        } else if (!EmailValidator.isValidEmail(email)) {
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
     * Validates that [code] is a 6-digit numeric string.
     * Returns `EmptyOtpCode` if null or blank, `InvalidOtpLength` if length != 6,
     * `NonNumericOtp` if it contains non-digit characters, or `null` if valid.
     */
    fun validateOtpCode(code: String?): AuthValidationError? {
        if (code.isNullOrBlank()) return AuthValidationError.EmptyOtpCode
        if (code.length != 6) return AuthValidationError.InvalidOtpLength
        if (!code.all { it in '0'..'9' }) return AuthValidationError.NonNumericOtp
        return null
    }

    /**
     * Checks email format and 6-digit OTP code for email verification.
     * Returns at most one error per field in email, code order, or an empty list.
     */
    fun validateVerifyEmail(
        email: String?,
        code: String?,
    ): List<AuthValidationError.VerifyEmail> {
        val errors = mutableListOf<AuthValidationError.VerifyEmail>()
        if (email.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyEmail)
        } else if (!EmailValidator.isValidEmail(email)) {
            errors.add(AuthValidationError.InvalidEmail)
        }

        when (val otpError = validateOtpCode(code)) {
            is AuthValidationError.VerifyEmail -> errors.add(otpError)
            else -> {}
        }
        return errors
    }

    /**
     * Checks email format, 6-digit OTP code, and [PasswordValidator.isValidPassword] for password reset.
     * Returns at most one error per field in email, code, new-password order, or an empty list.
     */
    fun validateResetPassword(
        email: String?,
        code: String?,
        newPassword: String?,
    ): List<AuthValidationError.ResetPassword> {
        val errors = mutableListOf<AuthValidationError.ResetPassword>()
        if (email.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyEmail)
        } else if (!EmailValidator.isValidEmail(email)) {
            errors.add(AuthValidationError.InvalidEmail)
        }

        when (val otpError = validateOtpCode(code)) {
            is AuthValidationError.ResetPassword -> errors.add(otpError)
            else -> {}
        }

        if (newPassword.isNullOrBlank()) {
            errors.add(AuthValidationError.EmptyNewPassword)
        } else if (!PasswordValidator.isValidPassword(newPassword)) {
            errors.add(AuthValidationError.InvalidNewPassword)
        }
        return errors
    }

    /**
     * Checks that [oldPassword] is nonblank and [newPassword] satisfies [PasswordValidator.isValidPassword].
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
        } else if (!PasswordValidator.isValidPassword(newPassword)) {
            errors.add(AuthValidationError.InvalidNewPassword)
        }
        return errors
    }
}
