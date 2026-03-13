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

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.uiModel.NewPlayerViewModelImpl
import net.newpipe.newplayer.uiModel.UIModeState

@OptIn(UnstableApi::class)
@Composable
fun AppUI(
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