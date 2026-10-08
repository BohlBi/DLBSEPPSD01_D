package org.ondeviceai.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ondeviceai.shared.components.ComingSoonPlaceholder
import org.ondeviceai.shared.theme.OnDeviceAITheme

const val SETTINGS_SCREEN_TEST_TAG = "settings_screen"

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        SettingsUiState.ComingSoon -> ComingSoonPlaceholder(modifier.testTag(SETTINGS_SCREEN_TEST_TAG))
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    OnDeviceAITheme {
        SettingsScreen()
    }
}
