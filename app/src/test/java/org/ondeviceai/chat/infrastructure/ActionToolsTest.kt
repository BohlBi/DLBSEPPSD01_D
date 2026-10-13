package org.ondeviceai.chat.infrastructure

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActionToolsTest {

    private val tools = ActionTools()

    @Test
    fun alarm() {
        assertEquals("Wecker für 2026-10-14T07:00 erkannt", tools.setAlarm("2026-10-14T07:00"))
    }

    @Test
    fun `alarm with invalid timestamp`() {
        assertTrue(tools.setAlarm("morgen 7 Uhr").startsWith("Fehler"))
    }

    @Test
    fun timer() {
        assertEquals("Timer für 1200 Sekunden erkannt", tools.setTimer(1200))
    }

    @Test
    fun `timer with non positive duration`() {
        assertTrue(tools.setTimer(0).startsWith("Fehler"))
    }

    @Test
    fun reminder() {
        assertEquals("Erinnerung „Müll“ für 2026-10-14T07:00 erkannt", tools.createReminder("Müll", "2026-10-14T07:00"))
    }

    @Test
    fun `reminder without text`() {
        assertTrue(tools.createReminder(" ", "2026-10-14T07:00").startsWith("Fehler"))
    }

    @Test
    fun event() {
        assertEquals("Termin „Zahnarzt“ für 2026-10-14T07:00 erkannt", tools.createEvent("Zahnarzt", "2026-10-14T07:00"))
    }

    @Test
    fun `event with invalid timestamp`() {
        assertTrue(tools.createEvent("Zahnarzt", "Freitag").startsWith("Fehler"))
    }

    @Test
    fun call() {
        assertEquals("Anruf an Mama erkannt", tools.callContact("Mama"))
    }

    @Test
    fun `call without name`() {
        assertTrue(tools.callContact("").startsWith("Fehler"))
    }
}
