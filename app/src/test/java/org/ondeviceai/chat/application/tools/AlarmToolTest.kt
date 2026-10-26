package org.ondeviceai.chat.application.tools

import android.content.ContextWrapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmToolTest {

    private val zone = ZoneId.of("Europe/Berlin")
    private val clock = Clock.fixed(LocalDateTime.of(2026, 10, 13, 18, 0).atZone(zone).toInstant(), zone)
    private val tool = AlarmTool(ContextWrapper(null), clock)

    @Test
    fun `sets the alarm`() {
        assertEquals("Wecker gestellt für 2026-10-14T07:30", tool.setAlarm("2026-10-14T07:30"))
    }

    @Test
    fun `invalid timestamp`() {
        assertTrue(tool.setAlarm("morgen 7 Uhr").startsWith("Fehler"))
    }

    @Test
    fun `timestamp in the past`() {
        assertTrue(tool.setAlarm("2026-10-13T17:00").startsWith("Fehler"))
    }

    @Test
    fun `timestamp more than 24 hours ahead`() {
        assertTrue(tool.setAlarm("2026-10-14T18:01").startsWith("Fehler"))
    }
}
