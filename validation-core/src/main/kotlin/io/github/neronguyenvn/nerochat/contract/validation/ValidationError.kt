package io.github.neronguyenvn.nerochat.contract.validation

/**
 * Base interface for all contract and client form validation errors.
 *
 * Every validation error provides its associated target [field] name (matching the DTO property)
 * and a default human-readable error [message].
 */
interface ValidationError {
    val field: String
    val message: String
}

/**
 * Common contract interface for request payloads that can be validated before network transmission
 * or before service execution.
 */
interface Validatable<out E : ValidationError> {
    fun validate(): List<E>
}
