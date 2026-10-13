package org.ondeviceai.chat.application.tools

import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet

private const val TAG = "ReminderTool"

class ReminderTool : ToolSet {

    @Tool(description = "Legt eine Erinnerung an.")
    fun createReminder(
        @ToolParam(description = "Woran erinnert werden soll") text: String,
        @ToolParam(description = TIMESTAMP) timestamp: String,
    ): String {
        Log.i(TAG, "createReminder(text=$text, timestamp=$timestamp)")
        if (text.isBlank()) return "Fehler: Text fehlt"
        val time = parseTimestamp(timestamp) ?: return invalidTimestamp(timestamp)
        return "Erinnerung „$text“ für $time erkannt"
    }
}
