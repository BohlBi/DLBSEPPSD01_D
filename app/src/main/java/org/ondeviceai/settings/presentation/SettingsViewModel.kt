package org.ondeviceai.settings.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.ComingSoon)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onIntent(intent: SettingsIntent) {}
}
