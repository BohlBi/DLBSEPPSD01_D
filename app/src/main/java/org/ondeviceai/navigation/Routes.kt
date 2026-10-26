package org.ondeviceai.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ChatRoute : NavKey

@Serializable
data object ModelsRoute : NavKey

@Serializable
data object SettingsRoute : NavKey
