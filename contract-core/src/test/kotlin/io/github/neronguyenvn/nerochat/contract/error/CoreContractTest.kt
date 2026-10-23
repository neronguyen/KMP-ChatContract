package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class CoreContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testApiErrorResponseSerialization() {
        val errorResponse = ApiErrorResponse(
            code = ServerError.BAD_REQUEST,
            message = "Invalid parameter",
            errors = listOf("Invalid format"),
            fieldErrors = mapOf("id" to "Invalid UUID"),
            retryAfterSeconds = null,
        )

        val encoded = json.encodeToString(errorResponse)
        val decoded = json.decodeFromString<ApiErrorResponse>(encoded)

        assertEquals(errorResponse, decoded)
        assertEquals(ServerError.BAD_REQUEST, decoded.code)
    }

    @Test
    fun testServerErrorConstants() {
        assertEquals("BAD_REQUEST", ServerError.BAD_REQUEST)
        assertEquals("VALIDATION_FAILED", ServerError.VALIDATION_FAILED)
        assertEquals("UNAUTHORIZED", ServerError.UNAUTHORIZED)
        assertEquals("FORBIDDEN", ServerError.FORBIDDEN)
        assertEquals("INTERNAL_SERVER_ERROR", ServerError.INTERNAL_SERVER_ERROR)
    }
}
