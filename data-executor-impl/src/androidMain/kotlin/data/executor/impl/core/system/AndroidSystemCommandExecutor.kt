package data.executor.impl.core.system

import android.accessibilityservice.AccessibilityService
import domain.core.source.monad.Optional
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.view.KeyEvent
import data.executor.impl.source.assistant.AssistantRobotService
import data.executor.impl.source.system.PromptKnobDeviceAdminReceiver

/**
 * Executes known system commands via [AudioManager], media key events,
 * [DevicePolicyManager], and the [AssistantRobotService] global-action API.
 *
 * Supported command strings (case-insensitive):
 *
 * **Media / Audio** (no extra permission required):
 * - `VOLUME_UP` / `VOLUME_DOWN` / `MUTE`
 * - `PLAY_PAUSE` / `NEXT_TRACK` / `PREV_TRACK`
 *
 * **Navigation** (requires accessibility service to be enabled):
 * - `BACK` — simulates the Back gesture
 * - `HOME` — simulates the Home gesture
 * - `RECENTS` — opens the Recent Apps switcher
 * - `NOTIFICATIONS` — expands the notification shade
 * - `QUICK_SETTINGS` — expands the Quick Settings panel
 * - `SCREENSHOT` — takes a screenshot (API 28+ / Android 9+; no-op on API 27)
 *
 * **Device policy** (requires device admin activation):
 * - `LOCK_SCREEN` — locks the device immediately via [DevicePolicyManager.lockNow].
 *   If admin is not yet active, opens the activation screen instead.
 *
 * Unknown strings are silently ignored.
 */
internal class AndroidSystemCommandExecutor(private val context: Context) {

    private val adminComponent by lazy {
        ComponentName(context, PromptKnobDeviceAdminReceiver::class.java)
    }

    fun execute(command: String, sortOrder: Int = -1): Optional = runCatching {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (command.uppercase().trim()) {
            // --- Audio / Media ---
            // Directional base name: sortOrder 0 = CW = raise, 1 = CCW = lower
            "VOLUME" -> if (sortOrder == 1) {
                audio.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
            } else {
                audio.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            }
            "VOLUME_UP" -> audio.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            "VOLUME_DOWN" -> audio.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
            "MUTE" -> audio.adjustVolume(AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_SHOW_UI)
            "PLAY_PAUSE" -> dispatchMediaKey(audio, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            "NEXT_TRACK" -> dispatchMediaKey(audio, KeyEvent.KEYCODE_MEDIA_NEXT)
            "PREV_TRACK" -> dispatchMediaKey(audio, KeyEvent.KEYCODE_MEDIA_PREVIOUS)

            // --- Navigation (via accessibility service global actions) ---
            "BACK" -> globalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            "HOME" -> globalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            "RECENTS" -> globalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
            "NOTIFICATIONS" -> globalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
            "QUICK_SETTINGS" -> globalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
            "SCREENSHOT" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    globalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
                }
                // Silent no-op on API 27 — action unavailable below API 28.
            }

            // --- Device policy ---
            "LOCK_SCREEN" -> lockScreen()
        }
    }

    private fun dispatchMediaKey(audio: AudioManager, keyCode: Int) {
        audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }

    private fun globalAction(action: Int) {
        if (!AssistantRobotService.triggerGlobalAction(action)) {
            // Service not connected — open accessibility settings so the user can enable it.
            context.startActivity(
                Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun lockScreen() {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isAdminActive(adminComponent)) {
            dpm.lockNow()
        } else {
            // Admin not yet active — prompt the user to activate it.
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Required for the LOCK_SCREEN command to lock your device.",
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
