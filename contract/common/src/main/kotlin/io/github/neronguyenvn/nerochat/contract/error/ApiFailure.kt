package io.github.neronguyenvn.nerochat.contract.error

data class ErrorDetails(
    val message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    val retryAfterSeconds: Long? = null,
)

sealed interface ApiFailure<out E> {
    data class Business<E>(val error: E, val details: ErrorDetails) : ApiFailure<E>
    data class Common(val error: CommonServerError, val details: ErrorDetails) : ApiFailure<Nothing>
    data class Unknown(val code: ErrorCode, val status: Int, val details: ErrorDetails) : ApiFailure<Nothing>
    data class Network(val cause: Throwable) : ApiFailure<Nothing>
}

fun <E : ServerError> ApiErrorResponse.toFailure(
    contract: ErrorContract<E>,
    status: Int,
): ApiFailure<E> {
    val details = ErrorDetails(message, fieldErrors, retryAfterSeconds)
    val business = contract.fromCode(error)
    if (business != null) return ApiFailure.Business(business, details)
    val common = CommonErrors.fromCode(error)
    if (common != null) return ApiFailure.Common(common, details)
    return ApiFailure.Unknown(error, status, details)
}
