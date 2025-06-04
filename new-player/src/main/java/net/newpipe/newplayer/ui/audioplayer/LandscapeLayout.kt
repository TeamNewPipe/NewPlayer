package net.newpipe.newplayer.ui.audioplayer

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.ui.selection_ui.ReorderableStreamItemsList
import net.newpipe.newplayer.ui.selection_ui.StreamSelectTopBar
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState


/**hide*/
@OptIn(UnstableApi::class)
@Composable
internal fun LandscapeLayout(
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    modifier: Modifier = Modifier,
    showPlaybackSpeedDialog: () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {

            LandscapeCoverArtOrPlaylistUI(
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                uiState = uiState,
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                AnimatedVisibility(uiState.showPlaylistInAudioPlayer) {
                    StreamSelectTopBar(
                        viewModel = viewModel,
                        uiState = uiState,
                        isUsedInAudiPlayer = true
                    )
                }

                AudioPlaybackControllerUI(viewModel = viewModel, uiState = uiState)
                ProgressUI(viewModel = viewModel, uiState = uiState)
                AudioBottomUI(viewModel = viewModel, uiState = uiState, showPlaybackSpeedDialog)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun LandscapeCoverArtOrPlaylistUI(
    modifier: Modifier = Modifier,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(uiState.showPlaylistInAudioPlayer, enter = fadeIn(), exit = fadeOut()) {
            ReorderableStreamItemsList(
                modifier = modifier
                    .fillMaxSize(),
                viewModel = viewModel,
                uiState = uiState
            )
        }
        AnimatedVisibility(!uiState.showPlaylistInAudioPlayer, enter = fadeIn(), exit = fadeOut()) {
            Column(modifier = modifier) {
                TitleView(
                    modifier = Modifier
                        .fillMaxWidth(),
                    uiState = uiState,
                )

                Spacer(modifier = Modifier.height(8.dp))

                CoverArtUI(
                    uiState = uiState
                )
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=landscape")
@Composable
private fun AudioPlayerUILandscapePreview() {
    VideoPlayerTheme {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            AudioPlayerUI(
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY.copy(uiMode = UIModeState.FULLSCREEN_AUDIO),
                isLandScape = true
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=landscape")
@Composable
private fun AudioPlayerUILandscapePreviewWithPlaylist() {
    VideoPlayerTheme {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
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
}