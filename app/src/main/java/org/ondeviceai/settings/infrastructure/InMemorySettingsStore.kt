package org.ondeviceai.settings.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ondeviceai.settings.domain.SettingsStore

class InMemorySettingsStore : SettingsStore {

    private val _useGpu = MutableStateFlow(false)
    override val useGpu: StateFlow<Boolean> = _useGpu.asStateFlow()

    override fun setUseGpu(useGpu: Boolean) {
        _useGpu.value = useGpu
    }
}
