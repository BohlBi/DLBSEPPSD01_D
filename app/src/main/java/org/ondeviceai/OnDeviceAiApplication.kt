package org.ondeviceai

import android.app.Application
import okhttp3.OkHttpClient
import org.ondeviceai.models.api.ModelStore
import org.ondeviceai.models.domain.ModelService
import org.ondeviceai.models.infrastructure.HuggingFaceApi
import org.ondeviceai.models.infrastructure.RetrofitModelService
import retrofit2.Retrofit

class OnDeviceAiApplication : Application() {

    val modelStore: ModelStore by lazy { ModelStore(filesDir) }

    val modelService: ModelService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://huggingface.co/")
            .client(OkHttpClient())
            .build()
        RetrofitModelService(modelStore, retrofit.create(HuggingFaceApi::class.java))
    }
}
