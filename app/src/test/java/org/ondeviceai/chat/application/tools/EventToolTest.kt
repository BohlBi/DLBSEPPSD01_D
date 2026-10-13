package org.ondeviceai.chat.application.tools

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventToolTest {

    private val tool = EventTool()

    @Test
    fun event() {
        assertEquals("Termin „Zahnarzt“ für 2026-10-14T07:00 erkannt", tool.createEvent("Zahnarzt", "2026-10-14T07:00"))
    }

    @Test
    fun `missing title`() {
        assertTrue(tool.createEvent("", "2026-10-14T07:00").startsWith("Fehler"))
    }

    @Test
    fun `invalid timestamp`() {
        assertTrue(tool.createEvent("Zahnarzt", "Freitag").startsWith("Fehler"))
    }
}
