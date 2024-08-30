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
import android.util.Log
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.StreamSelectUI
import net.newpipe.newplayer.utils.LockScreenOrientation
import net.newpipe.newplayer.utils.getDefaultBrightness
import net.newpipe.newplayer.utils.setScreenBrightness

private const val TAG = "VideoPlayerUI"

@Composable
fun VideoPlayerUI(
    viewModel: VideoPlayerViewModel?,
) {
    if (viewModel == null) {
        VideoPlayerLoadingPlaceholder()
    } else if (viewModel.newPlayer == null) {
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

        val defaultBrightness = getDefaultBrightness(activity)

        // Setup fullscreen

        LaunchedEffect(uiState.uiMode.fullscreen) {
            if (uiState.uiMode.fullscreen) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    false
            } else {
                uiState.embeddedUiConfig?.let {
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                        it.systemBarInLightMode
                }
            }
        }

        if (uiState.uiMode.fullscreen) {
            BackHandler {
                viewModel.onBackPressed()
            }
        }

        // setup immersive mode
        LaunchedEffect(
            key1 = uiState.uiMode.systemInsetsVisible,
        ) {
            if (uiState.uiMode.fullscreen && !uiState.uiMode.systemInsetsVisible) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }

        if (uiState.uiMode.fitScreenRotation) {
            if (uiState.contentRatio < 1) {
                LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } else {
                LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
        } else {
            uiState.embeddedUiConfig?.let {
                LockScreenOrientation(orientation = it.screenOrientation)
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


        val displayMetrics = activity.resources.displayMetrics
        val screenRatio =
            displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels.toFloat()


        LaunchedEffect(key1 = uiState.brightness) {
            Log.d(TAG, "New Brightnes: ${uiState.brightness}")
            setScreenBrightness(
                uiState.brightness ?: defaultBrightness, activity
            )
        }

        // Set UI
        Surface(
            modifier = Modifier.then(
                if (uiState.uiMode.fullscreen) Modifier.fillMaxSize()
                else Modifier
                    .fillMaxWidth()
                    .aspectRatio(uiState.embeddedUiRatio)
            ), color = Color.Black
        ) {
            Box(contentAlignment = Alignment.Center) {
                PlaySurface(
                    player = viewModel.newPlayer?.internalPlayer,
                    lifecycle = lifecycle,
                    fitMode = uiState.contentFitMode,
                    uiRatio = if (uiState.uiMode.fullscreen) screenRatio
                    else uiState.embeddedUiRatio,
                    contentRatio = uiState.contentRatio
                )
            }

            // the checks if VideoPlayerControllerUI should be visible or not are done by
            // The VideoPlayerControllerUI composable itself. This is because Visibility of
            // the controller is more complicated than just using a simple if statement.
            VideoPlayerControllerUI(
                viewModel, uiState = uiState
            )

            AnimatedVisibility(visible = uiState.uiMode.isStreamSelect) {
                StreamSelectUI(viewModel = viewModel, uiState = uiState, isChapterSelect = false)
            }
            AnimatedVisibility(visible = uiState.uiMode.isChapterSelect) {
                StreamSelectUI(viewModel = viewModel, uiState = uiState, isChapterSelect = true)
            }
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
        VideoPlayerUI(viewModel = VideoPlayerViewModelDummy())
    }
}