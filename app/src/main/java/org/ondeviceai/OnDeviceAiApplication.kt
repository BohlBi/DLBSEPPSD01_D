package org.ondeviceai

import android.app.Application
import okhttp3.OkHttpClient
import org.ondeviceai.chat.domain.ChatService
import org.ondeviceai.chat.domain.ChatSession
import org.ondeviceai.chat.infrastructure.LiteRtChatImpl
import org.ondeviceai.models.domain.ModelStore
import org.ondeviceai.models.domain.ModelService
import org.ondeviceai.models.infrastructure.FileModelStore
import org.ondeviceai.models.infrastructure.HuggingFaceApi
import org.ondeviceai.models.infrastructure.RetrofitModelService
import org.ondeviceai.settings.domain.SettingsStore
import org.ondeviceai.settings.infrastructure.InMemorySettingsStore
import retrofit2.Retrofit

/** Composition Root: erzeugt die gemeinsamen Instanzen, die Screens nur abholen. */
class OnDeviceAiApplication : Application() {

    val modelStore: ModelStore by lazy { FileModelStore(filesDir) }

    val settingsStore: SettingsStore by lazy { InMemorySettingsStore() }

    val chatSession: ChatSession by lazy { ChatSession() }

    val chatService: ChatService by lazy { LiteRtChatImpl(chatSession, modelStore, settingsStore, cacheDir, this) }

    val modelService: ModelService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://huggingface.co/")
            .client(OkHttpClient())
            .build()
        RetrofitModelService(modelStore, retrofit.create(HuggingFaceApi::class.java))
    }
}
