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
import org.junit.Test
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val session = ChatSession()
    private val sentMessages = mutableListOf<String>()
    private val chatService = object : ChatService {
        override fun sendMessage(message: String) {
            sentMessages += message
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
        val viewModel = ChatViewModel(session, chatService)

        assertEquals(ChatUiState(), viewModel.uiState.value)
    }

    @Test
    fun `InputChanged updates the input`() {
        val viewModel = ChatViewModel(session, chatService)

        viewModel.onIntent(ChatIntent.InputChanged("Hi"))

        assertEquals("Hi", viewModel.uiState.value.input)
    }

    @Test
    fun `SendMessage passes the trimmed input to the service and clears the input`() {
        val viewModel = ChatViewModel(session, chatService)

        viewModel.onIntent(ChatIntent.InputChanged(" Hi "))
        viewModel.onIntent(ChatIntent.SendMessage)

        assertEquals(listOf("Hi"), sentMessages)
        assertEquals("", viewModel.uiState.value.input)
    }

    @Test
    fun `SendMessage with blank input does nothing`() {
        val viewModel = ChatViewModel(session, chatService)

        viewModel.onIntent(ChatIntent.InputChanged("   "))
        viewModel.onIntent(ChatIntent.SendMessage)

        assertTrue(sentMessages.isEmpty())
        assertEquals("   ", viewModel.uiState.value.input)
    }

    @Test
    fun `messages from the session are shown`() {
        val viewModel = ChatViewModel(session, chatService)
        val message = ChatMessage("hallo", fromAi = true)

        session.addMessage(message)

        assertEquals(listOf(message), viewModel.uiState.value.messages)
    }

    @Test
    fun `thinking from the session is shown`() {
        val viewModel = ChatViewModel(session, chatService)

        session.setThinking(true)

        assertTrue(viewModel.uiState.value.thinking)
    }
}
