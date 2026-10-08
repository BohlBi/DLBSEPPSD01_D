package org.ondeviceai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.ondeviceai.navigation.AppNavigation
import org.ondeviceai.shared.theme.OnDeviceAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnDeviceAITheme {
                AppNavigation()
            }
        }
    }
}
