package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testApiErrorDtoSerialization() {
        val errorResponse = ApiErrorDto(
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
        val decoded = json.decodeFromString<ApiErrorDto>(encoded)

        assertEquals(CommonServerError.BadRequest.code, decoded.error)
        assertEquals(CommonServerError.BadRequest, CommonServerError.fromCode(decoded.error))
        assertEquals(errorResponse.message, decoded.message)
        assertEquals(errorResponse.fieldErrors, decoded.fieldErrors)
    }

    @Test
    fun testAllCommonServerErrorRoundTrip() {
        val errors: List<CommonServerError> = CommonServerError.entries

        for (err in errors) {
            val response = ApiErrorDto(error = err, message = "error")
            val serialized = json.encodeToString(response)
            val deserialized = json.decodeFromString<ApiErrorDto>(serialized)
            assertEquals(err.code, deserialized.error)
            assertEquals(err, CommonServerError.fromCode(deserialized.error))
        }
    }

    @Test
    fun testForwardCompatibilityUnknownErrorCode() {
        val jsonPayload = """{"error":"FutureService.BrandNewError","message":"Something new","fieldErrors":{}}"""
        val decoded = json.decodeFromString<ApiErrorDto>(jsonPayload)

        assertEquals("FutureService.BrandNewError", decoded.error.value)
        assertEquals("Something new", decoded.message)
        assertEquals(null, CommonServerError.fromCode(decoded.error))
    }
}
