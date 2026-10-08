package org.ondeviceai.chat.infrastructure

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession

class LiteRtChatImpl(
    private val session: ChatSession,
    context: Context,
) : ChatService {

    // just one hardcoed model for now
    private val modelPath = "${context.filesDir.path}/$MODEL_FILE"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private var conversation: Conversation? = null

    override fun sendMessage(message: String) {
        session.addMessage(ChatMessage(message, fromAi = false))
        session.setError(null)
        session.setThinking(true)
        scope.launch {
            try {
                val reply = conversation().sendMessage(message).toString()
                session.addMessage(ChatMessage(reply, fromAi = true))
            } catch (e: Exception) {
                session.setError(e.message ?: e.javaClass.simpleName)
            } finally {
                session.setThinking(false)
            }
        }
    }

    private fun conversation(): Conversation =
        conversation ?: Engine(EngineConfig(modelPath = modelPath, backend = Backend.GPU()))
            .apply { initialize() }
            .createConversation()
            .also { conversation = it }

    private companion object {
        const val MODEL_FILE = "gemma-4-E2B-it.litertlm"
    }
}
