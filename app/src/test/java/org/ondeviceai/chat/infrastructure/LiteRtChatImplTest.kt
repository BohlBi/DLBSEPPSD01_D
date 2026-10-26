package org.ondeviceai.chat.infrastructure

import android.content.ContextWrapper
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.Message
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.models.domain.MODEL_CATALOG
import org.ondeviceai.models.infrastructure.FileModelStore
import org.ondeviceai.settings.infrastructure.InMemorySettingsStore
import kotlin.time.Duration.Companion.milliseconds

class LiteRtChatImplTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val session = ChatSession()
    private val settingsStore = InMemorySettingsStore()
    private val conversation = mockk<Conversation>(relaxed = true)
    private val sentMessage = slot<String>()
    private lateinit var modelStore: FileModelStore

    private fun chunk(text: String) = mockk<Message>(relaxed = true) {
        every { this@mockk.toString() } returns text
    }

    @Before
    fun setUp() {
        val modelDir = folder.newFolder("models")
        MODEL_CATALOG.forEach { modelDir.resolve(it.fileName).createNewFile() }
        modelStore = FileModelStore(modelDir)

        mockkConstructor(Engine::class)
        every { anyConstructed<Engine>().initialize() } just runs
        every { anyConstructed<Engine>().close() } just runs
        every { anyConstructed<Engine>().createConversation(any()) } returns conversation
        every { conversation.isAlive } returns true
        every { conversation.sendMessageAsync(capture(sentMessage)) } returns flowOf(chunk("Hallo"), chunk(" Welt"))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun chatService() = LiteRtChatImpl(session, modelStore, settingsStore, folder.newFolder(), ContextWrapper(null))

    private fun LiteRtChatImpl.send(message: String) {
        sendMessage(message)
        runBlocking { withTimeout(5_000.milliseconds) { session.thinking.first { !it } } }
    }

    @Test
    fun `streams the answer into the session`() {
        chatService().send("Hi")

        assertEquals(
            listOf(ChatMessage("Hi", fromAi = false), ChatMessage("Hallo Welt", fromAi = true)),
            session.messages.value,
        )
        assertNull(session.error.value)
    }

    @Test
    fun `prefixes the message with the current time`() {
        chatService().send("Hi")

        assertTrue(sentMessage.captured.startsWith("Jetzt: "))
        assertTrue(sentMessage.captured.endsWith("\nHi"))
    }

    @Test
    fun `loads the engine only once for several messages`() {
        val chatService = chatService()

        chatService.send("Eins")
        chatService.send("Zwei")

        verify(exactly = 1) { anyConstructed<Engine>().initialize() }
        verify(exactly = 1) { anyConstructed<Engine>().createConversation(any()) }
    }

    @Test
    fun `reloads the engine after a model change`() {
        val chatService = chatService()
        chatService.send("Eins")

        modelStore.select(MODEL_CATALOG[1])
        chatService.send("Zwei")

        verify(exactly = 2) { anyConstructed<Engine>().initialize() }
        verify(exactly = 1) { anyConstructed<Engine>().close() }
    }

    @Test
    fun `reloads the engine after switching between GPU and CPU`() {
        val chatService = chatService()
        chatService.send("Eins")

        settingsStore.setUseGpu(false)
        chatService.send("Zwei")

        verify(exactly = 2) { anyConstructed<Engine>().initialize() }
    }

    @Test
    fun `error while answering is shown`() {
        every { conversation.sendMessageAsync(any<String>()) } returns flow { error("Modellfehler") }

        chatService().send("Hi")

        assertEquals("Modellfehler", session.error.value)
    }

    @Test
    fun `without downloaded model an error is set`() {
        modelStore = FileModelStore(folder.newFolder("empty"))

        chatService().send("Hi")

        assertEquals(listOf(ChatMessage("Hi", fromAi = false)), session.messages.value)
        assertEquals("Kein Modell heruntergeladen", session.error.value)
    }

    @Test
    fun `stop cancels and the next message uses a new conversation`() {
        val chatService = chatService()
        chatService.send("Eins")

        chatService.stop()
        chatService.send("Zwei")

        verify { conversation.cancelProcess() }
        verify(exactly = 2) { anyConstructed<Engine>().createConversation(any()) }
        verify(exactly = 1) { anyConstructed<Engine>().initialize() }
    }
}
