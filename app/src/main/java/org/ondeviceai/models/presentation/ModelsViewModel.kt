package org.ondeviceai.models.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface ModelsIntent

class ModelsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ModelsUiState>(ModelsUiState.ComingSoon)
    val uiState: StateFlow<ModelsUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ModelsIntent) {}
}
