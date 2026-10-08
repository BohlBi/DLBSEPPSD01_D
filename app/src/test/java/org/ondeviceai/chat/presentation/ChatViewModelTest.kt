package org.ondeviceai.chat.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class ChatViewModelTest {

    @Test
    fun `initial state is ComingSoon`() {
        val viewModel = ChatViewModel()

        assertEquals(ChatUiState.ComingSoon, viewModel.uiState.value)
    }
}
