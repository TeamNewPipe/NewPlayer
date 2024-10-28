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

package net.newpipe.newplayer.ui.videoplayer

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.controller.BottomUI
import net.newpipe.newplayer.ui.videoplayer.controller.CenterUI
import net.newpipe.newplayer.ui.videoplayer.controller.TopUI
import net.newpipe.newplayer.ui.common.getInsets


/** @hide */
internal val CONTROLLER_UI_BACKGROUND_COLOR = Color(0x75000000)

/** @hide */
internal val STREAMSELECT_UI_BACKGROUND_COLOR = Color(0xba000000)

@OptIn(UnstableApi::class)
@Composable

/** @hide */
internal fun VideoPlayerControllerUI(
    viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState
) {

    var volumeIndicatorVissible by remember {
        mutableStateOf(false)
    }

    val insets = getInsets()

    AnimatedVisibility(uiState.uiMode.videoControllerUiVisible) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = CONTROLLER_UI_BACKGROUND_COLOR
        ) {}
    }

    GestureUI(
        modifier = Modifier.fillMaxSize(), viewModel = viewModel, uiState = uiState,
        onVolumeIndicatorVisibilityChanged = {volumeIndicatorVissible = it}
    )

    AnimatedVisibility(uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    AnimatedVisibility(uiState.uiMode.videoControllerUiVisible) {

        AnimatedVisibility(visible = !uiState.isLoading && !volumeIndicatorVissible) {
            Box(modifier = Modifier.fillMaxSize()) {
                CenterUI(
                    modifier = Modifier.align(Alignment.Center),
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }

        Box(
            modifier = if (uiState.uiMode.fullscreen) Modifier.windowInsetsPadding(insets) else Modifier
        ) {
            TopUI(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 45.dp)
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                viewModel = viewModel,
                uiState = uiState
            )

            BottomUI(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp)
                    .defaultMinSize(minHeight = 40.dp)
                    .fillMaxWidth(),
                viewModel = viewModel,
                uiState = uiState
            )
        }
    }
}

///////////////////////////////////////////////////////////////////
// Utils
///////////////////////////////////////////////////////////////////

@Composable

/** @hide */
internal fun PreviewBackgroundSurface(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Black
    ) {
        content()
    }
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
private fun VideoPlayerControllerUIPreviewEmbedded() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(
                NewPlayerViewModelDummy(), NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.EMBEDDED_VIDEO_CONTROLLER_UI
                )
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=landscape")
@Composable
private fun VideoPlayerControllerUIPreviewLandscape() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(
                NewPlayerViewModelDummy(), NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI
                )
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=portrait")
@Composable
private fun VideoPlayerControllerUIPreviewPortrait() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(
                NewPlayerViewModelDummy(), NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI
                )
            )
        }
    }
}