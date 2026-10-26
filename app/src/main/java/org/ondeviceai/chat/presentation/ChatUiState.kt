package org.ondeviceai.chat.presentation

import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.models.domain.Model

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val thinking: Boolean = false,
    val error: String? = null,
    val models: List<Model> = emptyList(),
    val selectedModel: Model? = null,
)
