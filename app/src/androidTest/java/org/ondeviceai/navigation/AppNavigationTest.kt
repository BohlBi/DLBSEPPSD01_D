package org.ondeviceai.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ondeviceai.R
import org.ondeviceai.chat.presentation.CHAT_SCREEN_TEST_TAG
import org.ondeviceai.models.presentation.MODELS_SCREEN_TEST_TAG
import org.ondeviceai.settings.presentation.SETTINGS_SCREEN_TEST_TAG
import org.ondeviceai.shared.theme.OnDeviceAITheme

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun navItem(labelRes: Int) =
        composeRule.onNode(hasText(context.getString(labelRes)) and hasClickAction())

    private fun setContent() {
        composeRule.setContent { OnDeviceAITheme { AppNavigation() } }
    }

    @Test
    fun startsOnChatScreen() {
        setContent()

        composeRule.onNodeWithTag(CHAT_SCREEN_TEST_TAG).assertIsDisplayed()
        navItem(R.string.nav_chat).assertIsSelected()
    }

    @Test
    fun navigationItemsShowTheirScreens() {
        setContent()

        navItem(R.string.nav_models).performClick()
        composeRule.onNodeWithTag(MODELS_SCREEN_TEST_TAG).assertIsDisplayed()
        navItem(R.string.nav_models).assertIsSelected()

        navItem(R.string.nav_settings).performClick()
        composeRule.onNodeWithTag(SETTINGS_SCREEN_TEST_TAG).assertIsDisplayed()
        navItem(R.string.nav_settings).assertIsSelected()

        navItem(R.string.nav_chat).performClick()
        composeRule.onNodeWithTag(CHAT_SCREEN_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun placeholderScreensShowComingSoon() {
        setContent()
        val placeholder = hasText(context.getString(R.string.coming_soon))

        navItem(R.string.nav_models).performClick()
        composeRule.onNode(placeholder).assertIsDisplayed()
        navItem(R.string.nav_settings).performClick()
        composeRule.onNode(placeholder).assertIsDisplayed()
    }

    @Test
    fun backFromOtherScreenReturnsToChat() {
        setContent()

        navItem(R.string.nav_settings).performClick()
        Espresso.pressBack()

        composeRule.onNodeWithTag(CHAT_SCREEN_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun selectedScreenSurvivesStateRestoration() {
        val restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent { OnDeviceAITheme { AppNavigation() } }

        navItem(R.string.nav_models).performClick()
        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithTag(MODELS_SCREEN_TEST_TAG).assertIsDisplayed()
        navItem(R.string.nav_models).assertIsSelected()
    }
}
