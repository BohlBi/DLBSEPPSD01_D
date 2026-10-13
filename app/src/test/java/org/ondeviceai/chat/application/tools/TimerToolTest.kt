package org.ondeviceai.chat.application.tools

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerToolTest {

    private val tool = TimerTool()

    @Test
    fun timer() {
        assertEquals("Timer für 1200 Sekunden erkannt", tool.setTimer(1200))
    }

    @Test
    fun `non positive duration`() {
        assertTrue(tool.setTimer(0).startsWith("Fehler"))
    }
}
