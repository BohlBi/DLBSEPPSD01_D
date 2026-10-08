package org.ondeviceai.models.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class ModelsViewModelTest {

    @Test
    fun `initial state is ComingSoon`() {
        val viewModel = ModelsViewModel()

        assertEquals(ModelsUiState.ComingSoon, viewModel.uiState.value)
    }
}
