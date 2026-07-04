package presentation.core.ui.source.kit.atom.ble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme

/**
 * Renders a 4-bar signal strength indicator based on [rssi] value.
 *
 * Active bars are filled with [Theme.color.inkMain]; inactive bars use [Theme.color.outlineLow].
 * The RSSI-to-bar mapping follows standard BLE signal conventions:
 * - ≥ -50 dBm → 4 bars
 * - ≥ -65 dBm → 3 bars
 * - ≥ -80 dBm → 2 bars
 * - < -80 dBm or [Int.MIN_VALUE] → 1 bar
 */
@Composable
public fun SignalBars(rssi: Int, modifier: Modifier = Modifier) {
    val bars = when {
        rssi == Int.MIN_VALUE -> 1
        rssi >= -50 -> 4
        rssi >= -65 -> 3
        rssi >= -80 -> 2
        else -> 1
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        for (i in 1..4) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((4 + i * 3).dp)
                    .clip(Theme.cornerToken.chip)
                    .background(if (i <= bars) Theme.color.inkMain else Theme.color.outlineLow),
            )
        }
    }
}
