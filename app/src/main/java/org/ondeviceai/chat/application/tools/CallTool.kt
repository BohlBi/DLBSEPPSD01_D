package org.ondeviceai.chat.application.tools

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import com.google.ai.edge.litertlm.Tool
import com.google.ai.edge.litertlm.ToolParam
import com.google.ai.edge.litertlm.ToolSet

private const val TAG = "CallTool"

/**
 * Tool `call_contact`: sucht den Namen in den Kontakten (`READ_CONTACTS`) und öffnet bei genau einem
 * Treffer die Telefon-App mit der Nummer. Bei mehreren Treffern erhält das Modell die Namen,
 * um nachzufragen.
 */
class CallTool(private val context: Context) : ToolSet {

    @Tool(description = "Ruft einen Kontakt an.")
    fun callContact(@ToolParam(description = "Name des Kontakts") name: String): String {
        Log.i(TAG, "callContact(name=$name)")
        if (name.isBlank()) return "Fehler: Name fehlt"
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return "Fehler: Zugriff auf Kontakte nicht erlaubt"
        }
        val contacts = try {
            findContacts(name.trim())
        } catch (e: Exception) {
            Log.e(TAG, "findContacts failed", e)
            return "Fehler: Kontakte konnten nicht gelesen werden (${e.message})"
        }
        val names = contacts.map { it.first }.distinct()
        if (names.isEmpty()) return "Fehler: Kein Kontakt „$name“ gefunden"
        if (names.size > 1) return "Fehler: Mehrere Kontakte gefunden: ${names.joinToString()}. Frage nach, wer gemeint ist."
        val (contact, number) = contacts.first()
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "callContact failed", e)
            return "Fehler: Telefon-App konnte nicht geöffnet werden (${e.message})"
        }
        return "Telefon-App mit der Nummer von $contact geöffnet, der Nutzer muss den Anruf noch starten"
    }

    private fun findContacts(name: String): List<Pair<String, String>> {
        val cursor = context.contentResolver?.query(
            Phone.CONTENT_URI,
            arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER),
            "${Phone.DISPLAY_NAME} LIKE ?",
            arrayOf("%$name%"),
            null,
        ) ?: return emptyList()
        return cursor.use {
            buildList { while (it.moveToNext()) add(it.getString(0) to it.getString(1)) }
        }
    }
}
