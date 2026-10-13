package org.ondeviceai.chat.application.tools

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet
import java.time.Clock
import java.time.LocalDateTime

private const val TAG = "AlarmTool"

class AlarmTool(private val context: Context, private val clock: Clock) : ToolSet {

    @Tool(description = "Stellt einen Wecker.")
    fun setAlarm(@ToolParam(description = TIMESTAMP) timestamp: String): String {
        Log.i(TAG, "setAlarm(timestamp=$timestamp)")
        val time = parseTimestamp(timestamp) ?: return invalidTimestamp(timestamp)
        val now = LocalDateTime.now(clock)
        if (!time.isAfter(now)) return "Fehler: Zeitpunkt $time liegt in der Vergangenheit"
        if (time.isAfter(now.plusHours(24))) return "Fehler: Ein Wecker kann nur für die nächsten 24 Stunden gestellt werden"
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, time.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, time.minute)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "setAlarm failed", e)
            return "Fehler: Wecker konnte nicht gestellt werden (${e.message})"
        }
        return "Wecker gestellt für $time"
    }
}
