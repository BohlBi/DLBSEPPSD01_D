package org.ondeviceai.chat.infrastructure

import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet
import java.time.DateTimeException
import java.time.LocalDateTime

const val SYSTEM_PROMPT = "Du bist ein Assistent auf einem Android-Smartphone. " +
    "Wenn der Nutzer einen Wecker, einen Timer, eine Erinnerung, einen Kalendereintrag oder einen Anruf möchte, " +
    "rufe das passende Tool auf. Jede Nachricht beginnt mit dem aktuellen Zeitpunkt; " +
    "rechne Zeitangaben des Nutzers davon ausgehend in einen Zeitpunkt um. " +
    "Bestätige nach einem Tool-Aufruf das Ergebnis in einem kurzen Satz. " +
    "Sonst antworte kurz auf Deutsch."

private const val TIMESTAMP = "Zeitpunkt im Format yyyy-MM-ddTHH:mm"
private const val TAG = "ActionTools"

class ActionTools : ToolSet {

    @Tool(description = "Stellt einen Wecker.")
    fun setAlarm(@ToolParam(description = TIMESTAMP) timestamp: String): String {
        Log.i(TAG, "setAlarm(timestamp=$timestamp)")
        val time = parse(timestamp) ?: return invalidTimestamp(timestamp)
        return "Wecker für $time erkannt"
    }

    @Tool(description = "Startet einen Timer.")
    fun setTimer(@ToolParam(description = "Dauer in Sekunden") durationSeconds: Int): String {
        Log.i(TAG, "setTimer(durationSeconds=$durationSeconds)")
        if (durationSeconds <= 0) return "Fehler: Dauer muss größer als 0 sein"
        return "Timer für $durationSeconds Sekunden erkannt"
    }

    @Tool(description = "Legt eine Erinnerung an.")
    fun createReminder(
        @ToolParam(description = "Woran erinnert werden soll") text: String,
        @ToolParam(description = TIMESTAMP) timestamp: String,
    ): String {
        Log.i(TAG, "createReminder(text=$text, timestamp=$timestamp)")
        if (text.isBlank()) return "Fehler: Text fehlt"
        val time = parse(timestamp) ?: return invalidTimestamp(timestamp)
        return "Erinnerung „$text“ für $time erkannt"
    }

    @Tool(description = "Erstellt einen Kalendereintrag.")
    fun createEvent(
        @ToolParam(description = "Titel des Termins") title: String,
        @ToolParam(description = TIMESTAMP) timestamp: String,
    ): String {
        Log.i(TAG, "createEvent(title=$title, timestamp=$timestamp)")
        if (title.isBlank()) return "Fehler: Titel fehlt"
        val time = parse(timestamp) ?: return invalidTimestamp(timestamp)
        return "Termin „$title“ für $time erkannt"
    }

    @Tool(description = "Ruft einen Kontakt an.")
    fun callContact(@ToolParam(description = "Name des Kontakts") name: String): String {
        Log.i(TAG, "callContact(name=$name)")
        if (name.isBlank()) return "Fehler: Name fehlt"
        return "Anruf an $name erkannt"
    }

    private fun parse(timestamp: String): LocalDateTime? = try {
        LocalDateTime.parse(timestamp.trim())
    } catch (e: DateTimeException) {
        null
    }

    private fun invalidTimestamp(timestamp: String) = "Fehler: ungültiger Zeitpunkt „$timestamp“, erwartet $TIMESTAMP"
}
