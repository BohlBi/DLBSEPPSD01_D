package org.ondeviceai.chat.application.tools

import java.time.DateTimeException
import java.time.LocalDateTime

const val TIMESTAMP = "Zeitpunkt im Format yyyy-MM-ddTHH:mm"

fun parseTimestamp(timestamp: String): LocalDateTime? = try {
    LocalDateTime.parse(timestamp.trim())
} catch (_: DateTimeException) {
    null
}

fun invalidTimestamp(timestamp: String) = "Fehler: ungültiger Zeitpunkt „$timestamp“, erwartet $TIMESTAMP"
