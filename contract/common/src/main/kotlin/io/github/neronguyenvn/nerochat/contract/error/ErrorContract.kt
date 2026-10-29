package io.github.neronguyenvn.nerochat.contract.error

/**
 * Base class for all endpoint error contract companions.
 */
open class ErrorContract<E : ServerError>(val entries: List<E>) {
    private val byCode: Map<ErrorCode, E> = entries.associateBy { it.code }

    fun fromCode(code: ErrorCode): E? = byCode[code]
    fun fromCode(code: String): E? = byCode[ErrorCode(code)]
}
