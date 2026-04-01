package dev.prompt.knob.io

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

/**
 * Single-activity entry point for the Android application.
 *
 * Enables edge-to-edge rendering and delegates all UI to the shared
 * Compose [App] composable.
 *
 * @see App
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Composable
fun AppAndroidPreview() {
    App()
}

// add ui modifier
