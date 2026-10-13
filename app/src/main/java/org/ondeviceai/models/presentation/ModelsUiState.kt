package org.ondeviceai.models.presentation

import org.ondeviceai.models.domain.Model
import org.ondeviceai.models.domain.MODEL_CATALOG

data class ModelsUiState(
    val models: List<Model> = MODEL_CATALOG,
    val downloaded: List<Model> = emptyList(),
    val progress: Map<String, Float> = emptyMap(),
    val error: String? = null,
)
