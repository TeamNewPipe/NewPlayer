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
import android.content.pm.ActivityInfo
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelImpl
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.LockScreenOrientation

@Composable
fun VideoPlayerUI(
    viewModel: VideoPlayerViewModel?,
) {
    if (viewModel == null) {
        VideoPlayerLoadingPlaceholder()
    } else if (viewModel.player == null) {
        VideoPlayerLoadingPlaceholder(viewModel.uiState.collectAsState().value.embeddedUiRatio)
    } else {
        val uiState by viewModel.uiState.collectAsState()

        var lifecycle by remember {
            mutableStateOf(Lifecycle.Event.ON_CREATE)
        }

        val activity = LocalContext.current as Activity
        val view = LocalView.current

        val window = activity.window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val lifecycleOwner = LocalLifecycleOwner.current

        // Setup fullscreen
        if (uiState.fullscreen) {
            LaunchedEffect(key1 = true) {
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = false
            }
        }

        // Setup immersive mode
        if (uiState.fullscreen && ! uiState.uiVissible) {
            LaunchedEffect(key1 = true) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        } else {
            LaunchedEffect(key1 = false) {
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
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

        // Set Screen Rotation
        if (uiState.fullscreen) {
            if (uiState.contentRatio < 1) {
                LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } else {
                LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
        }

        val displayMetrics = activity.resources.displayMetrics
        val screenRatio =
            displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels.toFloat()

        // Set UI
        Surface(
            modifier = Modifier.then(
                if (uiState.fullscreen) Modifier.fillMaxSize()
                else Modifier
                    .fillMaxWidth()
                    .aspectRatio(uiState.embeddedUiRatio)
            ), color = Color.Black
        ) {
            Box(contentAlignment = Alignment.Center) {
                PlaySurface(
                    player = viewModel.player,
                    lifecycle = lifecycle,
                    fitMode = uiState.contentFitMode,
                    uiRatio = if (uiState.fullscreen) screenRatio else uiState.embeddedUiRatio,
                    contentRatio = uiState.contentRatio
                )
            }
            VideoPlayerControllerUI(
                isPlaying = uiState.playing,
                fullscreen = uiState.fullscreen,
                uiVissible = uiState.uiVissible,
                seekPosition = uiState.seekerPosition,
                isLoading = uiState.isLoading,
                durationInMs = uiState.durationInMs,
                playbackPositionInMs = uiState.playbackPositionInMs,
                bufferedPercentage = uiState.bufferedPercentage,
                play = viewModel::play,
                pause = viewModel::pause,
                prevStream = viewModel::prevStream,
                nextStream = viewModel::nextStream,
                switchToFullscreen = viewModel::switchToFullscreen,
                switchToEmbeddedView = viewModel::switchToEmbeddedView,
                showUi = viewModel::showUi,
                hideUi = viewModel::hideUi,
                seekPositionChanged = viewModel::seekPositionChanged,
                seekingFinished = viewModel::seekingFinished,
                embeddedDraggedDownBy = viewModel::embeddedDraggedDown,
                fastSeekForward = viewModel::fastSeekForward,
                fastSeekBackward = viewModel::fastSeekBackward
            )
        }
    }
}

@Composable
fun PlaySurface(
    player: Player?,
    lifecycle: Lifecycle.Event,
    fitMode: ContentScale,
    uiRatio: Float,
    contentRatio: Float
) {
    val viewBoxModifier = Modifier
    viewBoxModifier
        .fillMaxWidth()
        .aspectRatio(16F / 9F)

    /*
    Box(
        modifier = Modifier
            .then(
                when (fitMode) {
                    ContentScale.FILL -> Modifier.fillMaxSize()
                    ContentScale.FIT_INSIDE -> Modifier
                        .aspectRatio(contentRatio)
                        .then(
                            if (contentRatio < uiRatio) Modifier
                                .fillMaxWidth() else Modifier.fillMaxHeight()
                        )

                    ContentScale.CROP -> Modifier
                        .aspectRatio(contentRatio)
                        .wrapContentWidth(unbounded = true)
                        .fillMaxSize()
                }
            )
    ) {
     */
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(contentRatio)
    ) {
        AndroidView(factory = { context ->
            SurfaceView(context).also { view ->
                player?.setVideoSurfaceView(view)
            }
        }, update = { view ->
            when (lifecycle) {
                Lifecycle.Event.ON_RESUME -> {
                    player?.setVideoSurfaceView(view)
                }

                else -> Unit
            }
        })

    }
}


@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
fun PlayerUIPreviewEmbeded() {
    VideoPlayerTheme {
        VideoPlayerUI(viewModel = VideoPlayerViewModelImpl.dummy)
    }
}