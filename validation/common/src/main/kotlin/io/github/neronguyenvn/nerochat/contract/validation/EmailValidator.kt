package io.github.neronguyenvn.nerochat.contract.validation

/**
 * Shared ASCII-only email validation and normalization rule across client and backend.
 */
object EmailValidator {

    /**
     * Strict ASCII-only email regex requiring:
     * - non-empty local-part of dot-separated alphanumeric, plus, underscore, or hyphen tokens (no leading, trailing, or consecutive dots)
     * - '@' symbol
     * - domain with at least two dot-separated labels consisting of alphanumeric characters and hyphens, where each label does not start with a hyphen
     */
    val EMAIL_REGEX =
        Regex("^[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@[A-Za-z0-9][A-Za-z0-9-]*(\\.[A-Za-z0-9][A-Za-z0-9-]*)+$")

    /**
     * Canonicalizes [email] by trimming leading/trailing whitespace and converting to lowercase.
     */
    fun normalize(email: String): String = email.trim().lowercase()

    /**
     * Returns whether [email] matches [EMAIL_REGEX] in full, without trimming whitespace.
     * Null and blank inputs return `false`.
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && EMAIL_REGEX.matches(email)
    }
}
