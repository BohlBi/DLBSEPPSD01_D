package org.ondeviceai.settings.domain

import kotlinx.coroutines.flow.StateFlow

interface SettingsStore {
    val useGpu: StateFlow<Boolean>

    fun setUseGpu(useGpu: Boolean)
}
