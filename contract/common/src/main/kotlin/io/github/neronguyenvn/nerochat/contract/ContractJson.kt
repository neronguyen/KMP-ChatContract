package io.github.neronguyenvn.nerochat.contract

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/**
 * Standard serialization configuration for all client-server wire communication (HTTP / WebSocket).
 */
fun JsonBuilder.applyContractDefaults() {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}

/**
 * Shared [Json] instance for client-server wire contracts, WebSocket frames, and contract tests.
 * Do NOT reuse this for internal persistence (e.g. Android DataStore) or internal message queues (e.g. AMQP).
 */
val ContractJson: Json = Json {
    applyContractDefaults()
}
