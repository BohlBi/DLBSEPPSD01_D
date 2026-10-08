package org.ondeviceai.models.presentation

sealed interface ModelsUiState {
    data object ComingSoon : ModelsUiState
}
