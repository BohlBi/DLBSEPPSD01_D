package org.ondeviceai.chat.application.tools

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CallToolTest {

    private val tool = CallTool()

    @Test
    fun call() {
        assertEquals("Anruf an Mama erkannt", tool.callContact("Mama"))
    }

    @Test
    fun `missing name`() {
        assertTrue(tool.callContact("").startsWith("Fehler"))
    }
}
