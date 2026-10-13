package org.ondeviceai.chat.application.tools

import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet

private const val TAG = "CallTool"

class CallTool : ToolSet {

    @Tool(description = "Ruft einen Kontakt an.")
    fun callContact(@ToolParam(description = "Name des Kontakts") name: String): String {
        Log.i(TAG, "callContact(name=$name)")
        if (name.isBlank()) return "Fehler: Name fehlt"
        return "Anruf an $name erkannt"
    }
}
