package org.ondeviceai.chat.presentation

import org.ondeviceai.chat.domain.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val thinking: Boolean = false,
)
