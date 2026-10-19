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

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return EMAIL_REGEX.matches(email)
    }

    fun isValidUuid(uuid: String?): Boolean {
        if (uuid.isNullOrBlank()) return false
        return UUID_REGEX.matches(uuid)
    }

    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        if (password.length < MIN_PASSWORD_LENGTH) return false
        return password.any { it.isLetter() } && password.any { it.isDigit() }
    }
}
