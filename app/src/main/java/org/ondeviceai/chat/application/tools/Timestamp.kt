package org.ondeviceai.chat.application.tools

import java.time.DateTimeException
import java.time.LocalDateTime

/** Beschreibung des Zeitpunkt-Formats, das das Modell in den Tool-Argumenten liefern soll. */
const val TIMESTAMP = "Zeitpunkt im Format yyyy-MM-ddTHH:mm"

/** Liest einen Zeitpunkt im Format `yyyy-MM-ddTHH:mm`; `null`, wenn das Format nicht passt. */
fun parseTimestamp(timestamp: String): LocalDateTime? = try {
    LocalDateTime.parse(timestamp.trim())
} catch (_: DateTimeException) {
    null
}

fun invalidTimestamp(timestamp: String) = "Fehler: ungültiger Zeitpunkt „$timestamp“, erwartet $TIMESTAMP"
