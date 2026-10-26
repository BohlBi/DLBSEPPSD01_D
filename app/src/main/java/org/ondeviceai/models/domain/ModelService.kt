package org.ondeviceai.models.domain


/** Lädt Modelle herunter; kehrt sofort zurück, Fortschritt und Ergebnis landen im [ModelStore]. */
interface ModelService {
    fun download(model: Model)
}
