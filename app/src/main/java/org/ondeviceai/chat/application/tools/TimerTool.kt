package org.ondeviceai.chat.application.tools

import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet

private const val TAG = "TimerTool"

class TimerTool : ToolSet {

    @Tool(description = "Startet einen Timer.")
    fun setTimer(@ToolParam(description = "Dauer in Sekunden") durationSeconds: Int): String {
        Log.i(TAG, "setTimer(durationSeconds=$durationSeconds)")
        if (durationSeconds <= 0) return "Fehler: Dauer muss größer als 0 sein"
        return "Timer für $durationSeconds Sekunden erkannt"
    }
}
