package org.ondeviceai.models.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ondeviceai.shared.components.ComingSoonPlaceholder
import org.ondeviceai.shared.theme.OnDeviceAITheme

const val MODELS_SCREEN_TEST_TAG = "models_screen"

@Composable
fun ModelsScreen(
    modifier: Modifier = Modifier,
    viewModel: ModelsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        ModelsUiState.ComingSoon -> ComingSoonPlaceholder(modifier.testTag(MODELS_SCREEN_TEST_TAG))
    }
}

@Preview(showBackground = true)
@Composable
private fun ModelsScreenPreview() {
    OnDeviceAITheme {
        ModelsScreen()
    }
}
