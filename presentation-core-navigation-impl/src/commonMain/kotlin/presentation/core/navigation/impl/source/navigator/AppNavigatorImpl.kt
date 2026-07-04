package presentation.core.navigation.impl.source.navigator

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import presentation.core.navigation.api.source.destination.AppNavigator
import presentation.core.navigation.api.source.destination.AppNavigator.NavOptions.ClearTask
import presentation.core.navigation.api.source.destination.AppNavigator.NavOptions.Default
import presentation.core.navigation.api.source.destination.AppNavigator.NavOptions.SingleTop
import presentation.core.navigation.api.source.destination.Destination

/**
 * Navigation 3 implementation of [AppNavigator] backed by a [NavBackStack].
 *
 * @param backStack The mutable back stack managed by Navigation 3 runtime.
 *
 * @see AppNavigator
 * @see NavigationHost
 */
public class AppNavigatorImpl(
    private val backStack: NavBackStack<NavKey>,
) : AppNavigator {

    override fun popBackStack() {
        // Never pop the final entry: NavDisplay throws IllegalArgumentException
        // ("backstack cannot be empty") if the back stack is ever emptied.
        if (backStack.size > 1) backStack.removeLastOrNull()
    }

    override fun navigate(destination: Destination, options: AppNavigator.NavOptions) {
        when (options) {
            Default -> backStack.add(destination)
            SingleTop -> {
                if (backStack.lastOrNull() != destination) backStack.add(destination)
            }
            ClearTask -> {
                // Push the new destination first, then trim everything below it. This keeps
                // the back stack non-empty at every observable point — clearing first would
                // leave it transiently empty and can crash NavDisplay mid-recomposition.
                backStack.add(destination)
                while (backStack.size > 1) backStack.removeAt(0)
            }
        }
    }
}
