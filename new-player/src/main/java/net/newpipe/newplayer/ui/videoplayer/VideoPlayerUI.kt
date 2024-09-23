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

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.ui.PlaySurface
import net.newpipe.newplayer.ui.streamselect.StreamSelectUI
import androidx.lifecycle.LifecycleEventObserver
import net.newpipe.newplayer.ui.streamselect.ChapterSelectUI

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerUi(viewModel: NewPlayerViewModel, uiState: NewPlayerUIState) {
    val exoPlayer by viewModel.newPlayer?.exoPlayer!!.collectAsState()

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val activity = LocalContext.current as Activity

    val displayMetrics = activity.resources.displayMetrics

    val screenRatio =
        displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels.toFloat()

    val lifecycleOwner = LocalLifecycleOwner.current

    // Prepare stuff for the SurfaceView to which the video will be rendered
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        modifier = Modifier.then(
            if (uiState.uiMode.fullscreen) Modifier.fillMaxSize()
            else Modifier
                .fillMaxWidth()
                .aspectRatio(uiState.embeddedUiRatio)
        ), color = Color.Black
    ) {

        exoPlayer?.let { exoPlayer ->
            Box(contentAlignment = Alignment.Center) {
                PlaySurface(
                    player = exoPlayer,
                    lifecycle = lifecycle,
                    fitMode = uiState.contentFitMode,
                    uiRatio = if (uiState.uiMode.fullscreen) screenRatio
                    else uiState.embeddedUiRatio,
                    contentRatio = uiState.contentRatio
                )
            }
        }


        // the checks if VideoPlayerControllerUI should be visible or not are done by
        // The VideoPlayerControllerUI composable itself. This is because Visibility of
        // the controller is more complicated than just using a simple if statement.
        VideoPlayerControllerUI(
            viewModel, uiState = uiState
        )

        AnimatedVisibility(visible = uiState.uiMode.isStreamSelect) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = STREAMSELECT_UI_BACKGROUND_COLOR
            ) {
                StreamSelectUI(
                    viewModel = viewModel,
                    uiState = uiState,
                )
            }
        }
        AnimatedVisibility(visible = uiState.uiMode.isChapterSelect) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = STREAMSELECT_UI_BACKGROUND_COLOR
            ) {
                ChapterSelectUI(viewModel = viewModel, uiState = uiState)
            }
        }
    }
}