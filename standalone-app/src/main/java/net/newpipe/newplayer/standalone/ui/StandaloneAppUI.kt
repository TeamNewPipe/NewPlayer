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
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.standalone.R
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.uiModel.NewPlayerViewModelImpl
import net.newpipe.newplayer.uiModel.UIModeState

@OptIn(UnstableApi::class)
@Composable
fun StandaloneAppUI(
    viewModel: NewPlayerViewModelImpl,
    newPlayer: NewPlayer,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(newPlayer) {
        newPlayer.errorFlow.collect { error ->
            snackbarHostState.showSnackbar(
                message = error.message ?: error.javaClass.simpleName,
                duration = SnackbarDuration.Short,
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                uiState.uiMode == UIModeState.PLACEHOLDER -> {
                    StartScreen(
                        modifier = Modifier.fillMaxSize(),
                        onPlayFile = { uri ->
                            newPlayer.playWhenReady = true
                            newPlayer.playStream(uri.toString(), PlayMode.EMBEDDED_VIDEO)
                        },
                        onPlayUrl = { url ->
                            newPlayer.playWhenReady = true
                            newPlayer.playStream(url, PlayMode.EMBEDDED_VIDEO)
                        },
                    )
                }

                uiState.uiMode.fullscreen -> {
                    // Fullscreen: NewPlayerUI takes the full screen; system bars are
                    // managed by NewPlayerUI itself via WindowCompat APIs.
                    Box(modifier = Modifier.fillMaxSize()) {
                        NewPlayerUI(viewModel = viewModel)
                    }
                }

                else -> {
                    // Embedded: player at top (16:9) + add-to-queue bar below
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                        ) {
                            NewPlayerUI(viewModel = viewModel)
                        }
                        AnimatedVisibility(
                            visible = !uiState.uiMode.fullscreen,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                        ) {
                            AddToQueueBar(
                                onAddFile = { uri ->
                                    newPlayer.addToPlaylist(uri.toString())
                                },
                                onAddUrl = { url ->
                                    newPlayer.addToPlaylist(url)
                                },
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StartScreen(
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

@Composable
private fun AddToQueueBar(
    modifier: Modifier = Modifier,
    onAddFile: (Uri) -> Unit,
    onAddUrl: (String) -> Unit,
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
        onAddFile(uri)
    }

    var showUrlDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { filePicker.launch(arrayOf("video/*")) },
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.add_file))
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { showUrlDialog = true },
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.add_stream_url))
            }
        }
    }

    if (showUrlDialog) {
        UrlInputDialog(
            onDismiss = { showUrlDialog = false },
            onConfirm = { url ->
                showUrlDialog = false
                onAddUrl(url)
            },
        )
    }
}

@Composable
private fun UrlInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.enter_stream_url)) },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text(stringResource(R.string.url_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (url.isNotBlank()) onConfirm(url) },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (url.isNotBlank()) onConfirm(url) },
                enabled = url.isNotBlank(),
            ) {
                Text(stringResource(R.string.play))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
