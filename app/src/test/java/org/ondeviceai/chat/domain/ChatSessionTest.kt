package org.ondeviceai.chat.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatSessionTest {

    private val session = ChatSession()

    @Test
    fun `starts empty`() {
        assertEquals(emptyList<ChatMessage>(), session.messages.value)
        assertFalse(session.thinking.value)
        assertNull(session.error.value)
    }

    @Test
    fun `addMessage appends messages in order`() {
        session.addMessage(ChatMessage("Hallo", fromAi = false))
        session.addMessage(ChatMessage("Hi", fromAi = true))

        assertEquals(
            listOf(ChatMessage("Hallo", fromAi = false), ChatMessage("Hi", fromAi = true)),
            session.messages.value,
        )
    }

    @Test
    fun `updateLastMessage replaces only the last message`() {
        session.addMessage(ChatMessage("Hallo", fromAi = false))
        session.addMessage(ChatMessage("Ich", fromAi = true))

        session.updateLastMessage(ChatMessage("Ich habe", fromAi = true))

        assertEquals(
            listOf(ChatMessage("Hallo", fromAi = false), ChatMessage("Ich habe", fromAi = true)),
            session.messages.value,
        )
    }

    @Test
    fun `thinking and error are set`() {
        session.setThinking(true)
        session.setError("Fehler")

        assertTrue(session.thinking.value)
        assertEquals("Fehler", session.error.value)
    }
}
