package org.ondeviceai.models.domain

import kotlinx.coroutines.flow.StateFlow

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
    fun pathOf(model: Model): String
}
