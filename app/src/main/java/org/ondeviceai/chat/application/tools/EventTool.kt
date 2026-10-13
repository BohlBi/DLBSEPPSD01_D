package org.ondeviceai.chat.application.tools

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet
import java.time.ZoneId

private const val TAG = "EventTool"

class EventTool(private val context: Context) : ToolSet {

    @Tool(description = "Erstellt einen Kalendereintrag.")
    fun createEvent(
        @ToolParam(description = "Titel des Termins") title: String,
        @ToolParam(description = TIMESTAMP) timestamp: String,
    ): String {
        Log.i(TAG, "createEvent(title=$title, timestamp=$timestamp)")
        if (title.isBlank()) return "Fehler: Titel fehlt"
        val time = parseTimestamp(timestamp) ?: return invalidTimestamp(timestamp)
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "createEvent failed", e)
            return "Fehler: Kalender konnte nicht geöffnet werden (${e.message})"
        }
        return "Kalender mit Termin „$title“ für $time geöffnet, der Nutzer muss ihn noch speichern"
    }
}
