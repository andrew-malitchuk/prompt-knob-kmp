package data.executor.impl.core.assistant

import android.content.Context
import android.content.Intent
import domain.core.source.monad.Optional
import data.executor.impl.source.assistant.AssistantRobotService

/**
 * Launches Google Assistant with [command] as the pending prompt.
 *
 * Requires [AssistantRobotService] to be enabled in Accessibility Settings.
 * The service will auto-type [command] into the Assistant input and submit it.
 *
 * Falls back to [Intent.ACTION_WEB_SEARCH] if `ACTION_VOICE_COMMAND` is unavailable
 * (e.g., on devices without the Google Quick Search Box).
 */
internal class AssistantPromptExecutor(private val context: Context) {

    fun execute(command: String): Optional = runCatching {
        AssistantRobotService.pendingCommand = command
        AssistantRobotService.shouldExecute = true

        val voiceIntent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
            setPackage("com.google.android.googlequicksearchbox")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(voiceIntent)
        } catch (_: Exception) {
            // Fallback: ACTION_ASSIST still opens Google Assistant so the
            // accessibility service can type into it — unlike ACTION_WEB_SEARCH
            // which bypasses the Assistant UI entirely.
            context.startActivity(
                Intent(Intent.ACTION_ASSIST).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}
