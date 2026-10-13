package org.ondeviceai.chat.application.tools

import android.content.ContextWrapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerToolTest {

    private val tool = TimerTool(ContextWrapper(null))

    @Test
    fun `sets the timer`() {
        assertEquals("Timer gestellt für 1200 Sekunden", tool.setTimer(1200))
    }

    @Test
    fun `non positive duration`() {
        assertTrue(tool.setTimer(0).startsWith("Fehler"))
    }

    @Test
    fun `duration longer than 24 hours`() {
        assertTrue(tool.setTimer(86_401).startsWith("Fehler"))
    }
}
