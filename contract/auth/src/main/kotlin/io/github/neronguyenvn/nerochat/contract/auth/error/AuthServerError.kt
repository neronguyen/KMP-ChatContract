package io.github.neronguyenvn.nerochat.contract.auth.error

import io.github.neronguyenvn.nerochat.contract.error.ErrorCode
import io.github.neronguyenvn.nerochat.contract.error.ErrorContract
import io.github.neronguyenvn.nerochat.contract.error.ServerError

object AuthServerError {
    sealed interface Register : ServerError {
        companion object : ErrorContract<Register>(listOf(UserAlreadyExists))
    }

    sealed interface Login : ServerError {
        companion object : ErrorContract<Login>(
            listOf(
                InvalidCredentials,
                EmailNotVerified,
            )
        )
    }

    sealed interface RefreshToken : ServerError {
        companion object : ErrorContract<RefreshToken>(
            listOf(
                InvalidToken,
                UserNotFound,
            )
        )
    }

    sealed interface Logout : ServerError {
        companion object : ErrorContract<Logout>(listOf(InvalidToken))
    }

    sealed interface VerifyEmail : ServerError {
        companion object : ErrorContract<VerifyEmail>(listOf(InvalidToken))
    }

    sealed interface ResetPassword : ServerError {
        companion object : ErrorContract<ResetPassword>(
            listOf(
                InvalidToken,
                SamePassword,
            )
        )
    }

    sealed interface ChangePassword : ServerError {
        companion object : ErrorContract<ChangePassword>(
            listOf(
                WrongPassword,
                SamePassword,
                UserNotFound,
            )
        )
    }

    data object UserAlreadyExists : Register {
        override val code = ErrorCode("AuthServerError.UserAlreadyExists")
    }

    data object InvalidCredentials : Login {
        override val code = ErrorCode("AuthServerError.InvalidCredentials")
    }

    data object EmailNotVerified : Login {
        override val code = ErrorCode("AuthServerError.EmailNotVerified")
    }

    data object InvalidToken : RefreshToken, Logout, VerifyEmail, ResetPassword {
        override val code = ErrorCode("AuthServerError.InvalidToken")
    }

    data object UserNotFound : RefreshToken, ChangePassword {
        override val code = ErrorCode("AuthServerError.UserNotFound")
    }

    data object WrongPassword : ChangePassword {
        override val code = ErrorCode("AuthServerError.WrongPassword")
    }

    data object SamePassword : ResetPassword, ChangePassword {
        override val code = ErrorCode("AuthServerError.SamePassword")
    }
}
