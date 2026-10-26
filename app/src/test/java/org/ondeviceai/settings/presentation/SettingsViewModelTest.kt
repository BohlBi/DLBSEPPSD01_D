package org.ondeviceai.settings.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.ondeviceai.settings.infrastructure.InMemorySettingsStore

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val store = InMemorySettingsStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state uses GPU`() {
        val viewModel = SettingsViewModel(store)

        assertEquals(SettingsUiState(useGpu = true), viewModel.uiState.value)
    }

    @Test
    fun `SetUseGpu updates the store and the state`() {
        val viewModel = SettingsViewModel(store)

        viewModel.onIntent(SettingsIntent.SetUseGpu(false))

        assertFalse(store.useGpu.value)
        assertFalse(viewModel.uiState.value.useGpu)
    }

    @Test
    fun `changes in the store are shown`() {
        val viewModel = SettingsViewModel(store)
        store.setUseGpu(false)

        store.setUseGpu(true)

        assertTrue(viewModel.uiState.value.useGpu)
    }
}
