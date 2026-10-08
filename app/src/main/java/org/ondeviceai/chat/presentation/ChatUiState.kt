package org.ondeviceai.chat.presentation

sealed interface ChatUiState {
    data object ComingSoon : ChatUiState
}
