package io.github.neronguyenvn.nerochat.contract.error

/**
 * Base contract interface implemented by all server error hierarchies.
 */
interface ServerError {
    val code: ErrorCode
}
