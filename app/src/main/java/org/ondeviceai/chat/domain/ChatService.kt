package org.ondeviceai.chat.domain

/**
 * Schnittstelle zum Sprachmodell. Aufrufe kehren sofort zurück; Antwort, Ladezustand und Fehler
 * landen in der [ChatSession].
 */
interface ChatService {
    fun sendMessage(message: String)
    /** Bricht die laufende Antwort ab; ihr Ergebnis wird verworfen. */
    fun stop()
}
