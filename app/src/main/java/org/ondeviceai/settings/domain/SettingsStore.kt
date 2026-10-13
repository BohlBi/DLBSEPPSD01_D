package org.ondeviceai.settings.domain

import kotlinx.coroutines.flow.StateFlow

/** App-Einstellungen; Änderungen wirken beim nächsten Laden der Engine. */
interface SettingsStore {
    val useGpu: StateFlow<Boolean>

    fun setUseGpu(useGpu: Boolean)
}
