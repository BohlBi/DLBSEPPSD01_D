package org.ondeviceai.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ondeviceai.settings.domain.SettingsStore

sealed interface SettingsIntent {
    data class SetUseGpu(val useGpu: Boolean) : SettingsIntent
}

class SettingsViewModel(
    private val store: SettingsStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            store.useGpu.collect { useGpu -> _uiState.update { it.copy(useGpu = useGpu) } }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetUseGpu -> store.setUseGpu(intent.useGpu)
        }
    }
}
