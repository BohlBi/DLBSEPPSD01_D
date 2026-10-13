package org.ondeviceai.models.domain

import org.ondeviceai.models.api.Model

interface ModelService {
    fun download(model: Model)
}
