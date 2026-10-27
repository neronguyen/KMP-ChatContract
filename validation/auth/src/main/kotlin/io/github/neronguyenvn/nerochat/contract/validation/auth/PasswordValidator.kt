package io.github.neronguyenvn.nerochat.contract.validation.auth

/**
 * Shared password validation rule across client and backend.
 */
object PasswordValidator {

    const val MIN_PASSWORD_LENGTH = 10

    /**
     * Returns whether [password] has at least [MIN_PASSWORD_LENGTH] Unicode code points
     * and contains a letter and a digit, as recognized by [Char.isLetter] and [Char.isDigit].
     * Null and blank inputs return `false`; whitespace is preserved and counts toward the length.
     */
    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        val codePoints = password.codePointCount(0, password.length)
        if (codePoints < MIN_PASSWORD_LENGTH) return false

        var hasLetter = false
        var hasDigit = false
        for (char in password) {
            if (char.isLetter()) hasLetter = true
            if (char.isDigit()) hasDigit = true
            if (hasLetter && hasDigit) return true
        }
        return false
    }
}
