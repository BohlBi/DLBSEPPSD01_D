package org.ondeviceai.models.api

data class Model(
    val name: String,
    val url: String,
    val fileName: String,
    val sizeBytes: Long,
)
