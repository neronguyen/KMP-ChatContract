package io.github.neronguyenvn.nerochat.contract.error

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiErrorDtoGoldenWireTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `ApiErrorDto serializes to golden wire json format`() {
        val dto = ApiErrorDto(
            error = CommonServerError.BadRequest,
            message = "Invalid request",
        )
        val expectedJson = """{"error":"CommonServerError.BadRequest","message":"Invalid request"}"""

        val actualJson = json.encodeToString(dto)
        assertEquals(expectedJson, actualJson)

        val deserialized = json.decodeFromString<ApiErrorDto>(expectedJson)
        assertEquals(dto, deserialized)
    }

    @Test
    fun `ApiErrorDto with fieldErrors and retryAfterSeconds serializes to golden wire json format`() {
        val dto = ApiErrorDto(
            error = CommonServerError.ValidationFailed,
            message = "Validation failed",
            fieldErrors = mapOf("email" to "Invalid email format"),
            retryAfterSeconds = 60L,
        )
        val expectedJson = """{"error":"CommonServerError.ValidationFailed","message":"Validation failed","fieldErrors":{"email":"Invalid email format"},"retryAfterSeconds":60}"""

        val actualJson = json.encodeToString(dto)
        assertEquals(expectedJson, actualJson)

        val deserialized = json.decodeFromString<ApiErrorDto>(expectedJson)
        assertEquals(dto, deserialized)
    }

    @Test
    fun `ApiErrorDto with unknown error code deserializes cleanly for forward compatibility`() {
        val payload = """{"error":"UnknownService.SomeFutureError","message":"Future error occurred","fieldErrors":{"field":"detail"},"retryAfterSeconds":30}"""
        val decoded = json.decodeFromString<ApiErrorDto>(payload)

        assertEquals("UnknownService.SomeFutureError", decoded.error.value)
        assertEquals("Future error occurred", decoded.message)
        assertEquals(mapOf("field" to "detail"), decoded.fieldErrors)
        assertEquals(30L, decoded.retryAfterSeconds)
    }
}
