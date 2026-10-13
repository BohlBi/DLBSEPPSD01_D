package org.ondeviceai.models.domain

data class Model(
    val name: String,
    val url: String,
    val fileName: String,
    val sizeBytes: Long,
)
