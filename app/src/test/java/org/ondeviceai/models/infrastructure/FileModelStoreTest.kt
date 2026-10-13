package org.ondeviceai.models.infrastructure

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.ondeviceai.models.domain.MODEL_CATALOG
import java.io.File

class FileModelStoreTest {

    @get:Rule
    val modelDir = TemporaryFolder()

    private val first = MODEL_CATALOG[0]
    private val second = MODEL_CATALOG[1]

    @Test
    fun `without files nothing is downloaded or selected`() {
        val store = FileModelStore(modelDir.root)

        assertEquals(emptyList<Any>(), store.downloadedModels.value)
        assertNull(store.selectedModel.value)
    }

    @Test
    fun `existing file counts as downloaded and is selected`() {
        modelDir.newFile(second.fileName)

        val store = FileModelStore(modelDir.root)

        assertEquals(listOf(second), store.downloadedModels.value)
        assertEquals(second, store.selectedModel.value)
    }

    @Test
    fun `markDownloaded adds the model and selects it if none is selected`() {
        val store = FileModelStore(modelDir.root)

        store.markDownloaded(first)

        assertEquals(listOf(first), store.downloadedModels.value)
        assertEquals(first, store.selectedModel.value)
    }

    @Test
    fun `markDownloaded keeps the current selection`() {
        modelDir.newFile(first.fileName)
        val store = FileModelStore(modelDir.root)

        store.markDownloaded(second)

        assertEquals(listOf(first, second), store.downloadedModels.value)
        assertEquals(first, store.selectedModel.value)
    }

    @Test
    fun `select changes the selected model`() {
        val store = FileModelStore(modelDir.root)

        store.select(second)

        assertEquals(second, store.selectedModel.value)
    }

    @Test
    fun `progress is set and removed per file name`() {
        val store = FileModelStore(modelDir.root)

        store.setProgress(first, 0.5f)
        assertEquals(mapOf(first.fileName to 0.5f), store.downloadProgress.value)

        store.removeProgress(first)
        assertEquals(emptyMap<String, Float>(), store.downloadProgress.value)
    }

    @Test
    fun `error is stored`() {
        val store = FileModelStore(modelDir.root)

        store.setError("Fehler")

        assertEquals("Fehler", store.error.value)
    }

    @Test
    fun `pathOf points into the model directory`() {
        val store = FileModelStore(modelDir.root)

        assertEquals(File(modelDir.root, first.fileName).path, store.pathOf(first))
    }
}
