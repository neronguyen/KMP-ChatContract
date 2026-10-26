package io.github.neronguyenvn.nerochat.contract.validation

/**
 * Shared UUID validation rule across client and backend.
 */
object UuidValidator {

    /**
     * Standard canonical 8-4-4-4-12 UUID format.
     */
    val UUID_REGEX =
        Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

    /**
     * Returns whether [uuid] has the hexadecimal 8-4-4-4-12 format, accepting either letter case.
     * Null and blank inputs return `false`; whitespace is not trimmed and UUID version bits are not checked.
     */
    fun isValidUuid(uuid: String?): Boolean {
        return !uuid.isNullOrBlank() && UUID_REGEX.matches(uuid)
    }
}
