package org.ondeviceai.models.infrastructure

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.ondeviceai.models.domain.MODEL_CATALOG
import java.io.File
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

class RetrofitModelServiceTest {

    @get:Rule
    val modelDir = TemporaryFolder()

    private val model = MODEL_CATALOG.first()

    private class FakeApi(private val body: () -> ResponseBody) : HuggingFaceApi {
        val urls = mutableListOf<String>()

        override suspend fun download(url: String): ResponseBody {
            urls += url
            return body()
        }
    }

    private fun FileModelStore.awaitDownloadFinished() = runBlocking {
        withTimeout(5_000.milliseconds) { downloadProgress.first { it.isEmpty() } }
    }

    @Test
    fun `successful download writes the file and marks the model as downloaded`() {
        val store = FileModelStore(modelDir.root)
        val api = FakeApi { "modell".toResponseBody() }

        RetrofitModelService(store, api).download(model)
        store.awaitDownloadFinished()

        assertEquals(listOf(model.url), api.urls)
        assertEquals("modell", File(store.pathOf(model)).readText())
        assertEquals(listOf(model), store.downloadedModels.value)
        assertNull(store.error.value)
    }

    @Test
    fun `failed download sets the error and leaves no file`() {
        val store = FileModelStore(modelDir.root)
        val api = FakeApi { throw IOException("Netzwerkfehler") }

        RetrofitModelService(store, api).download(model)
        store.awaitDownloadFinished()

        assertEquals("Netzwerkfehler", store.error.value)
        assertFalse(File(store.pathOf(model)).exists())
        assertFalse(File(store.pathOf(model) + ".part").exists())
        assertEquals(emptyList<Any>(), store.downloadedModels.value)
    }

    @Test
    fun `running download is not started twice`() {
        val store = FileModelStore(modelDir.root)
        store.setProgress(model, 0.5f)
        val api = FakeApi { "modell".toResponseBody() }

        RetrofitModelService(store, api).download(model)

        assertEquals(emptyList<String>(), api.urls)
    }
}
