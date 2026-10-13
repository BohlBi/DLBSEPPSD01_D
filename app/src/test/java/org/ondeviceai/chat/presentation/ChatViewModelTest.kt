package org.ondeviceai.chat.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.models.api.ModelStore
import org.ondeviceai.models.domain.MODEL_CATALOG

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val modelDir = TemporaryFolder()

    private val session = ChatSession()
    private val modelStore by lazy { ModelStore(modelDir.root) }
    private val sentMessages = mutableListOf<String>()
    private var stopCount = 0
    private val chatService = object : ChatService {
        override fun sendMessage(message: String) {
            sentMessages += message
        }

        override fun stop() {
            stopCount++
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        assertEquals(ChatUiState(), viewModel.uiState.value)
    }

    @Test
    fun `InputChanged updates the input`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        viewModel.onIntent(ChatIntent.InputChanged("Hi"))

        assertEquals("Hi", viewModel.uiState.value.input)
    }

    @Test
    fun `SendMessage passes the trimmed input to the service and clears the input`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        viewModel.onIntent(ChatIntent.InputChanged(" Hi "))
        viewModel.onIntent(ChatIntent.SendMessage)

        assertEquals(listOf("Hi"), sentMessages)
        assertEquals("", viewModel.uiState.value.input)
    }

    @Test
    fun `SendMessage with blank input does nothing`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        viewModel.onIntent(ChatIntent.InputChanged("   "))
        viewModel.onIntent(ChatIntent.SendMessage)

        assertTrue(sentMessages.isEmpty())
        assertEquals("   ", viewModel.uiState.value.input)
    }

    @Test
    fun `messages from the session are shown`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)
        val message = ChatMessage("hallo", fromAi = true)

        session.addMessage(message)

        assertEquals(listOf(message), viewModel.uiState.value.messages)
    }

    @Test
    fun `thinking from the session is shown`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        session.setThinking(true)

        assertTrue(viewModel.uiState.value.thinking)
    }

    @Test
    fun `error from the session is shown`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        session.setError("Modell nicht gefunden")

        assertEquals("Modell nicht gefunden", viewModel.uiState.value.error)
    }

    @Test
    fun `downloaded models and selection from the store are shown`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)
        val model = MODEL_CATALOG.first()

        modelStore.markDownloaded(model)

        assertEquals(listOf(model), viewModel.uiState.value.models)
        assertEquals(model, viewModel.uiState.value.selectedModel)
    }

    @Test
    fun `SelectModel selects the model in the store`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)
        val (first, second) = MODEL_CATALOG
        modelStore.markDownloaded(first)
        modelStore.markDownloaded(second)

        viewModel.onIntent(ChatIntent.SelectModel(second))

        assertEquals(second, modelStore.selectedModel.value)
        assertEquals(second, viewModel.uiState.value.selectedModel)
    }

    @Test
    fun `Stop stops the service`() {
        val viewModel = ChatViewModel(session, chatService, modelStore)

        viewModel.onIntent(ChatIntent.Stop)

        assertEquals(1, stopCount)
    }
}
