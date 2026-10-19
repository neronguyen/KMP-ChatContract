package io.github.neronguyenvn.nerochat.contract.validation.auth

import io.github.neronguyenvn.nerochat.contract.validation.ValidationError

/**
 * Pure Kotlin validation errors for authentication forms and requests.
 *
 * Each flow/endpoint has a dedicated sub-interface so client UI states and request
 * validators only need to handle the validation errors relevant to that flow.
 */
sealed interface AuthValidationError : ValidationError {

    /** Validation errors for email-only inputs (e.g. forgot-password, resend-verification). */
    sealed interface Email : AuthValidationError

    /** Validation errors for password inputs. */
    sealed interface Password : AuthValidationError

    /** Validation errors for user registration. */
    sealed interface Register : AuthValidationError

    /** Validation errors for user login. */
    sealed interface Login : AuthValidationError

    /** Validation errors for token refresh. */
    sealed interface RefreshToken : AuthValidationError

    /** Validation errors for password reset. */
    sealed interface ResetPassword : AuthValidationError

    /** Validation errors for password change. */
    sealed interface ChangePassword : AuthValidationError

    data object EmptyEmail : Email, Register, Login {
        override val field: String = "email"
        override val message: String = "Email must not be blank"
    }

    data object InvalidEmail : Email, Register, Login {
        override val field: String = "email"
        override val message: String = "Invalid email format"
    }

    data object EmptyDisplayName : Register {
        override val field: String = "displayName"
        override val message: String = "Display name must not be blank"
    }

    data object EmptyPassword : Password, Register, Login {
        override val field: String = "password"
        override val message: String = "Password must not be blank"
    }

    data object InvalidPassword : Password, Register, Login {
        override val field: String = "password"
        override val message: String =
            "Password must be at least 10 characters and contain at least 1 digit and 1 letter"
    }

    data object EmptyRefreshToken : RefreshToken {
        override val field: String = "refreshToken"
        override val message: String = "Refresh token must not be blank"
    }

    data object EmptyResetToken : ResetPassword {
        override val field: String = "token"
        override val message: String = "Token must not be blank"
    }

    data object EmptyOldPassword : ChangePassword {
        override val field: String = "oldPassword"
        override val message: String = "Old password must not be blank"
    }

    data object EmptyNewPassword : ResetPassword, ChangePassword {
        override val field: String = "newPassword"
        override val message: String = "New password must not be blank"
    }

    data object InvalidNewPassword : ResetPassword, ChangePassword {
        override val field: String = "newPassword"
        override val message: String =
            "Password must be at least 10 characters and contain at least 1 digit and 1 letter"
    }
}
