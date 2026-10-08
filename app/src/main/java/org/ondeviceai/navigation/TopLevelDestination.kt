package org.ondeviceai.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import org.ondeviceai.R

enum class TopLevelDestination(
    val route: NavKey,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    CHAT(ChatRoute, R.string.nav_chat, Icons.AutoMirrored.Filled.Chat),
    MODELS(ModelsRoute, R.string.nav_models, Icons.Filled.Download),
    SETTINGS(SettingsRoute, R.string.nav_settings, Icons.Filled.Settings),
}
