package org.ondeviceai.chat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ondeviceai.shared.components.ComingSoonPlaceholder
import org.ondeviceai.shared.theme.OnDeviceAITheme

const val CHAT_SCREEN_TEST_TAG = "chat_screen"

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        ChatUiState.ComingSoon -> ComingSoonPlaceholder(modifier.testTag(CHAT_SCREEN_TEST_TAG))
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    OnDeviceAITheme {
        ChatScreen()
    }
}
