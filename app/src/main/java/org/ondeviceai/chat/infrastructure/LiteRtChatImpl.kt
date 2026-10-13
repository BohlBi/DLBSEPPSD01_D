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
import org.ondeviceai.settings.domain.SettingsStore
import java.util.concurrent.atomic.AtomicInteger

class LiteRtChatImpl(
    private val session: ChatSession,
    private val modelStore: ModelStore,
    private val settingsStore: SettingsStore,
) : ChatService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private var engine: Engine? = null
    @Volatile private var conversation: Conversation? = null
    @Volatile private var cancelled = false
    private var loadedModel: Model? = null
    private var loadedWithGpu: Boolean? = null
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
        cancelled = true
        try {
            conversation?.takeIf { it.isAlive }?.cancelProcess()
        } catch (e: Exception) {
            session.setError(e.message ?: e.javaClass.simpleName)
        }
        session.setThinking(false)
    }

    private fun conversation(model: Model): Conversation {
        val useGpu = settingsStore.useGpu.value
        if (model != loadedModel || useGpu != loadedWithGpu) loadEngine(model, useGpu)

        val current = conversation
        if (current != null && current.isAlive && !cancelled) return current

        current?.takeIf { it.isAlive }?.close()
        cancelled = false
        val engine = checkNotNull(engine)
        return engine.createConversation(ConversationConfig(thinkingConfig = ThinkingConfig(enableThinking = false)))
            .also { conversation = it }
    }

    private fun loadEngine(model: Model, useGpu: Boolean) {
        conversation?.takeIf { it.isAlive }?.close()
        engine?.close()
        conversation = null
        engine = null
        loadedModel = null
        loadedWithGpu = null

        val backend = if (useGpu) Backend.GPU() else Backend.CPU()
        val newEngine = Engine(EngineConfig(modelPath = modelStore.pathOf(model), backend = backend))
        newEngine.initialize()
        engine = newEngine
        loadedModel = model
        loadedWithGpu = useGpu
    }
}
