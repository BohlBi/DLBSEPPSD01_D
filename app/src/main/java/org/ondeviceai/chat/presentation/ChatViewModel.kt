package org.ondeviceai.chat.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.ComingSoon)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ChatIntent) {}
}
