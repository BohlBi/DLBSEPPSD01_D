package org.ondeviceai.chat.application.tools

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderToolTest {

    private val tool = ReminderTool()

    @Test
    fun reminder() {
        assertEquals("Erinnerung „Müll“ für 2026-10-14T07:00 erkannt", tool.createReminder("Müll", "2026-10-14T07:00"))
    }

    @Test
    fun `missing text`() {
        assertTrue(tool.createReminder(" ", "2026-10-14T07:00").startsWith("Fehler"))
    }

    @Test
    fun `invalid timestamp`() {
        assertTrue(tool.createReminder("Müll", "morgen").startsWith("Fehler"))
    }
}
