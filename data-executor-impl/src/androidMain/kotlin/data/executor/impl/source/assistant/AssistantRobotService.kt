package data.executor.impl.source.assistant

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.lang.ref.WeakReference

/**
 * Accessibility service that drives the Google Assistant UI to submit a prompt
 * and supports issuing global system actions (Back, Home, Recents, etc.).
 *
 * ## Prompt flow
 * 1. Caller sets [pendingCommand] and flips [shouldExecute] = true.
 * 2. Caller launches Google Assistant via [AssistantPromptExecutor].
 * 3. When the Assistant window appears, this service finds the text input field,
 *    types [pendingCommand] into it, then clicks the send button.
 *
 * ## Global action flow
 * Call [triggerGlobalAction] with an [AccessibilityService].GLOBAL_ACTION_* constant.
 * The action is dispatched immediately on the service if it is connected.
 *
 * The user must enable this service once in
 * Settings > Accessibility > Installed apps > PromptKnob.
 */
public class AssistantRobotService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = WeakReference(this)
    }

    override fun onUnbind(intent: Intent): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!shouldExecute) return

        val rootNode = rootInActiveWindow ?: return
        val inputNode = findInputNode(rootNode)

        if (inputNode != null) {
            val current = inputNode.text?.toString() ?: ""
            if (!current.equals(pendingCommand, ignoreCase = true)) {
                inputNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                val args = Bundle()
                args.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    pendingCommand,
                )
                val ok = inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
                Log.d(TAG, "Set text: $ok")
            } else {
                if (tryClickSend(rootNode)) {
                    Log.d(TAG, "Sent — resetting flag")
                    shouldExecute = false
                }
            }
        }
    }

    override fun onInterrupt(): Unit = Unit


    private fun findInputNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val ids = arrayOf(
            "com.google.android.googlequicksearchbox:id/assistant_robin_input_collapsed_text_half_sheet",
            "com.google.android.googlequicksearchbox:id/input_text_box",
        )
        for (id in ids) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes.isNotEmpty()) return nodes[0]
        }
        return findEditableRecursively(root)
    }

    private fun findEditableRecursively(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isEditable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findEditableRecursively(child)
            if (found != null) return found
        }
        return null
    }

    private fun tryClickSend(root: AccessibilityNodeInfo): Boolean {
        val sendIds = arrayOf(
            "com.google.android.googlequicksearchbox:id/assistant_robin_send_icon_button",
            "com.google.android.googlequicksearchbox:id/send_button",
        )
        for (id in sendIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes.isNotEmpty()) {
                val btn = nodes[0]
                if (btn.isClickable) return btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (btn.parent?.isClickable == true) return btn.parent.performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                )
            }
        }
        return false
    }

    public companion object {
        private const val TAG = "AssistantRobotService"

        /** Set the prompt text before triggering [shouldExecute]. */
        @Volatile public var pendingCommand: String = ""

        /** Flip to true after setting [pendingCommand] to trigger execution. */
        @Volatile public var shouldExecute: Boolean = false

        private var instance: WeakReference<AssistantRobotService>? = null

        /**
         * Dispatches an [AccessibilityService].GLOBAL_ACTION_* constant on the
         * running service instance. Returns false if the service is not connected
         * (i.e., the user has not enabled it in accessibility settings).
         */
        public fun triggerGlobalAction(action: Int): Boolean =
            instance?.get()?.performGlobalAction(action) ?: false
    }
}
