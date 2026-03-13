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

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.MutableStateFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.NewPlayerDummy
import net.newpipe.newplayer.standalone.ui.theme.StandaloneTheme
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.NewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: NewPlayerViewModel,
    newPlayer: NewPlayer,
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.uiMode.fullscreen) {
        // Fullscreen: NewPlayerUI takes the full screen; system bars are
        // managed by NewPlayerUI itself via WindowCompat APIs.
        Box(modifier = Modifier.fillMaxSize()) {
            NewPlayerUI(viewModel = viewModel)
        }
    } else {
        val isLandscape = LocalConfiguration.current.orientation ==
                Configuration.ORIENTATION_LANDSCAPE
        if (isLandscape) {
            // Landscape: player and bar side by side, each centered in their half
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    NewPlayerUI(viewModel = viewModel)
                }
                AnimatedVisibility(
                    modifier = Modifier.weight(1f),
                    visible = !uiState.uiMode.fullscreen,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally(),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                        AddToQueueBar(
                            vertical = true,
                            onAddFile = { uri ->
                                newPlayer.addToPlaylist(uri.toString())
                            },
                            onAddUrl = { url ->
                                newPlayer.addToPlaylist(url)
                            },
                        )
                    }
                }
            }
        } else {
            // Portrait: player at top, bar below
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth()) {
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

@OptIn(UnstableApi::class)
@Preview(name = "PlayerScreen – portrait", widthDp = 400, heightDp = 800, showBackground = true)
@Composable
private fun PlayerScreenPortraitPreview() {
    StandaloneTheme {
        PlayerScreen(
            viewModel = object : NewPlayerViewModelDummy() {
                override var uiState = MutableStateFlow(NewPlayerUIState.DUMMY)
            },
            newPlayer = NewPlayerDummy(),
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(name = "PlayerScreen – landscape", widthDp = 800, heightDp = 400, showBackground = true)
@Composable
private fun PlayerScreenLandscapePreview() {
    StandaloneTheme {
        PlayerScreen(
            viewModel = object : NewPlayerViewModelDummy() {
                override var uiState = MutableStateFlow(NewPlayerUIState.DUMMY)
            },
            newPlayer = NewPlayerDummy(),
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(name = "PlayerScreen – fullscreen", widthDp = 800, heightDp = 400, showBackground = true)
@Composable
private fun PlayerScreenFullscreenPreview() {
    StandaloneTheme {
        PlayerScreen(
            viewModel = object : NewPlayerViewModelDummy() {
                override var uiState = MutableStateFlow(
                    NewPlayerUIState.DUMMY.copy(
                        uiMode = net.newpipe.newplayer.uiModel.UIModeState.FULLSCREEN_VIDEO
                    )
                )
            },
            newPlayer = NewPlayerDummy(),
        )
    }
}