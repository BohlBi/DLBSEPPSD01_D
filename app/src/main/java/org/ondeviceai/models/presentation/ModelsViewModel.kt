package org.ondeviceai.models.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ondeviceai.models.domain.Model
import org.ondeviceai.models.domain.ModelStore
import org.ondeviceai.models.domain.ModelService

sealed interface ModelsIntent {
    data class Download(val model: Model) : ModelsIntent
}

class ModelsViewModel(
    store: ModelStore,
    private val modelService: ModelService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelsUiState())
    val uiState: StateFlow<ModelsUiState> = _uiState.asStateFlow()

    init {
        collectStore(store)
    }

    fun onIntent(intent: ModelsIntent) {
        when (intent) {
            is ModelsIntent.Download -> modelService.download(intent.model)
        }
    }

    private fun collectStore(store: ModelStore) {
        viewModelScope.launch {
            store.downloadedModels.collect { downloaded -> _uiState.update { it.copy(downloaded = downloaded) } }
        }
        viewModelScope.launch {
            store.downloadProgress.collect { progress -> _uiState.update { it.copy(progress = progress) } }
        }
        viewModelScope.launch {
            store.error.collect { error -> _uiState.update { it.copy(error = error) } }
        }
    }
}
