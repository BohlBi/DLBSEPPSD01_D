package org.ondeviceai.chat.infrastructure

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
import org.ondeviceai.models.api.Model
import org.ondeviceai.models.api.ModelStore

class LiteRtChatImpl(
    private val session: ChatSession,
    private val modelStore: ModelStore,
) : ChatService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private var engine: Engine? = null
    private var conversation: Conversation? = null
    private var loadedModel: Model? = null

    override fun sendMessage(message: String) {
        session.addMessage(ChatMessage(message, fromAi = false))
        session.setError(null)
        session.setThinking(true)
        scope.launch {
            try {
                val model = modelStore.selectedModel.value ?: error("Kein Modell heruntergeladen")
                val reply = conversation(model).sendMessage(message).toString()
                session.addMessage(ChatMessage(reply, fromAi = true))
            } catch (e: Exception) {
                session.setError(e.message ?: e.javaClass.simpleName)
            } finally {
                session.setThinking(false)
            }
        }
    }

    private fun conversation(model: Model): Conversation {
        val current = conversation
        if (current != null && model == loadedModel) return current

        conversation?.close()
        engine?.close()
        conversation = null
        engine = null
        loadedModel = null

        val newEngine = Engine(EngineConfig(modelPath = modelStore.pathOf(model), backend = Backend.GPU()))
        engine = newEngine
        newEngine.initialize()
        return newEngine.createConversation().also {
            conversation = it
            loadedModel = model
        }
    }
}
