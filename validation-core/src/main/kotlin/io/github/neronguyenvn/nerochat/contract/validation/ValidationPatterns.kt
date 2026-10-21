package io.github.neronguyenvn.nerochat.contract.validation

/**
 * Shared validation patterns and utility rules across client and backend.
 */
object ValidationPatterns {

    const val MIN_PASSWORD_LENGTH = 10

    /**
     * Strict email regex requiring:
     * - non-empty local-part with valid characters
     * - '@' symbol
     * - domain with at least two dot-separated labels consisting of alphanumeric characters and hyphens
     */
    val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")

    /**
     * Standard canonical 8-4-4-4-12 UUID format.
     */
    val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

    /**
     * Returns whether [email] matches [EMAIL_REGEX] in full, without trimming whitespace.
     * Null and blank inputs return `false`.
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return EMAIL_REGEX.matches(email)
    }

    /**
     * Returns whether [uuid] has the hexadecimal 8-4-4-4-12 format, accepting either letter case.
     * Null and blank inputs return `false`; whitespace is not trimmed and UUID version bits are not checked.
     */
    fun isValidUuid(uuid: String?): Boolean {
        if (uuid.isNullOrBlank()) return false
        return UUID_REGEX.matches(uuid)
    }

    /**
     * Returns whether [password] has at least [MIN_PASSWORD_LENGTH] UTF-16 code units
     * and contains a letter and a digit, as recognized by [Char.isLetter] and [Char.isDigit].
     * Null and blank inputs return `false`; whitespace is preserved and counts toward the length.
     */
    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        if (password.length < MIN_PASSWORD_LENGTH) return false
        return password.any { it.isLetter() } && password.any { it.isDigit() }
    }
}
