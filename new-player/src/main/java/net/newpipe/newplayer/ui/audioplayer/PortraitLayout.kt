package net.newpipe.newplayer.ui.audioplayer

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
internal fun PortraitLayout(
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.animation.AnimatedVisibility(uiState.showPlaylistInAudioPlayer) {
            StreamSelectTopBar(viewModel = viewModel, uiState = uiState, isUsedInAudiPlayer = true)
        }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            PortraitCoverArtOrPlaylistUI(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                viewModel = viewModel,
                uiState = uiState
            )
            AudioPlaybackControllerUI(viewModel = viewModel, uiState = uiState)
            ProgressUI(viewModel = viewModel, uiState = uiState)
            AudioBottomUI(viewModel, uiState, {})
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PortraitCoverArtOrPlaylistUI(
    modifier: Modifier = Modifier,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(uiState.showPlaylistInAudioPlayer, enter = fadeIn(), exit = fadeOut()) {
            ReorderableStreamItemsList(
                modifier = Modifier
                    .fillMaxSize(),
                viewModel = viewModel,
                uiState = uiState
            )
        }

        AnimatedVisibility(!uiState.showPlaylistInAudioPlayer, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CoverArtUI(uiState = uiState)
                Spacer(modifier = Modifier.height(24.dp))
                TitleView(uiState = uiState)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6")
@Composable
private fun AudioPlayerUIPortraitPreview() {
    VideoPlayerTheme {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            PortraitLayout(
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_AUDIO
                )
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6")
@Composable
private fun AudioPlayerUIPortraitPreviewWithPlaylist() {
    VideoPlayerTheme {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            PortraitLayout(
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_AUDIO,
                    showPlaylistInAudioPlayer = true
                )
            )
        }
    }
}

