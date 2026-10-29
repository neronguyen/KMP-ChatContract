package io.github.neronguyenvn.nerochat.contract.error

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * Type-safe value class wrapper around server error code wire strings.
 * Serializes directly as a JSON string.
 */
@JvmInline
@Serializable
value class ErrorCode(val value: String) {
    override fun toString(): String = value
}
