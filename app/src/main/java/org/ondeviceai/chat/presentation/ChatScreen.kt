package org.ondeviceai.chat.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ondeviceai.OnDeviceAiApplication
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.chat.infrastructure.LiteRtChatImpl

const val CHAT_SCREEN_TEST_TAG = "chat_screen"

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = (LocalContext.current.applicationContext as OnDeviceAiApplication).let { app ->
        viewModel {
            val session = ChatSession()
            ChatViewModel(session, LiteRtChatImpl(session, app.modelStore, app.settingsStore, app.cacheDir), app.modelStore)
        }
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .testTag(CHAT_SCREEN_TEST_TAG),
    ) {
        ModelSelector(
            models = uiState.models,
            selectedModel = uiState.selectedModel,
            onIntent = viewModel::onIntent,
        )
        MessageList(messages = uiState.messages, modifier = Modifier.weight(1f))
        if (uiState.thinking) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        ChatInput(input = uiState.input, thinking = uiState.thinking, onIntent = viewModel::onIntent)
    }
}
