package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testApiErrorResponseSerialization() {
        val errorResponse = ApiErrorResponse<CommonServerError>(
            error = CommonServerError.BadRequest,
            message = "Invalid parameter",
            fieldErrors = mapOf("id" to "Invalid UUID"),
            retryAfterSeconds = null,
        )

        val encoded = json.encodeToString(errorResponse)
        val decoded = json.decodeFromString<ApiErrorResponse<CommonServerError>>(encoded)

        assertEquals(errorResponse.error, decoded.error)
        assertEquals(errorResponse.message, decoded.message)
        assertEquals(errorResponse.fieldErrors, decoded.fieldErrors)
        assertEquals(CommonServerError.BadRequest, decoded.error)
    }

    @Test
    fun testAllCommonServerErrorRoundTrip() {
        val errors: List<CommonServerError> = listOf(
            CommonServerError.BadRequest,
            CommonServerError.ValidationFailed,
            CommonServerError.Unauthorized,
            CommonServerError.RateLimitExceeded,
            CommonServerError.InternalServerError,
        )

        for (err in errors) {
            val response = ApiErrorResponse(error = err, message = "error")
            val serialized = json.encodeToString(response)
            val deserialized = json.decodeFromString<ApiErrorResponse<CommonServerError>>(serialized)
            assertEquals(err, deserialized.error)
        }
    }
}
