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
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toRect
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.ui.selection_ui.StreamSelectUI
import androidx.lifecycle.LifecycleEventObserver
import net.newpipe.newplayer.data.NewPlayerException
import net.newpipe.newplayer.uiModel.EmbeddedUiConfig
import net.newpipe.newplayer.ui.selection_ui.ChapterSelectUI
import net.newpipe.newplayer.ui.videoplayer.pip.getPipParams
import net.newpipe.newplayer.ui.videoplayer.pip.supportsPip
import net.newpipe.newplayer.ui.common.getEmbeddedUiConfig

@OptIn(UnstableApi::class)
@Composable
internal fun VideoPlayerUi(viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState) {
    val embeddedUiConfig = if (LocalContext.current is Activity)
        getEmbeddedUiConfig(activity = LocalContext.current as Activity)
    else
        EmbeddedUiConfig.DUMMY

    val exoPlayer by viewModel.newPlayer?.exoPlayer!!.collectAsState()

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val activity = LocalContext.current as Activity

    val displayMetrics = activity.resources.displayMetrics

    val screenRatio =
        displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels.toFloat()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var videoViewBounds by remember {
        mutableStateOf(android.graphics.Rect())
    }

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

    LaunchedEffect(uiState.enteringPip) {
        // TODO what if supportsPip returns false? Shouldn't the enteringPip flag be cleared?
        //  Probably the supportsPip check can be done in Application and then be available
        //  throughout the app execution, so that the check can be done in the view model and
        //  PIP state changes can be ignored.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && supportsPip(activity))
            if (uiState.enteringPip) {
                val pipParams = getPipParams(uiState.contentRatio, videoViewBounds)
                if (pipParams != null) {
                    activity.enterPictureInPictureMode(pipParams)
                } else {
                    throw NewPlayerException("Pip params where null even though pip seemed to be supported.")
                }
                viewModel.doneEnteringPip()
            }
    }

    Surface(
        modifier = Modifier
            .then(
                if (uiState.uiMode.fullscreen) Modifier.fillMaxSize()
                else Modifier
                    .fillMaxWidth()
                    .aspectRatio(uiState.embeddedUiRatio)
            ), color = Color.Black
    ) {

        exoPlayer?.let { exoPlayer ->
            Box(contentAlignment = Alignment.Center) {
                PlaySurface(
                    modifier = Modifier.onGloballyPositioned {
                        videoViewBounds = it
                            .boundsInWindow()
                            .toAndroidRectF()
                            .toRect()
                    },
                    player = exoPlayer,
                    lifecycle = lifecycle,
                    fitMode = uiState.contentFitMode,
                    uiRatio = if (uiState.uiMode.fullscreen) screenRatio
                    else uiState.embeddedUiRatio,
                    contentRatio = uiState.contentRatio,
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
                    shownInAudioPlayer = false
                )
            }
        }
        AnimatedVisibility(visible = uiState.uiMode.isChapterSelect) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = STREAMSELECT_UI_BACKGROUND_COLOR
            ) {
                ChapterSelectUI(
                    viewModel = viewModel,
                    uiState = uiState,
                    shownInAudioPlayer = false
                )
            }
        }
    }
}
