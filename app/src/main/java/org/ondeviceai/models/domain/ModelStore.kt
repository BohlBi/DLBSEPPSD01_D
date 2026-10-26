package org.ondeviceai.models.domain

import kotlinx.coroutines.flow.StateFlow

/** Gemeinsamer Zustand der Modelle (heruntergeladen, ausgewählt, Download-Fortschritt), genutzt von Modelle-Screen und Chat. */
interface ModelStore {
    val downloadedModels: StateFlow<List<Model>>
    val selectedModel: StateFlow<Model?>
    val downloadProgress: StateFlow<Map<String, Float>>
    val error: StateFlow<String?>
    fun markDownloaded(model: Model)
    fun select(model: Model)
    fun setProgress(model: Model, progress: Float)
    fun removeProgress(model: Model)
    fun setError(error: String?)
    /** Pfad der Modelldatei auf dem Gerät. */
    fun pathOf(model: Model): String
}
