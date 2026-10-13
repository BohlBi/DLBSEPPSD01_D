package org.ondeviceai.chat.presentation

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ondeviceai.R
import org.ondeviceai.chat.domain.ChatMessage
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.models.domain.MODEL_CATALOG
import org.ondeviceai.models.infrastructure.FileModelStore
import org.ondeviceai.shared.theme.OnDeviceAITheme
import java.io.File

@RunWith(AndroidJUnit4::class)
class ChatScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val session = ChatSession()
    private val model = MODEL_CATALOG.first()

    private val chatService = object : ChatService {
        override fun sendMessage(message: String) {
            session.addMessage(ChatMessage(message, fromAi = false))
            session.addMessage(ChatMessage("Antwort auf: $message", fromAi = true))
        }

        override fun stop() {}
    }

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .grantRuntimePermission(context.packageName, Manifest.permission.READ_CONTACTS)
        val modelDir = File(context.cacheDir, "chat-screen-test").apply {
            deleteRecursively()
            mkdirs()
        }
        File(modelDir, model.fileName).createNewFile()
        val viewModel = ChatViewModel(session, chatService, FileModelStore(modelDir))
        composeRule.setContent { OnDeviceAITheme { ChatScreen(viewModel = viewModel) } }
    }

    private fun send(text: String) {
        composeRule.onNode(hasSetTextAction()).performTextInput(text)
        composeRule.onNodeWithContentDescription(context.getString(R.string.chat_send)).performClick()
    }

    @Test
    fun showsModelSelectorInputAndSendButton() {
        composeRule.onNodeWithText(context.getString(R.string.chat_model)).assertIsDisplayed()
        composeRule.onNodeWithText(model.name).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.chat_input_placeholder)).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(context.getString(R.string.chat_send)).assertIsDisplayed()
    }

    @Test
    fun showsChatHistory() {
        send("Stell mir einen Wecker für morgen um 7 Uhr")
        send("Starte einen Timer für 20 Minuten")
        send("Ruf Mama an")
        Espresso.closeSoftKeyboard()

        listOf(
            "Stell mir einen Wecker für morgen um 7 Uhr",
            "Antwort auf: Stell mir einen Wecker für morgen um 7 Uhr",
            "Starte einen Timer für 20 Minuten",
            "Antwort auf: Starte einen Timer für 20 Minuten",
            "Ruf Mama an",
            "Antwort auf: Ruf Mama an",
        ).forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test
    fun showsStopButtonWhileThinking() {
        session.setThinking(true)

        composeRule.onNodeWithContentDescription(context.getString(R.string.chat_stop)).assertIsDisplayed()
    }

    @Test
    fun showsError() {
        session.setError("Kein Modell heruntergeladen")

        composeRule.onNodeWithText("Kein Modell heruntergeladen").assertIsDisplayed()
    }
}
