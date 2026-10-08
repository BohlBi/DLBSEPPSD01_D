package org.ondeviceai.settings.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsViewModelTest {

    @Test
    fun `initial state is ComingSoon`() {
        val viewModel = SettingsViewModel()

        assertEquals(SettingsUiState.ComingSoon, viewModel.uiState.value)
    }
}
