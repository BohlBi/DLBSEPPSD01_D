package org.ondeviceai.chat.infrastructure

import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.ThinkingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.models.api.Model
import org.ondeviceai.models.api.ModelStore
import java.util.concurrent.atomic.AtomicInteger

class LiteRtChatImpl(
    private val session: ChatSession,
    private val modelStore: ModelStore,
) : ChatService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private var engine: Engine? = null
    @Volatile private var conversation: Conversation? = null
    private var loadedModel: Model? = null
    private val currentRequest = AtomicInteger()

    override fun sendMessage(message: String) {
        session.addMessage(ChatMessage(message, fromAi = false))
        session.setError(null)
        session.setThinking(true)
        val request = currentRequest.incrementAndGet()
        scope.launch {
            try {
                val model = modelStore.selectedModel.value ?: error("Kein Modell heruntergeladen")
                val conversation = conversation(model)
                if (request != currentRequest.get()) return@launch
                val reply = conversation.sendMessage(message).toString()
                if (request == currentRequest.get()) session.addMessage(ChatMessage(reply, fromAi = true))
            } catch (e: Exception) {
                if (request == currentRequest.get()) session.setError(e.message ?: e.javaClass.simpleName)
            } finally {
                if (request == currentRequest.get()) session.setThinking(false)
            }
        }
    }

    override fun stop() {
        currentRequest.incrementAndGet()
        try {
            conversation?.takeIf { it.isAlive }?.cancelProcess()
        } catch (e: Exception) {
            session.setError(e.message ?: e.javaClass.simpleName)
        }
        session.setThinking(false)
    }

    private fun conversation(model: Model): Conversation {
        val current = conversation
        if (current != null && current.isAlive && model == loadedModel) return current

        conversation?.takeIf { it.isAlive }?.close()
        engine?.close()
        conversation = null
        engine = null
        loadedModel = null

        val newEngine = Engine(EngineConfig(modelPath = modelStore.pathOf(model), backend = Backend.GPU()))
        engine = newEngine
        newEngine.initialize()
        return newEngine.createConversation(ConversationConfig(thinkingConfig = ThinkingConfig(enableThinking = false))).also {
            conversation = it
            loadedModel = model
        }
    }
}
