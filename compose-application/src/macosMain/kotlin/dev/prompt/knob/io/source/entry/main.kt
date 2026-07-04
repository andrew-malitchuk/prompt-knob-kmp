package dev.prompt.knob.io.source.entry

import androidx.compose.ui.window.Window
import data.mcp.impl.di.dataMcpImplModule
import dev.prompt.knob.io.source.app.App
import dev.prompt.knob.io.source.di.initKoin
import domain.usecase.api.source.usecase.configuration.GetClaudeHookEnabledUseCase
import domain.usecase.api.source.usecase.mcp.StartClaudeHookServerUseCase
import domain.usecase.api.source.usecase.mcp.StartMcpServerUseCase
import domain.usecase.impl.di.domainMcpUseCaseModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import platform.AppKit.NSApplication

/**
 * macOS native entry point for the PromptKnob application.
 *
 * Uses Compose Multiplatform macOS native rendering (Skiko/Metal) with real
 * CoreBluetooth BLE support via Kable — unlike the JVM desktop target which
 * uses no-op BLE stubs.
 */
public fun main() {
    initKoin()

    // MCP + Claude hook modules are macOS-only — loaded after the common Koin graph is initialized.
    loadKoinModules(listOf(dataMcpImplModule, domainMcpUseCaseModule))
    val startMcpServer: StartMcpServerUseCase = object : KoinComponent {}.get()
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch(Dispatchers.Default) { startMcpServer() }

    val claudeHookEnabled = runBlocking {
        object : KoinComponent {}.get<GetClaudeHookEnabledUseCase>()().getOrDefault(false)
    }
    if (claudeHookEnabled) {
        val startClaudeHookServer: StartClaudeHookServerUseCase = object : KoinComponent {}.get()
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(Dispatchers.Default) { startClaudeHookServer() }
    }

    // NOTE: sharedApplication() must be called before creating the Compose Window to
    // ensure the AppKit event loop and NSApplication singleton are initialised first.
    // Skipping this call causes a crash on macOS when Compose tries to attach to the
    // run loop before NSApplication exists.
    NSApplication.sharedApplication()
    Window("PromptKnob") {
        App()
    }
    // NOTE: run() starts the AppKit main run loop and never returns — it must be the
    // last call in main(). Everything above sets up state consumed during this loop.
    NSApplication.sharedApplication().run()
}
