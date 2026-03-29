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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.MutableStateFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.NewPlayerDummy
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.standalone.ui.theme.StandaloneTheme
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.NewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState

@OptIn(UnstableApi::class)
@Composable
fun AppUI(
    viewModel: NewPlayerViewModel,
    newPlayer: NewPlayer,
) {
    val uiState by viewModel.uiState.collectAsState()
    var errorToShow by remember { mutableStateOf<Throwable?>(null) }

    LaunchedEffect(newPlayer) {
        newPlayer.errorFlow.collect { error ->
            errorToShow = error
        }
    }

    errorToShow?.let { error ->
        ErrorDialog(
            message = error.message ?: error.javaClass.simpleName,
            onDismiss = { errorToShow = null },
        )
    }

    Scaffold(
        contentWindowInsets = if (uiState.uiMode.fullscreen) WindowInsets(0) else ScaffoldDefaults.contentWindowInsets,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (uiState.uiMode == UIModeState.PLACEHOLDER) {
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
            } else {
                PlayerScreen(viewModel = viewModel, newPlayer = newPlayer)
            }
        }
    }
}

@Preview(
    name = "AppUI – placeholder – portrait",
    widthDp = 400,
    heightDp = 800,
    showBackground = true,
)
@Composable
private fun AppUIPlaceholderPortraitPreview() {
    StandaloneTheme {
        StartScreen(
            modifier = Modifier.fillMaxSize(),
            onPlayFile = {},
            onPlayUrl = {},
        )
    }
}

@Preview(
    name = "AppUI – placeholder – landscape",
    widthDp = 800,
    heightDp = 400,
    showBackground = true,
)
@Composable
private fun AppUIPlaceholderLandscapePreview() {
    StandaloneTheme {
        StartScreen(
            modifier = Modifier.fillMaxSize(),
            onPlayFile = {},
            onPlayUrl = {},
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(name = "AppUI – embedded – portrait", widthDp = 400, heightDp = 800, showBackground = true, showSystemUi = true)
@Composable
private fun AppUIEmbeddedPortraitPreview() {
    StandaloneTheme {
        AppUI(viewModel = object : NewPlayerViewModelDummy() {
            override var uiState = MutableStateFlow(NewPlayerUIState.DUMMY)
        }, newPlayer = NewPlayerDummy())
    }
}

@OptIn(UnstableApi::class)
@Preview(
    name = "AppUI – embedded – landscape",
    widthDp = 800,
    heightDp = 400,
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun AppUIEmbeddedLandscapePreview() {
    StandaloneTheme {
        AppUI(viewModel = object : NewPlayerViewModelDummy() {
            override var uiState = MutableStateFlow(NewPlayerUIState.DUMMY)
        }, newPlayer = NewPlayerDummy())
    }
}


@OptIn(UnstableApi::class)
@Preview(name = "PlayerScreen – fullscreen", widthDp = 800, heightDp = 400, showBackground = true)
@Composable
private fun PlayerScreenFullscreenPreview() {
    StandaloneTheme {
        AppUI(
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
