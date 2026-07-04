package data.executor.impl.core.shell

import domain.core.source.monad.Optional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSTask
import platform.Foundation.launch
import platform.Foundation.launchPath
import platform.Foundation.waitUntilExit

/**
 * Executes an arbitrary shell command via `/bin/zsh -l -c`.
 *
 * The `-l` (login) flag loads the user's shell profile (`~/.zshrc`, `/etc/zprofile`, etc.)
 * so the full `PATH` is available — including Homebrew, pyenv, nvm, and other
 * user-installed tools. Without `-l`, only `/usr/bin/` and `/bin/` are on the path.
 *
 * Runs on [Dispatchers.Default] to avoid blocking the calling coroutine.
 * No output is captured; use for fire-and-forget side effects.
 *
 * The deprecated `launchPath`/`launch()` API is used intentionally — it covers
 * macOS 10.13+ which includes all currently supported targets. The newer
 * `executableURL`/`run(error:)` API is equivalent at runtime.
 */
@Suppress("DEPRECATION")
internal class ShellCommandExecutor {

    suspend fun execute(command: String): Optional = withContext(Dispatchers.Default) {
        runCatching {
            val task = NSTask()
            task.launchPath = "/bin/zsh"
            task.arguments = listOf("-l", "-c", command)
            task.launch()
            task.waitUntilExit()
        }
    }
}
