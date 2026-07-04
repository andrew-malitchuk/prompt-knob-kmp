package domain.core.source.model

/** Claude Code session state surfaced on the physical device. */
public enum class ClaudeState {
    /** No active Claude Code session or session has ended. */
    IDLE,

    /** Claude Code is actively running a tool or processing. */
    WORKING,

    /** Claude Code is waiting for user input or confirmation. */
    WAITING,

    /** Task completed successfully. */
    DONE,

    /** An error occurred in the current session. */
    ERROR,
}
