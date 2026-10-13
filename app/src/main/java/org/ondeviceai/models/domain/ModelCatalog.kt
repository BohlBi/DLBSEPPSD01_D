package org.ondeviceai.models.domain

import org.ondeviceai.models.api.Model

val MODEL_CATALOG = listOf(
    huggingFaceModel("Gemma 4 E2B", "litert-community/gemma-4-E2B-it-litert-lm", "gemma-4-E2B-it.litertlm", 2_588_147_712),
    huggingFaceModel("Qwen3 0.6B", "litert-community/Qwen3-0.6B", "Qwen3-0.6B_dynamic_wi4b32_afp32.litertlm", 344_671_744),
)

private fun huggingFaceModel(name: String, repo: String, fileName: String, sizeBytes: Long) =
    Model(name, "https://huggingface.co/$repo/resolve/main/$fileName", fileName, sizeBytes)
