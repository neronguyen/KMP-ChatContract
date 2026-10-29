package io.github.neronguyenvn.nerochat.contract.auth.error

import io.github.neronguyenvn.nerochat.contract.error.ErrorCode
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError

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

object AuthErrors {
    data object UserAlreadyExists : RegisterError {
        override val code = ErrorCode("AuthServerError.UserAlreadyExists")
    }

    data object InvalidCredentials : LoginError {
        override val code = ErrorCode("AuthServerError.InvalidCredentials")
    }

    data object EmailNotVerified : LoginError {
        override val code = ErrorCode("AuthServerError.EmailNotVerified")
    }

    data object InvalidToken : RefreshTokenError, LogoutError, VerifyEmailError, ResetPasswordError {
        override val code = ErrorCode("AuthServerError.InvalidToken")
    }

    data object UserNotFound : RefreshTokenError, ChangePasswordError {
        override val code = ErrorCode("AuthServerError.UserNotFound")
    }

    data object WrongPassword : ChangePasswordError {
        override val code = ErrorCode("AuthServerError.WrongPassword")
    }

    data object SamePassword : ResetPasswordError, ChangePasswordError {
        override val code = ErrorCode("AuthServerError.SamePassword")
    }

    val all: List<ServerError> = listOf(
        UserAlreadyExists,
        InvalidCredentials,
        EmailNotVerified,
        InvalidToken,
        UserNotFound,
        WrongPassword,
        SamePassword,
    )
}
