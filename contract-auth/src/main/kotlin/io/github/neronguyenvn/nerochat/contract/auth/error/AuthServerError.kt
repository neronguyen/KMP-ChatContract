package io.github.neronguyenvn.nerochat.contract.auth.error

import io.github.neronguyenvn.nerochat.contract.error.ServerError

sealed interface AuthServerError : ServerError {
    override val code: String

    // Per-endpoint error contracts
    sealed interface Register : AuthServerError
    sealed interface ResendVerification : AuthServerError
    sealed interface Login : AuthServerError
    sealed interface RefreshToken : AuthServerError
    sealed interface Logout : AuthServerError
    sealed interface VerifyEmail : AuthServerError
    sealed interface ForgotPassword : AuthServerError
    sealed interface ResetPassword : AuthServerError
    sealed interface ChangePassword : AuthServerError

    data object UserAlreadyExists : Register {
        override val code = "USER_ALREADY_EXISTS"
    }

    data object InvalidCredentials : Login {
        override val code = "INVALID_CREDENTIALS"
    }

    data object EmailNotVerified : Login {
        override val code = "EMAIL_NOT_VERIFIED"
    }

    data object InvalidToken : RefreshToken, Logout, VerifyEmail, ResetPassword {
        override val code = "INVALID_TOKEN"
    }

    data object UserNotFound : RefreshToken, ChangePassword {
        override val code = "USER_NOT_FOUND"
    }

    data object WrongPassword : ChangePassword {
        override val code = "WRONG_PASSWORD"
    }

    data object SamePassword : ResetPassword, ChangePassword {
        override val code = "SAME_PASSWORD"
    }

    data object Unauthorized : ChangePassword {
        override val code = ServerError.UNAUTHORIZED
    }

    data object RateLimitExceeded :
        Register,
        ResendVerification,
        Login,
        RefreshToken,
        ForgotPassword,
        ResetPassword {
        override val code = "RATE_LIMIT_EXCEEDED"
    }

    data object ValidationFailed :
        Register,
        ResendVerification,
        Login,
        RefreshToken,
        Logout,
        ForgotPassword,
        ResetPassword,
        ChangePassword {
        override val code = ServerError.VALIDATION_FAILED
    }

    companion object {
        val entries: List<AuthServerError> = listOf(
            UserAlreadyExists,
            InvalidCredentials,
            EmailNotVerified,
            InvalidToken,
            UserNotFound,
            WrongPassword,
            SamePassword,
            Unauthorized,
            RateLimitExceeded,
            ValidationFailed,
        )

        /**
         * Returns the error whose code exactly matches [code], including case, or `null` if unknown.
         */
        fun fromCode(code: String): AuthServerError? = entries.find { it.code == code }
    }
}
