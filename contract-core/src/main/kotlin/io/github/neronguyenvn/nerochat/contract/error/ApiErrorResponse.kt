package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val code: String,
    val message: String,
    val errors: List<String> = emptyList(),
    val fieldErrors: Map<String, String> = emptyMap(),
    val retryAfterSeconds: Long? = null,
)
