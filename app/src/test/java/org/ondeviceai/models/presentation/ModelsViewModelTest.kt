package org.ondeviceai.models.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.ondeviceai.models.domain.Model
import org.ondeviceai.models.infrastructure.FileModelStore
import org.ondeviceai.models.domain.MODEL_CATALOG
import org.ondeviceai.models.domain.ModelService

@OptIn(ExperimentalCoroutinesApi::class)
class ModelsViewModelTest {

    @get:Rule
    val modelDir = TemporaryFolder()

    private val requestedDownloads = mutableListOf<Model>()
    private val modelService = object : ModelService {
        override fun download(model: Model) {
            requestedDownloads += model
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows the catalog without downloads`() {
        val viewModel = ModelsViewModel(FileModelStore(modelDir.root), modelService)

        assertEquals(ModelsUiState(models = MODEL_CATALOG), viewModel.uiState.value)
    }

    @Test
    fun `existing model file counts as downloaded`() {
        val model = MODEL_CATALOG.first()
        modelDir.newFile(model.fileName)

        val viewModel = ModelsViewModel(FileModelStore(modelDir.root), modelService)

        assertEquals(listOf(model), viewModel.uiState.value.downloaded)
    }

    @Test
    fun `Download passes the model to the service`() {
        val viewModel = ModelsViewModel(FileModelStore(modelDir.root), modelService)
        val model = MODEL_CATALOG.last()

        viewModel.onIntent(ModelsIntent.Download(model))

        assertEquals(listOf(model), requestedDownloads)
    }

    @Test
    fun `progress and error from the store are shown`() {
        val store = FileModelStore(modelDir.root)
        val viewModel = ModelsViewModel(store, modelService)
        val model = MODEL_CATALOG.first()

        store.setProgress(model, 0.5f)
        store.setError("Netzwerkfehler")

        assertEquals(mapOf(model.fileName to 0.5f), viewModel.uiState.value.progress)
        assertEquals("Netzwerkfehler", viewModel.uiState.value.error)
    }

    @Test
    fun `finished download appears as downloaded`() {
        val store = FileModelStore(modelDir.root)
        val viewModel = ModelsViewModel(store, modelService)
        val model = MODEL_CATALOG.first()

        store.markDownloaded(model)

        assertEquals(listOf(model), viewModel.uiState.value.downloaded)
    }
}
