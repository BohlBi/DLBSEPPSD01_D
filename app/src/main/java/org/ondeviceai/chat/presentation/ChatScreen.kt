package org.ondeviceai.chat.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ondeviceai.shared.theme.OnDeviceAITheme

const val CHAT_SCREEN_TEST_TAG = "chat_screen"

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .testTag(CHAT_SCREEN_TEST_TAG),
    ) {
        MessageList(messages = uiState.messages, modifier = Modifier.weight(1f))
        if (uiState.thinking) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        ChatInput(input = uiState.input, onIntent = viewModel::onIntent)
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    OnDeviceAITheme {
        ChatScreen()
    }
}
