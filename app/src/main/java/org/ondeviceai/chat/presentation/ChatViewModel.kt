package org.ondeviceai.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession

sealed interface ChatIntent {
    data class InputChanged(val text: String) : ChatIntent
    data object SendMessage : ChatIntent
}

class ChatViewModel(
    session: ChatSession,
    private val chatService: ChatService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        collectSession(session)
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.InputChanged -> _uiState.update { it.copy(input = intent.text) }
            ChatIntent.SendMessage -> sendMessage()
        }
    }

    private fun sendMessage() {
        val text = _uiState.value.input.trim()
        if (text.isEmpty()) return

        _uiState.update { it.copy(input = "") }
        chatService.sendMessage(text)
    }

    private fun collectSession(session: ChatSession) {
        viewModelScope.launch {
            session.messages.collect { messages -> _uiState.update { it.copy(messages = messages) } }
        }
        viewModelScope.launch {
            session.thinking.collect { thinking -> _uiState.update { it.copy(thinking = thinking) } }
        }
        viewModelScope.launch {
            session.error.collect { error -> _uiState.update { it.copy(error = error) } }
        }
    }
}
