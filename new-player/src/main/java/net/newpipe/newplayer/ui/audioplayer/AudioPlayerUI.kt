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


package net.newpipe.newplayer.ui.audioplayer

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.ui.common.getInsets
import net.newpipe.newplayer.ui.selection_ui.ChapterSelectUI
import net.newpipe.newplayer.ui.selection_ui.StreamSelectUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState


private val UI_ENTER_ANIMATION = fadeIn(tween(200))
private val UI_EXIT_ANIMATION = fadeOut(tween(200))

/**hide*/
@Composable
internal fun lightAudioControlButtonColorScheme() = ButtonDefaults.buttonColors().copy(
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onSurface,
    disabledContainerColor = MaterialTheme.colorScheme.background
)

/**hide*/
@Composable
internal fun highlightedLightAudioControlButtonColorScheme() = ButtonDefaults.buttonColors().copy(
    containerColor = MaterialTheme.colorScheme.outlineVariant,
    contentColor = MaterialTheme.colorScheme.onSurface,
    disabledContainerColor = MaterialTheme.colorScheme.background
)

/** @hide */
@OptIn(UnstableApi::class)
@Composable
internal fun AudioPlayerUI(
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    isLandScape: Boolean
) {
    val insets = getInsets()

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.AUDIO_CHAPTER_SELECT,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            ChapterSelectUI(viewModel = viewModel, uiState = uiState, shownInAudioPlayer = true)
        }

        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.AUDIO_STREAM_SELECT,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            StreamSelectUI(viewModel = viewModel, uiState = uiState, shownInAudioPlayer = true)
        }

        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.EMBEDDED_AUDIO,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            AudioPlayerEmbeddedUI(viewModel = viewModel, uiState = uiState)
        }

        AnimatedVisibility(
            uiState.uiMode == UIModeState.FULLSCREEN_AUDIO,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(insets),
            ) { innerPadding ->
                if (isLandScape) {
                    LandscapeLayout(
                        viewModel = viewModel,
                        uiState = uiState,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                    )
                } else {
                    PortraitLayout(
                        viewModel = viewModel,
                        uiState = uiState,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6", showSystemUi = true)
@Composable
private fun AudioPlayerUIPortraitPreview() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(
                uiMode = UIModeState.FULLSCREEN_AUDIO
            ),
            isLandScape = false
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6", showSystemUi = true)
@Composable
private fun AudioPlayerUIPortraitPreviewWithPlaylist() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(
                uiMode = UIModeState.FULLSCREEN_AUDIO,
                showPlaylistInAudioPlayer = true
            ),
            isLandScape = false
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=landscape", showSystemUi = true)
@Composable
private fun AudioPlayerUILandscapePreview() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(uiMode = UIModeState.FULLSCREEN_AUDIO),
            isLandScape = true
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=landscape", showSystemUi = true)
@Composable
private fun AudioPlayerUILandscapePreviewWithPlaylist() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(
                uiMode = UIModeState.FULLSCREEN_AUDIO,
                showPlaylistInAudioPlayer = true
            ),
            isLandScape = true
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=portrait", showSystemUi = true)
@Composable
private fun AudioPlayerUIEmbeddedPreview() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(uiMode = UIModeState.EMBEDDED_AUDIO),
            isLandScape = false
        )
    }
}