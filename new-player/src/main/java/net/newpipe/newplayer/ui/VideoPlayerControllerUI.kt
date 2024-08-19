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

package net.newpipe.newplayer.ui


import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.BottomUI
import net.newpipe.newplayer.ui.videoplayer.CenterUI
import net.newpipe.newplayer.ui.videoplayer.TopUI
import net.newpipe.newplayer.ui.videoplayer.GestureUI
import net.newpipe.newplayer.utils.getDefaultBrightness

@Composable
fun VideoPlayerControllerUI(
    viewModel: VideoPlayerViewModel,
    uiState: VideoPlayerUIState
) {

    val context = LocalContext.current

    if (uiState.fullscreen) {
        BackHandler {
            viewModel.switchToEmbeddedView()
        }
    }

    val internalBrightnessChange = { rateChange: Float ->
        val systemBrightness = getDefaultBrightness(context as Activity)
        viewModel.brightnessChange(rateChange, systemBrightness)
    }

    val insets =
        WindowInsets.systemBars
            .union(WindowInsets.displayCutout)
            .union(WindowInsets.waterfall)

    AnimatedVisibility(uiState.uiVisible) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = Color(0x75000000)
        ) {}
    }

    GestureUI(
        modifier = Modifier
            .fillMaxSize(),
        viewModel = viewModel,
        uiState = uiState
    )

    if (uiState.isLoading) {
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

    AnimatedVisibility(uiState.uiVisible) {

        Box(modifier = Modifier.fillMaxSize()) {
            CenterUI(
                modifier = Modifier.align(Alignment.Center),
                viewModel = viewModel,
                uiState = uiState
            )
        }

        Box(
            modifier = if (uiState.fullscreen) Modifier.windowInsetsPadding(insets) else Modifier
        ) {
            TopUI(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 45.dp)
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp)
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
private fun ViewInFullScreen() {
    //LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}

@Composable
fun PreviewBackgroundSurface(
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

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerUIPreviewEmbedded() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(VideoPlayerViewModelDummy(), VideoPlayerUIState.DEFAULT)
        }
    }
}

@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerUIPreviewLandscape() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(VideoPlayerViewModelDummy(), VideoPlayerUIState.DEFAULT)
        }
    }
}

@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=portrait")
@Composable
fun VideoPlayerControllerUIPreviewPortrait() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(VideoPlayerViewModelDummy(), VideoPlayerUIState.DEFAULT)
        }
    }
}