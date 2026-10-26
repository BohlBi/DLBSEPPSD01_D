package org.ondeviceai.settings.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ondeviceai.R
import org.ondeviceai.settings.infrastructure.InMemorySettingsStore
import org.ondeviceai.shared.theme.OnDeviceAITheme

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val store = InMemorySettingsStore()

    @Before
    fun setUp() {
        composeRule.setContent { OnDeviceAITheme { SettingsScreen(viewModel = SettingsViewModel(store)) } }
    }

    @Test
    fun showsGpuSetting() {
        composeRule.onNodeWithText(context.getString(R.string.settings_use_gpu)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.settings_use_gpu_description)).assertIsDisplayed()
        composeRule.onNode(isToggleable()).assertIsOn()
    }

    @Test
    fun switchTurnsGpuOff() {
        composeRule.onNode(isToggleable()).performClick()

        composeRule.onNode(isToggleable()).assertIsOff()
        assertFalse(store.useGpu.value)
    }
}
