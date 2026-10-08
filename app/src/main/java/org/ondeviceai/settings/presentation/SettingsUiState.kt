package org.ondeviceai.settings.presentation

sealed interface SettingsUiState {
    data object ComingSoon : SettingsUiState
}
