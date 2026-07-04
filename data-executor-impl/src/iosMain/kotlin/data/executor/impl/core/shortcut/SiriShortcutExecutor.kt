package data.executor.impl.core.shortcut

import domain.core.source.monad.Optional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Opens a named Siri Shortcut via the `shortcuts://run-shortcut?name=…` URL scheme.
 *
 * The user must have created a Shortcut in the Apple Shortcuts app with the
 * same name as [CommandNodeModel.command]. When triggered, iOS opens the Shortcuts
 * app and runs the automation immediately.
 *
 * The shortcut name is percent-encoded so names containing spaces and special
 * characters work correctly in the URL.
 *
 * Must be called on the main thread; this function dispatches internally.
 */
internal class SiriShortcutExecutor {

    suspend fun execute(shortcutName: String): Optional = withContext(Dispatchers.Main) {
        runCatching {
            val encoded = shortcutName.percentEncodeQueryComponent()
            val url = NSURL.URLWithString("shortcuts://run-shortcut?name=$encoded")
                ?: error("Could not build Shortcuts URL for: $shortcutName")
            UIApplication.sharedApplication.openURL(url)
            Unit
        }
    }

    private fun String.percentEncodeQueryComponent(): String =
        encodeToByteArray().joinToString("") { byte ->
            val i = byte.toInt() and 0xFF
            if ((i in 0x41..0x5A) || (i in 0x61..0x7A) || (i in 0x30..0x39) ||
                i == 0x2D || i == 0x5F || i == 0x2E || i == 0x7E
            ) i.toChar().toString()
            else "%${i.toString(16).uppercase().padStart(2, '0')}"
        }
}
