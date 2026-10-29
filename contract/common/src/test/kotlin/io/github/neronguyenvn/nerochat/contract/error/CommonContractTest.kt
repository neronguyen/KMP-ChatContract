package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testApiErrorResponseSerialization() {
        val errorResponse = ApiErrorResponse(
            error = CommonServerError.BadRequest,
            message = "Invalid parameter",
            fieldErrors = mapOf("id" to "Invalid UUID"),
            retryAfterSeconds = null,
        )

        val encoded = json.encodeToString(errorResponse)
        assertTrue(
            encoded.contains("\"error\":\"CommonServerError.BadRequest\""),
            "Expected wire format to contain 'error':'CommonServerError.BadRequest' but was: $encoded"
        )
        val decoded = json.decodeFromString<ApiErrorResponse>(encoded)

        assertEquals(CommonServerError.BadRequest.code, decoded.error)
        assertEquals(CommonServerError.BadRequest, CommonErrors.fromCode(decoded.error))
        assertEquals(errorResponse.message, decoded.message)
        assertEquals(errorResponse.fieldErrors, decoded.fieldErrors)
        assertEquals(CommonServerError.BadRequest.code, decoded.error)
    }

    @Test
    fun testAllCommonServerErrorRoundTrip() {
        val errors: List<CommonServerError> = CommonErrors.entries

        for (err in errors) {
            val response = ApiErrorResponse(error = err, message = "error")
            val serialized = json.encodeToString(response)
            val deserialized = json.decodeFromString<ApiErrorResponse>(serialized)
            assertEquals(err.code, deserialized.error)
            assertEquals(err, CommonErrors.fromCode(deserialized.error))
        }
    }

    @Test
    fun testForwardCompatibilityUnknownErrorCode() {
        val jsonPayload = """{"error":"FutureService.BrandNewError","message":"Something new","fieldErrors":{}}"""
        val decoded = json.decodeFromString<ApiErrorResponse>(jsonPayload)

        assertEquals("FutureService.BrandNewError", decoded.error.value)
        assertEquals("Something new", decoded.message)
        assertEquals(null, CommonErrors.fromCode(decoded.error))
    }
}
