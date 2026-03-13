/* NewPlayer
 *
 * @author Christian Schabesberger
 *
 * Copyright (C) NewPipe e.V. 2024 <code(at)newpipe-ev.de>
 *
 * NewPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.newpipe.newplayer.standalone.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.standalone.R
import net.newpipe.newplayer.standalone.ui.theme.StandaloneTheme

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    onPlayFile: (Uri) -> Unit,
    onPlayUrl: (String) -> Unit,
) {
    val context = LocalContext.current
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) { }
        onPlayFile(uri)
    }

    var showUrlDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(32.dp))
        FilledTonalButton(onClick = { filePicker.launch(arrayOf("video/*")) }) {
            Text(stringResource(R.string.open_video_file))
        }
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = { showUrlDialog = true }) {
            Text(stringResource(R.string.play_network_stream))
        }
    }

    if (showUrlDialog) {
        UrlInputDialog(
            onDismiss = { showUrlDialog = false },
            onConfirm = { url ->
                showUrlDialog = false
                onPlayUrl(url)
            },
        )
    }
}

@Preview(name = "StartScreen – portrait", widthDp = 400, heightDp = 800, showBackground = true)
@Composable
private fun StartScreenPortraitPreview() {
    StandaloneTheme {
        StartScreen(
            modifier = Modifier.fillMaxSize(),
            onPlayFile = {},
            onPlayUrl = {},
        )
    }
}

@Preview(name = "StartScreen – landscape", widthDp = 800, heightDp = 400, showBackground = true)
@Composable
private fun StartScreenLandscapePreview() {
    StandaloneTheme {
        StartScreen(
            modifier = Modifier.fillMaxSize(),
            onPlayFile = {},
            onPlayUrl = {},
        )
    }
}