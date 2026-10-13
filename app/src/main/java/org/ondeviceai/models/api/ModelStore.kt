package org.ondeviceai.models.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.ondeviceai.models.domain.MODEL_CATALOG
import java.io.File

class ModelStore(private val modelDir: File) {

    private val _downloadedModels = MutableStateFlow(MODEL_CATALOG.filter { File(modelDir, it.fileName).exists() })
    val downloadedModels: StateFlow<List<Model>> = _downloadedModels.asStateFlow()

    private val _selectedModel = MutableStateFlow(_downloadedModels.value.firstOrNull())
    val selectedModel: StateFlow<Model?> = _selectedModel.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun markDownloaded(model: Model) {
        _downloadedModels.update { if (model in it) it else it + model }
        if (_selectedModel.value == null) _selectedModel.value = model
    }

    fun select(model: Model) {
        _selectedModel.value = model
    }

    fun setProgress(model: Model, progress: Float) {
        _downloadProgress.update { it + (model.fileName to progress) }
    }

    fun removeProgress(model: Model) {
        _downloadProgress.update { it - model.fileName }
    }

    fun setError(error: String?) {
        _error.value = error
    }

    fun pathOf(model: Model): String = File(modelDir, model.fileName).path
}
