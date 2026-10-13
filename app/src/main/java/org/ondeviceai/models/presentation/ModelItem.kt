package org.ondeviceai.models.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.ondeviceai.R
import org.ondeviceai.models.domain.Model

@Composable
fun ModelItem(
    model: Model,
    downloaded: Boolean,
    progress: Float?,
    onIntent: (ModelsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = model.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${model.sizeBytes / 1_000_000} MB",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            when {
                downloaded -> Text(
                    text = stringResource(R.string.models_downloaded),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                progress != null -> LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.width(96.dp),
                )
                else -> Button(onClick = { onIntent(ModelsIntent.Download(model)) }) {
                    Text(stringResource(R.string.models_download))
                }
            }
        }
    }
}
