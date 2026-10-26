package org.ondeviceai.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ondeviceai.OnDeviceAiApplication
import org.ondeviceai.R

const val SETTINGS_SCREEN_TEST_TAG = "settings_screen"

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = (LocalContext.current.applicationContext as OnDeviceAiApplication).let { app ->
        viewModel { SettingsViewModel(app.settingsStore) }
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag(SETTINGS_SCREEN_TEST_TAG),
    ) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_use_gpu)) },
            supportingContent = { Text(stringResource(R.string.settings_use_gpu_description)) },
            trailingContent = {
                Switch(
                    checked = uiState.useGpu,
                    onCheckedChange = { viewModel.onIntent(SettingsIntent.SetUseGpu(it)) },
                )
            },
        )
    }
}
