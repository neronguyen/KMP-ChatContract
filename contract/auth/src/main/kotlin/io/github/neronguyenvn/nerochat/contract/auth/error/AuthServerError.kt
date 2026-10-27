package io.github.neronguyenvn.nerochat.contract.auth.error

import io.github.neronguyenvn.nerochat.contract.error.ServerError
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthServerError : ServerError {
    // Per-endpoint error contracts
    @Serializable sealed interface Register : AuthServerError
    @Serializable sealed interface ResendVerification : AuthServerError
    @Serializable sealed interface Login : AuthServerError
    @Serializable sealed interface RefreshToken : AuthServerError
    @Serializable sealed interface Logout : AuthServerError
    @Serializable sealed interface VerifyEmail : AuthServerError
    @Serializable sealed interface ForgotPassword : AuthServerError
    @Serializable sealed interface ResetPassword : AuthServerError
    @Serializable sealed interface ChangePassword : AuthServerError

    @Serializable data object UserAlreadyExists : Register
    @Serializable data object InvalidCredentials : Login
    @Serializable data object EmailNotVerified : Login
    @Serializable data object InvalidToken : RefreshToken, Logout, VerifyEmail, ResetPassword
    @Serializable data object UserNotFound : RefreshToken, ChangePassword
    @Serializable data object WrongPassword : ChangePassword
    @Serializable data object SamePassword : ResetPassword, ChangePassword
    @Serializable data object Unauthorized : ChangePassword
    @Serializable
    data object RateLimitExceeded :
        Register,
        ResendVerification,
        Login,
        RefreshToken,
        ForgotPassword,
        ResetPassword
    @Serializable
    data object ValidationFailed :
        Register,
        ResendVerification,
        Login,
        RefreshToken,
        Logout,
        ForgotPassword,
        ResetPassword,
        ChangePassword
}
