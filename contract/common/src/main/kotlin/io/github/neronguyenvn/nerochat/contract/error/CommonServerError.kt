package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.Serializable

/**
 * Standard framework and cross-cutting server errors.
 */
@Serializable
sealed interface CommonServerError : ServerError {
    @Serializable data object BadRequest : CommonServerError
    @Serializable data object ValidationFailed : CommonServerError
    @Serializable data object Unauthorized : CommonServerError
    @Serializable data object RateLimitExceeded : CommonServerError
    @Serializable data object InternalServerError : CommonServerError
}
