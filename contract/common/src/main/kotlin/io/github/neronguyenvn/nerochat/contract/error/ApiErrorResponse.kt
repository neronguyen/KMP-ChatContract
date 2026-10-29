package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val error: ErrorCode,
    val message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    val retryAfterSeconds: Long? = null,
) {
    constructor(
        error: ServerError,
        message: String,
        fieldErrors: Map<String, String> = emptyMap(),
        retryAfterSeconds: Long? = null,
    ) : this(
        error = error.code,
        message = message,
        fieldErrors = fieldErrors,
        retryAfterSeconds = retryAfterSeconds,
    )
}
