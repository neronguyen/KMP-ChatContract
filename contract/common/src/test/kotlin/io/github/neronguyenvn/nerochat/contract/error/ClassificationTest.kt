package io.github.neronguyenvn.nerochat.contract.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ClassificationTest {

    // Dummy test contract to test classification in :contract:common
    private sealed interface TestFeatureError : ServerError {
        companion object : ErrorContract<TestFeatureError>(listOf(ItemNotFound))
    }

    private data object ItemNotFound : TestFeatureError {
        override val code = ErrorCode("TestFeature.ItemNotFound")
    }

    @Test
    fun `toFailure correctly classifies business error`() {
        val response = ApiErrorResponse(
            error = ItemNotFound.code,
            message = "Item not found",
            fieldErrors = mapOf("id" to "123"),
            retryAfterSeconds = null,
        )

        val failure = response.toFailure(TestFeatureError, 404)
        assertIs<ApiFailure.Business<TestFeatureError>>(failure)
        assertEquals(ItemNotFound, failure.error)
        assertEquals("Item not found", failure.details.message)
        assertEquals(mapOf("id" to "123"), failure.details.fieldErrors)
    }

    @Test
    fun `toFailure correctly classifies common error`() {
        val response = ApiErrorResponse(
            error = CommonServerError.Unauthorized.code,
            message = "Authentication required",
        )

        val failure = response.toFailure(TestFeatureError, 401)
        assertIs<ApiFailure.Common>(failure)
        assertEquals(CommonServerError.Unauthorized, failure.error)
        assertEquals("Authentication required", failure.details.message)
    }

    @Test
    fun `toFailure correctly classifies unknown error for forward compatibility`() {
        val response = ApiErrorResponse(
            error = ErrorCode("FutureService.UnknownCode"),
            message = "Something new",
        )

        val failure = response.toFailure(TestFeatureError, 502)
        assertIs<ApiFailure.Unknown>(failure)
        assertEquals(ErrorCode("FutureService.UnknownCode"), failure.code)
        assertEquals(502, failure.status)
        assertEquals("Something new", failure.details.message)
    }

    @Test
    fun `toFailure gives precedence to business contract over common contract if identical code`() {
        // Even if an error code coincided, contract is checked first
        val response = ApiErrorResponse(
            error = ItemNotFound.code,
            message = "Item not found",
        )
        val failure = response.toFailure(TestFeatureError, 404)
        assertIs<ApiFailure.Business<TestFeatureError>>(failure)
    }
}
