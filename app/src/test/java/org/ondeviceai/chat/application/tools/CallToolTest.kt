package org.ondeviceai.chat.application.tools

import android.content.ContextWrapper
import android.content.pm.PackageManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CallToolTest {

    private class PermissionContext(private val granted: Boolean) : ContextWrapper(null) {
        override fun checkSelfPermission(permission: String) =
            if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
    }

    @Test
    fun `missing name`() {
        assertTrue(CallTool(PermissionContext(true)).callContact("").startsWith("Fehler"))
    }

    @Test
    fun `contacts permission denied`() {
        assertEquals("Fehler: Zugriff auf Kontakte nicht erlaubt", CallTool(PermissionContext(false)).callContact("Mama"))
    }

    @Test
    fun `no contact found`() {
        assertEquals("Fehler: Kein Kontakt „Mama“ gefunden", CallTool(PermissionContext(true)).callContact("Mama"))
    }
}
