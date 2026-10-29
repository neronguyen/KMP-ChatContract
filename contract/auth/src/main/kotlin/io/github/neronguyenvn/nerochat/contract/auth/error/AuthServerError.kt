package io.github.neronguyenvn.nerochat.contract.auth.error

import io.github.neronguyenvn.nerochat.contract.error.ErrorCode
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError

// Base auth error interface for broad matching across any auth error
sealed interface AuthServerError : ServerError {
    companion object : ErrorContract<AuthServerError>(
        listOf(
            AuthErrors.UserAlreadyExists,
            AuthErrors.InvalidCredentials,
            AuthErrors.EmailNotVerified,
            AuthErrors.InvalidToken,
            AuthErrors.UserNotFound,
            AuthErrors.WrongPassword,
            AuthErrors.SamePassword,
        )
    )
}

// Top-level per-endpoint contracts
sealed interface RegisterError : ServerError {
    companion object : ErrorContract<RegisterError>(listOf(AuthErrors.UserAlreadyExists))
}

sealed interface LoginError : ServerError {
    companion object : ErrorContract<LoginError>(
        listOf(
            AuthErrors.InvalidCredentials,
            AuthErrors.EmailNotVerified,
        )
    )
}

sealed interface RefreshTokenError : ServerError {
    companion object : ErrorContract<RefreshTokenError>(
        listOf(
            AuthErrors.InvalidToken,
            AuthErrors.UserNotFound,
        )
    )
}

sealed interface LogoutError : ServerError {
    companion object : ErrorContract<LogoutError>(listOf(AuthErrors.InvalidToken))
}

sealed interface VerifyEmailError : ServerError {
    companion object : ErrorContract<VerifyEmailError>(listOf(AuthErrors.InvalidToken))
}

sealed interface ResetPasswordError : ServerError {
    companion object : ErrorContract<ResetPasswordError>(
        listOf(
            AuthErrors.InvalidToken,
            AuthErrors.SamePassword,
        )
    )
}

sealed interface ChangePasswordError : ServerError {
    companion object : ErrorContract<ChangePasswordError>(
        listOf(
            AuthErrors.WrongPassword,
            AuthErrors.SamePassword,
            AuthErrors.UserNotFound,
        )
    )
}

// Error Catalog
object AuthErrors {
    data object UserAlreadyExists : RegisterError, AuthServerError {
        override val code = ErrorCode("AuthServerError.UserAlreadyExists")
    }

    data object InvalidCredentials : LoginError, AuthServerError {
        override val code = ErrorCode("AuthServerError.InvalidCredentials")
    }

    data object EmailNotVerified : LoginError, AuthServerError {
        override val code = ErrorCode("AuthServerError.EmailNotVerified")
    }

    data object InvalidToken : RefreshTokenError, LogoutError, VerifyEmailError, ResetPasswordError, AuthServerError {
        override val code = ErrorCode("AuthServerError.InvalidToken")
    }

    data object UserNotFound : RefreshTokenError, ChangePasswordError, AuthServerError {
        override val code = ErrorCode("AuthServerError.UserNotFound")
    }

    data object WrongPassword : ChangePasswordError, AuthServerError {
        override val code = ErrorCode("AuthServerError.WrongPassword")
    }

    data object SamePassword : ResetPasswordError, ChangePasswordError, AuthServerError {
        override val code = ErrorCode("AuthServerError.SamePassword")
    }

    val all: List<AuthServerError> = listOf(
        UserAlreadyExists,
        InvalidCredentials,
        EmailNotVerified,
        InvalidToken,
        UserNotFound,
        WrongPassword,
        SamePassword,
    )
}
