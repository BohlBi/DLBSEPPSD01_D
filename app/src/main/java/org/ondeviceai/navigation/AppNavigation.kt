package org.ondeviceai.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.ondeviceai.chat.presentation.ChatScreen
import org.ondeviceai.models.presentation.ModelsScreen
import org.ondeviceai.settings.presentation.SettingsScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ChatRoute)

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                item(
                    selected = destination.route == backStack.last(),
                    onClick = { if (backStack.last() != destination.route) backStack.add(destination.route) },
                    icon = { Icon(destination.icon, contentDescription = null) },
                    label = { Text(stringResource(destination.labelRes)) },
                )
            }
        },
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<ChatRoute> { ChatScreen() }
                entry<ModelsRoute> { ModelsScreen() }
                entry<SettingsRoute> { SettingsScreen() }
            },
        )
    }
}
