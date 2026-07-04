package data.mcp.impl.source.server

import co.touchlab.kermit.Logger
import data.mcp.api.core.config.McpServerConfig
import data.mcp.api.source.server.McpServer
import domain.usecase.api.source.usecase.ble.AwaitCommandSelectedUseCase
import domain.usecase.api.source.usecase.ble.SendNotifyUseCase
import domain.usecase.api.source.usecase.ble.SendShowApprovalUseCase
import domain.usecase.api.source.usecase.ble.SendShowChoiceUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.*

internal class McpServerImpl(
    private val config: McpServerConfig,
    private val sendShowApproval: SendShowApprovalUseCase,
    private val sendShowChoice: SendShowChoiceUseCase,
    private val sendNotify: SendNotifyUseCase,
    private val awaitCommandSelected: AwaitCommandSelectedUseCase,
) : McpServer {

    private val log = Logger.withTag("McpServerImpl")
    private var engine: EmbeddedServer<*, *>? = null
    private val sessions = HashMap<String, Channel<String>>()
    private val sessionsMutex = Mutex()

    override suspend fun start() {
        engine = embeddedServer(CIO, host = config.host, port = config.port) {
            install(SSE)
            routing { configureMcpRoutes() }
        }
        log.i { "MCP SSE server starting on ${config.host}:${config.port}" }
        engine?.start(wait = true)
    }

    override suspend fun stop() {
        engine?.stop(gracePeriodMillis = 1_000, timeoutMillis = 5_000)
        engine = null
        log.i { "MCP server stopped" }
    }

    private fun Routing.configureMcpRoutes() {
        sse("/sse") {
            val sessionId = newSessionId()
            val channel = Channel<String>(Channel.UNLIMITED)
            sessionsMutex.withLock { sessions[sessionId] = channel }

            val userAgent = call.request.headers["User-Agent"] ?: "unknown"
            val remoteAddr = call.request.local.remoteAddress
            send(ServerSentEvent(data = "/message?sessionId=$sessionId", event = "endpoint"))
            log.i { "MCP SSE connected: session=$sessionId addr=$remoteAddr UA=$userAgent" }

            try {
                for (msg in channel) {
                    send(ServerSentEvent(data = msg, event = "message"))
                }
            } finally {
                sessionsMutex.withLock { sessions.remove(sessionId) }
                channel.close()
                log.d { "SSE session closed: $sessionId" }
            }
        }

        post("/message") {
            val sessionId = call.request.queryParameters["sessionId"]
            if (sessionId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing sessionId")
                return@post
            }
            val channel = sessionsMutex.withLock { sessions[sessionId] }
            if (channel == null) {
                call.respond(HttpStatusCode.NotFound, "Session not found")
                return@post
            }

            val body = call.receiveText()
            val userAgent = call.request.headers["User-Agent"] ?: "unknown"
            val remoteAddr = call.request.local.remoteAddress
            log.i { "MCP ← [$remoteAddr] UA=$userAgent body=$body" }
            val response = handleJsonRpc(body)
            if (response != null) channel.send(response)

            call.respond(HttpStatusCode.Accepted, "")
        }
    }

    private suspend fun handleJsonRpc(body: String): String? {
        val request = runCatching { Json.parseToJsonElement(body).jsonObject }.getOrElse {
            log.w { "Invalid JSON body: ${it.message}" }
            return null
        }

        val id: JsonElement? = request["id"]
        val method = request["method"]?.jsonPrimitive?.content ?: return null
        val params = request["params"]?.jsonObject ?: JsonObject(emptyMap())

        // Notifications have no id — no response required
        if (id == null || id == JsonNull) {
            log.d { "MCP notification: $method" }
            return null
        }

        log.d { "MCP request: $method" }

        return when (method) {
            "initialize" -> buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", id)
                putJsonObject("result") {
                    put("protocolVersion", "2024-11-05")
                    putJsonObject("capabilities") { putJsonObject("tools") {} }
                    putJsonObject("serverInfo") {
                        put("name", "prompt-knob")
                        put("version", "1.0.0")
                    }
                }
            }.toString()

            "tools/list" -> buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", id)
                putJsonObject("result") {
                    putJsonArray("tools") {
                        addJsonObject {
                            put("name", "request_approval")
                            put("description", "Request user approval via the physical PromptKnob knob device. The device shows an approval screen; the user taps to approve or swipes to reject. Blocks until the user responds or the 30-second timeout expires.")
                            putJsonObject("inputSchema") {
                                put("type", "object")
                                putJsonObject("properties") {
                                    putJsonObject("reason") {
                                        put("type", "string")
                                        put("description", "Human-readable description of the action requiring approval")
                                    }
                                }
                                putJsonArray("required") { add("reason") }
                            }
                        }
                        addJsonObject {
                            put("name", "request_choice")
                            put("description", "Ask the user to pick one option via the physical PromptKnob knob device. The device shows a scrollable list; the user rotates to navigate and taps to confirm, or swipes to cancel. Blocks until the user responds or the timeout expires. Returns the selected option string, or null if cancelled.")
                            putJsonObject("inputSchema") {
                                put("type", "object")
                                putJsonObject("properties") {
                                    putJsonObject("options") {
                                        put("type", "array")
                                        putJsonObject("items") { put("type", "string") }
                                        put("description", "List of options to display on the device (2–8 short strings recommended)")
                                        put("minItems", 2)
                                    }
                                    putJsonObject("prompt") {
                                        put("type", "string")
                                        put("description", "Optional question or context shown as the screen title")
                                    }
                                }
                                putJsonArray("required") { add("options") }
                            }
                        }
                        addJsonObject {
                            put("name", "notify")
                            put("description", "Send a fire-and-forget notification to the physical PromptKnob knob device. The device briefly displays the message and plays a haptic/LED pulse. Does not wait for any user response — returns immediately. Use this to signal task completion, errors, or status updates.")
                            putJsonObject("inputSchema") {
                                put("type", "object")
                                putJsonObject("properties") {
                                    putJsonObject("message") {
                                        put("type", "string")
                                        put("description", "Short text to display on the device screen (≤32 chars recommended)")
                                    }
                                    putJsonObject("level") {
                                        put("type", "string")
                                        put("description", "Severity hint for LED/haptic: info | success | warning | error")
                                        putJsonArray("enum") {
                                            add("info"); add("success"); add("warning"); add("error")
                                        }
                                    }
                                }
                                putJsonArray("required") { add("message") }
                            }
                        }
                    }
                }
            }.toString()

            "tools/call" -> {
                val toolName = request["params"]?.jsonObject?.get("name")?.jsonPrimitive?.content
                val arguments = params["arguments"]?.jsonObject ?: JsonObject(emptyMap())

                when (toolName) {
                    "request_approval" -> {
                        val reason = runCatching { arguments["reason"]?.jsonPrimitive?.content }.getOrNull() ?: "Unknown action"
                        log.i { "request_approval: reason=$reason" }

                        // Subscribe before sending the opcode to avoid missing the response event.
                        val cmdId = coroutineScope {
                            val awaited = async { awaitCommandSelected(config.approvalTimeoutMs) }
                            sendShowApproval()
                            awaited.await().getOrNull()
                        }

                        val resultText = when {
                            cmdId == null -> """{"approved":false,"reason":"timeout"}"""
                            cmdId > 0 -> """{"approved":true,"cmdId":$cmdId}"""
                            else -> """{"approved":false,"reason":"rejected"}"""
                        }
                        log.i { "request_approval result: $resultText" }

                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("id", id)
                            putJsonObject("result") {
                                putJsonArray("content") {
                                    addJsonObject {
                                        put("type", "text")
                                        put("text", resultText)
                                    }
                                }
                            }
                        }.toString()
                    }

                    "request_choice" -> {
                        val options = runCatching {
                            arguments["options"]?.jsonArray?.map { it.jsonPrimitive.content }
                        }.getOrNull().orEmpty()

                        if (options.size < 2) {
                            return errorResponse(id, -32602, "request_choice requires at least 2 options")
                        }

                        log.i { "request_choice: options=$options" }

                        // Subscribe before sending the opcode to avoid missing the response event.
                        val cmdId = coroutineScope {
                            val awaited = async { awaitCommandSelected(config.approvalTimeoutMs) }
                            sendShowChoice(options)
                            awaited.await().getOrNull()
                        }

                        val resultText = when {
                            cmdId == null -> """{"selected":null,"reason":"timeout"}"""
                            cmdId == 0 -> """{"selected":null,"reason":"cancelled"}"""
                            else -> {
                                val index = cmdId - 1
                                val selected = options.getOrNull(index)
                                if (selected != null) {
                                    """{"selected":${JsonPrimitive(selected)},"index":$index}"""
                                } else {
                                    """{"selected":null,"reason":"out_of_range","cmdId":$cmdId}"""
                                }
                            }
                        }
                        log.i { "request_choice result: $resultText" }

                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("id", id)
                            putJsonObject("result") {
                                putJsonArray("content") {
                                    addJsonObject {
                                        put("type", "text")
                                        put("text", resultText)
                                    }
                                }
                            }
                        }.toString()
                    }

                    "notify" -> {
                        val message = runCatching { arguments["message"]?.jsonPrimitive?.content }.getOrNull() ?: ""
                        if (message.isBlank()) {
                            return errorResponse(id, -32602, "notify requires a non-empty message")
                        }
                        val level = when (arguments["level"]?.jsonPrimitive?.content) {
                            "success" -> 1
                            "warning" -> 2
                            "error" -> 3
                            else -> 0
                        }
                        log.i { "notify: message=$message level=$level" }
                        sendNotify(message, level)

                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("id", id)
                            putJsonObject("result") {
                                putJsonArray("content") {
                                    addJsonObject {
                                        put("type", "text")
                                        put("text", """{"sent":true}""")
                                    }
                                }
                            }
                        }.toString()
                    }

                    else -> errorResponse(id, -32601, "Tool not found: $toolName")
                }
            }

            "ping" -> buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", id)
                putJsonObject("result") {}
            }.toString()

            else -> {
                log.w { "Unknown MCP method: $method" }
                null
            }
        }
    }

    private fun errorResponse(id: JsonElement, code: Int, message: String): String =
        buildJsonObject {
            put("jsonrpc", "2.0")
            put("id", id)
            putJsonObject("error") {
                put("code", code)
                put("message", message)
            }
        }.toString()

    private fun newSessionId(): String =
        "pk-${(1..12).map { ('a'..'z').random() }.joinToString("")}"
}
