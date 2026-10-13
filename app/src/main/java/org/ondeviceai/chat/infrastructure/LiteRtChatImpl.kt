package org.ondeviceai.chat.infrastructure

import android.content.Context
import android.util.Log
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.ExperimentalApi
import com.google.ai.edge.litertlm.ExperimentalFlags
import com.google.ai.edge.litertlm.ThinkingConfig
import com.google.ai.edge.litertlm.tool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.chat.application.SYSTEM_PROMPT
import org.ondeviceai.chat.application.tools.AlarmTool
import org.ondeviceai.chat.application.tools.CallTool
import org.ondeviceai.chat.application.tools.EventTool
import org.ondeviceai.chat.application.tools.ReminderTool
import org.ondeviceai.chat.application.tools.TimerTool
import org.ondeviceai.models.domain.Model
import org.ondeviceai.models.domain.ModelStore
import org.ondeviceai.settings.domain.SettingsStore
import java.io.File
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "LiteRtChatImpl"

class LiteRtChatImpl(
    private val session: ChatSession,
    private val modelStore: ModelStore,
    private val settingsStore: SettingsStore,
    private val cacheDir: File,
    private val context: Context,
) : ChatService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private var engine: Engine? = null
    @Volatile private var conversation: Conversation? = null
    @Volatile private var cancelled = false
    private var loadedModel: Model? = null
    private var loadedWithGpu: Boolean? = null
    private val currentRequest = AtomicInteger()
    private val now = DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd'T'HH:mm", Locale.GERMAN)

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
                stream(conversation, "Jetzt: ${LocalDateTime.now().format(now)}\n$message", request)
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

    private suspend fun stream(conversation: Conversation, message: String, request: Int) {
        var text = ""
        conversation.sendMessageAsync(message).collect { chunk ->
            Log.d(TAG, "chunk text=[$chunk] toolCalls=${chunk.toolCalls} channels=${chunk.channels}")
            if (request != currentRequest.get()) return@collect
            val delta = chunk.toString()
            if (delta.isEmpty()) return@collect
            if (text.isEmpty()) {
                session.addMessage(ChatMessage(delta, fromAi = true))
            } else {
                session.updateLastMessage(ChatMessage(text + delta, fromAi = true))
            }
            text += delta
        }
    }

    private fun conversation(model: Model): Conversation {
        val useGpu = settingsStore.useGpu.value
        if (model != loadedModel || useGpu != loadedWithGpu) loadEngine(model, useGpu)

        val current = conversation
        if (current != null && current.isAlive && !cancelled) return current

        current?.takeIf { it.isAlive }?.close()
        cancelled = false
        val engine = checkNotNull(engine)
        val config = ConversationConfig(
            systemInstruction = Contents.of(SYSTEM_PROMPT),
            tools = listOf(
                tool(AlarmTool(context, Clock.systemDefaultZone())),
                tool(TimerTool()),
                tool(ReminderTool()),
                tool(EventTool()),
                tool(CallTool()),
            ),
                        thinkingConfig = ThinkingConfig(enableThinking = false),
        )
        return engine.createConversation(config)
            .also { conversation = it }
    }

    @OptIn(ExperimentalApi::class)
    private fun loadEngine(model: Model, useGpu: Boolean) {
        conversation?.takeIf { it.isAlive }?.close()
        engine?.close()
        conversation = null
        engine = null
        loadedModel = null
        loadedWithGpu = null

        val backend = if (useGpu) Backend.GPU() else Backend.CPU()
        ExperimentalFlags.enableSpeculativeDecoding = useGpu
        val newEngine = Engine(EngineConfig(modelPath = modelStore.pathOf(model), backend = backend, cacheDir = cacheDir.path))
        newEngine.initialize()
        engine = newEngine
        loadedModel = model
        loadedWithGpu = useGpu
    }
}
