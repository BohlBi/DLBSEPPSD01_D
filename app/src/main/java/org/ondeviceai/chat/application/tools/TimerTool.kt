package org.ondeviceai.chat.application.tools

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet

private const val TAG = "TimerTool"
private const val MAX_SECONDS = 86_400

class TimerTool(private val context: Context) : ToolSet {

    @Tool(description = "Startet einen Timer.")
    fun setTimer(@ToolParam(description = "Dauer in Sekunden") durationSeconds: Int): String {
        Log.i(TAG, "setTimer(durationSeconds=$durationSeconds)")
        if (durationSeconds <= 0) return "Fehler: Dauer muss größer als 0 sein"
        if (durationSeconds > MAX_SECONDS) return "Fehler: Ein Timer kann höchstens 24 Stunden lang sein"
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, durationSeconds)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "setTimer failed", e)
            return "Fehler: Timer konnte nicht gestellt werden (${e.message})"
        }
        return "Timer gestellt für $durationSeconds Sekunden"
    }
}
