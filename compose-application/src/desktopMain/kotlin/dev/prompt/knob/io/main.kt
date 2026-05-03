package dev.prompt.knob.io

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

/**
 * Desktop (JVM) entry point for the PromptKnob application.
 *
 * Creates a single always-on-top window and renders the shared [App] composable.
 */
public fun main(): Unit = application {
    Window(
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
        title = "PromptKnob",
    ) {
        App()
    }
}

// fix recomposition
