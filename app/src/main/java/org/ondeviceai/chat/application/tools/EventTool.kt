package org.ondeviceai.chat.application.tools

import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet

private const val TAG = "EventTool"

class EventTool : ToolSet {

    @Tool(description = "Erstellt einen Kalendereintrag.")
    fun createEvent(
        @ToolParam(description = "Titel des Termins") title: String,
        @ToolParam(description = TIMESTAMP) timestamp: String,
    ): String {
        Log.i(TAG, "createEvent(title=$title, timestamp=$timestamp)")
        if (title.isBlank()) return "Fehler: Titel fehlt"
        val time = parseTimestamp(timestamp) ?: return invalidTimestamp(timestamp)
        return "Termin „$title“ für $time erkannt"
    }
}
