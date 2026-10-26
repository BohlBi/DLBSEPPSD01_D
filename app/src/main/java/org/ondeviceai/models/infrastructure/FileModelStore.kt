package org.ondeviceai.models.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.ondeviceai.models.domain.ModelStore
import org.ondeviceai.models.domain.MODEL_CATALOG
import org.ondeviceai.models.domain.Model
import java.io.File

/** [ModelStore], der heruntergeladene Modelle anhand vorhandener Dateien in [modelDir] erkennt. */
class FileModelStore(private val modelDir: File) : ModelStore {

    private val _downloadedModels = MutableStateFlow(MODEL_CATALOG.filter { File(modelDir, it.fileName).exists() })
    override val downloadedModels: StateFlow<List<Model>> = _downloadedModels.asStateFlow()

    private val _selectedModel = MutableStateFlow(_downloadedModels.value.firstOrNull())
    override val selectedModel: StateFlow<Model?> = _selectedModel.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    override val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    override fun markDownloaded(model: Model) {
        _downloadedModels.update { if (model in it) it else it + model }
        if (_selectedModel.value == null) _selectedModel.value = model
    }

    override fun select(model: Model) {
        _selectedModel.value = model
    }

    override fun setProgress(model: Model, progress: Float) {
        _downloadProgress.update { it + (model.fileName to progress) }
    }

    override fun removeProgress(model: Model) {
        _downloadProgress.update { it - model.fileName }
    }

    override fun setError(error: String?) {
        _error.value = error
    }

    override fun pathOf(model: Model): String = File(modelDir, model.fileName).path
}
