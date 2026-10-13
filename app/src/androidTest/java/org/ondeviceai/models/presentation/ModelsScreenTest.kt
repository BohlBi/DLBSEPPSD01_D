package org.ondeviceai.models.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.ondeviceai.R
import org.ondeviceai.models.domain.MODEL_CATALOG
import org.ondeviceai.models.domain.Model
import org.ondeviceai.models.domain.ModelService
import org.ondeviceai.models.infrastructure.FileModelStore
import org.ondeviceai.shared.theme.OnDeviceAITheme
import java.io.File

@RunWith(AndroidJUnit4::class)
class ModelsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val downloaded = MODEL_CATALOG[0]
    private val notDownloaded = MODEL_CATALOG[1]
    private val requested = mutableListOf<Model>()

    private val modelService = object : ModelService {
        override fun download(model: Model) {
            requested += model
        }
    }

    @Before
    fun setUp() {
        val modelDir = File(context.cacheDir, "models-screen-test").apply {
            deleteRecursively()
            mkdirs()
        }
        File(modelDir, downloaded.fileName).createNewFile()
        val viewModel = ModelsViewModel(FileModelStore(modelDir), modelService)
        composeRule.setContent { OnDeviceAITheme { ModelsScreen(viewModel = viewModel) } }
    }

    @Test
    fun showsAllModelsFromTheCatalog() {
        MODEL_CATALOG.forEach { composeRule.onNodeWithText(it.name).assertIsDisplayed() }
    }

    @Test
    fun showsDownloadedStateAndDownloadButton() {
        composeRule.onNodeWithText(context.getString(R.string.models_downloaded)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.models_download)).assertIsDisplayed()
    }

    @Test
    fun downloadButtonStartsDownload() {
        composeRule.onNodeWithText(context.getString(R.string.models_download)).performClick()

        assertEquals(listOf(notDownloaded), requested)
    }
}
