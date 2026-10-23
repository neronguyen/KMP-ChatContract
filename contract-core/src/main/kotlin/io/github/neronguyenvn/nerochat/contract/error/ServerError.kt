package io.github.neronguyenvn.nerochat.contract.error

/**
 * Base contract interface implemented by all feature server error hierarchies
 * (`AuthServerError`, `ChatServerError`).
 */
interface ServerError {
    val code: String

    companion object {
        const val BAD_REQUEST = "BAD_REQUEST"
        const val VALIDATION_FAILED = "VALIDATION_FAILED"
        const val UNAUTHORIZED = "UNAUTHORIZED"
        const val FORBIDDEN = "FORBIDDEN"
        const val INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR"
    }
}
