package data.mcp.impl.source.server

import co.touchlab.kermit.Logger
import data.mcp.api.core.config.ClaudeHookServerConfig
import domain.core.source.model.ClaudeState
import data.mcp.api.source.server.ClaudeHookServer
import domain.usecase.api.source.usecase.ble.SendClaudeStateUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

internal class ClaudeHookServerImpl(
    private val config: ClaudeHookServerConfig,
    private val sendClaudeState: SendClaudeStateUseCase,
) : ClaudeHookServer {

    private val log = Logger.withTag("ClaudeHookServerImpl")
    private var engine: EmbeddedServer<*, *>? = null

    override suspend fun start() {
        engine = embeddedServer(CIO, host = config.host, port = config.port) {
            routing {
                post("/state") {
                    val body = call.receiveText().trim().lowercase()
                    val state = when (body) {
                        "working" -> ClaudeState.WORKING
                        "waiting" -> ClaudeState.WAITING
                        "done" -> ClaudeState.DONE
                        "error" -> ClaudeState.ERROR
                        else -> ClaudeState.IDLE
                    }
                    log.d { "Claude hook: $body → $state" }
                    sendClaudeState(state)
                    call.respond(HttpStatusCode.OK, "")
                }
            }
        }
        log.i { "Claude hook server starting on ${config.host}:${config.port}" }
        engine?.start(wait = true)
    }

    override suspend fun stop() {
        engine?.stop(gracePeriodMillis = 1_000, timeoutMillis = 5_000)
        engine = null
        log.i { "Claude hook server stopped" }
    }
}
