package dev.prompt.knob.io.source.entry

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.prompt.knob.io.source.app.App
import dev.prompt.knob.io.source.di.initKoin

/**
 * Desktop (JVM) entry point for the PromptKnob application.
 *
 * Initialises the Koin dependency injection graph, then creates a single
 * always-on-top window and renders the shared [App] composable.
 */
public fun main(): Unit {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            // NOTE: alwaysOnTop keeps the companion app visible while the user works in
            // other applications — the knob controls the host app, so the panel must stay
            // visible without requiring Alt+Tab.
            alwaysOnTop = true,
            title = "PromptKnob",
        ) {
            App()
        }
    }
}
