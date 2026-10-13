package org.ondeviceai.settings.application

import kotlinx.coroutines.flow.StateFlow

interface SettingsStore {
    val useGpu: StateFlow<Boolean>

    fun setUseGpu(useGpu: Boolean)
}
