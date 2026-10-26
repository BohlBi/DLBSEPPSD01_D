package org.ondeviceai.chat.application.tools

import android.content.ContextWrapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventToolTest {

    private val tool = EventTool(ContextWrapper(null))

    @Test
    fun `opens the calendar`() {
        assertEquals(
            "Kalender mit Termin „Zahnarzt“ für 2026-10-16T14:30 geöffnet, der Nutzer muss ihn noch speichern",
            tool.createEvent("Zahnarzt", "2026-10-16T14:30"),
        )
    }

    @Test
    fun `missing title`() {
        assertTrue(tool.createEvent("", "2026-10-16T14:30").startsWith("Fehler"))
    }

    @Test
    fun `invalid timestamp`() {
        assertTrue(tool.createEvent("Zahnarzt", "Freitag").startsWith("Fehler"))
    }
}
