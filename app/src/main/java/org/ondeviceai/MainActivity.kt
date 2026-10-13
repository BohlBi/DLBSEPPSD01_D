package org.ondeviceai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import org.ondeviceai.navigation.AppNavigation
import org.ondeviceai.shared.theme.OnDeviceAITheme

class MainActivity : ComponentActivity() {

    private val requestContacts = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestContacts.launch(Manifest.permission.READ_CONTACTS)
        }
        setContent {
            OnDeviceAITheme {
                AppNavigation()
            }
        }
    }
}
