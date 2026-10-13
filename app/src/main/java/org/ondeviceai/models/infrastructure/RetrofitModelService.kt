package org.ondeviceai.models.infrastructure

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.ondeviceai.models.domain.Model
import org.ondeviceai.models.application.ModelStore
import org.ondeviceai.models.domain.ModelService
import java.io.File

class RetrofitModelService(
    private val store: ModelStore,
    private val api: HuggingFaceApi,
) : ModelService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun download(model: Model) {
        if (model.fileName in store.downloadProgress.value) return
        store.setError(null)
        store.setProgress(model, 0f)
        scope.launch {
            val target = File(store.pathOf(model))
            val partFile = File(target.path + ".part")
            try {
                api.download(model.url).use { body ->
                    val total = body.contentLength().takeIf { it > 0 } ?: model.sizeBytes
                    body.byteStream().use { input ->
                        partFile.outputStream().use { output ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var written = 0L
                            var percent = 0L
                            var read = input.read(buffer)
                            while (read >= 0) {
                                output.write(buffer, 0, read)
                                written += read
                                if (written * 100 / total != percent) {
                                    percent = written * 100 / total
                                    store.setProgress(model, percent / 100f)
                                }
                                read = input.read(buffer)
                            }
                        }
                    }
                }
                if (!partFile.renameTo(target)) error("Datei konnte nicht gespeichert werden")
                store.markDownloaded(model)
            } catch (e: Exception) {
                partFile.delete()
                store.setError(e.message ?: e.javaClass.simpleName)
            } finally {
                store.removeProgress(model)
            }
        }
    }
}
