package org.ondeviceai.models.domain

import org.ondeviceai.models.api.Model

val MODEL_CATALOG = listOf(
    huggingFaceModel("Gemma 4 E2B", "litert-community/gemma-4-E2B-it-litert-lm", "gemma-4-E2B-it.litertlm", 2_588_147_712),
    huggingFaceModel("Qwen3 0.6B", "litert-community/Qwen3-0.6B", "Qwen3-0.6B.litertlm", 614_000_000),
    huggingFaceModel("SmolLM2 360M", "litert-community/SmolLM2-360M-Instruct", "SmolLM2_360M_instruct.litertlm", 373_000_000),
    huggingFaceModel("Qwen3.5 0.8B", "litert-community/Qwen3.5-0.8B", "Qwen3.5-0.8B_int8.litertlm", 964_000_000),
)

private fun huggingFaceModel(name: String, repo: String, fileName: String, sizeBytes: Long) =
    Model(name, "https://huggingface.co/$repo/resolve/main/$fileName", fileName, sizeBytes)
