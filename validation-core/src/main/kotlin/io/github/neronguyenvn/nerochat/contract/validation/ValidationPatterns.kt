package io.github.neronguyenvn.nerochat.contract.validation

/**
 * Shared validation patterns and utility rules across client and backend.
 */
object ValidationPatterns {

    const val MIN_PASSWORD_LENGTH = 10

    /**
     * Strict ASCII-only email regex requiring:
     * - non-empty local-part of dot-separated alphanumeric, plus, underscore, or hyphen tokens (no leading, trailing, or consecutive dots)
     * - '@' symbol
     * - domain with at least two dot-separated labels consisting of alphanumeric characters and hyphens, where each label does not start with a hyphen
     *
     * Note: This pattern intentionally enforces an ASCII-only email contract. Non-ASCII addresses such as
     * internationalized domain names (IDNA) and SMTPUTF8 (RFC 6530/6531) are intentionally not supported unless
     * domains are pre-encoded in ASCII Punycode (e.g. `xn--...`).
     */
    val EMAIL_REGEX =
        Regex("^[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@[A-Za-z0-9][A-Za-z0-9-]*(\\.[A-Za-z0-9][A-Za-z0-9-]*)+$")

    /**
     * Standard canonical 8-4-4-4-12 UUID format.
     */
    val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

    /**
     * Returns whether [email] matches [EMAIL_REGEX] in full, without trimming whitespace.
     * Null and blank inputs return `false`.
     *
     * Note: Validates against an intentional ASCII-only format; non-ASCII characters (SMTPUTF8/IDNA) are not accepted.
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && EMAIL_REGEX.matches(email)
    }

    /**
     * Returns whether [uuid] has the hexadecimal 8-4-4-4-12 format, accepting either letter case.
     * Null and blank inputs return `false`; whitespace is not trimmed and UUID version bits are not checked.
     */
    fun isValidUuid(uuid: String?): Boolean {
        return !uuid.isNullOrBlank() && UUID_REGEX.matches(uuid)
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
