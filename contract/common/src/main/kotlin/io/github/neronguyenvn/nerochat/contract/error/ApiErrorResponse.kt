package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse<out E : ServerError>(
    val error: E,
    val message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    val retryAfterSeconds: Long? = null,
)
