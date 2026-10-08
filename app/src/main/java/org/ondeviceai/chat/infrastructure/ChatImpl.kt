package org.ondeviceai.chat.infrastructure

import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession

class ChatImpl(private val session: ChatSession) : ChatService {

    override fun sendMessage(message: String) {
        session.addMessage(ChatMessage(message, fromAi = false))
        session.setThinking(true)
        session.addMessage(ChatMessage("hallo", fromAi = true))
        session.setThinking(false)
    }
}
