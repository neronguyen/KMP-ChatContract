package io.github.neronguyenvn.nerochat.contract.error

/**
 * Standard framework and cross-cutting server errors.
 */
sealed interface CommonServerError : ServerError {
    data object BadRequest : CommonServerError {
        override val code = ErrorCode("CommonServerError.BadRequest")
    }
    data object ValidationFailed : CommonServerError {
        override val code = ErrorCode("CommonServerError.ValidationFailed")
    }
    data object Unauthorized : CommonServerError {
        override val code = ErrorCode("CommonServerError.Unauthorized")
    }
    data object RateLimitExceeded : CommonServerError {
        override val code = ErrorCode("CommonServerError.RateLimitExceeded")
    }
    data object InternalServerError : CommonServerError {
        override val code = ErrorCode("CommonServerError.InternalServerError")
    }
}

object CommonErrors : ErrorContract<CommonServerError>(listOf(
    CommonServerError.BadRequest,
    CommonServerError.ValidationFailed,
    CommonServerError.Unauthorized,
    CommonServerError.RateLimitExceeded,
    CommonServerError.InternalServerError,
))
