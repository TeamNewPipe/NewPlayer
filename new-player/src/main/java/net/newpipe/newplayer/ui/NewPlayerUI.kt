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
import android.os.Build
import android.util.Log
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.audioplayer.AudioPlayerUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.VideoPlayerUi
import net.newpipe.newplayer.utils.LockScreenOrientation
import net.newpipe.newplayer.utils.getDefaultBrightness
import net.newpipe.newplayer.utils.isInPowerSaveMode
import net.newpipe.newplayer.utils.setScreenBrightness

private const val TAG = "VideoPlayerUI"

@OptIn(UnstableApi::class)
@Composable
fun NewPlayerUI(
    viewModel: NewPlayerViewModel?,
) {
    if (viewModel == null) {
        LoadingPlaceholder()
    } else if (viewModel.newPlayer == null) {
        LoadingPlaceholder(viewModel.uiState.collectAsState().value.embeddedUiRatio)
    } else {
        val uiState by viewModel.uiState.collectAsState()

        val activity = LocalContext.current as Activity
        val view = LocalView.current

        val window = activity.window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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
            if (uiState.uiMode.systemInsetsVisible) {
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val isInPowerSaveMode = isInPowerSaveMode()
            LaunchedEffect(key1 = isInPowerSaveMode) {
                viewModel.deviceInPowerSaveMode = isInPowerSaveMode
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

        LaunchedEffect(key1 = uiState.brightness) {
            Log.d(TAG, "New Brightnes: ${uiState.brightness}")
            setScreenBrightness(
                uiState.brightness ?: defaultBrightness, activity
            )
        }

        if (uiState.uiMode == UIModeState.FULLSCREEN_VIDEO ||
            uiState.uiMode == UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI ||
            uiState.uiMode == UIModeState.FULLSCREEN_VIDEO_CHAPTER_SELECT ||
            uiState.uiMode == UIModeState.FULLSCREEN_VIDEO_STREAM_SELECT ||
            uiState.uiMode == UIModeState.EMBEDDED_VIDEO ||
            uiState.uiMode == UIModeState.EMBEDDED_VIDEO_CONTROLLER_UI ||
            uiState.uiMode == UIModeState.EMBEDDED_VIDEO_STREAM_SELECT ||
            uiState.uiMode == UIModeState.EMBEDDED_VIDEO_CHAPTER_SELECT
        ) {
            VideoPlayerUi(viewModel = viewModel, uiState = uiState)
        } else if (uiState.uiMode == UIModeState.FULLSCREEN_AUDIO ||
            uiState.uiMode == UIModeState.AUDIO_STREAM_SELECT ||
            uiState.uiMode == UIModeState.AUDIO_CHAPTER_SELECT
        ) {
            AudioPlayerUI(viewModel = viewModel, uiState = uiState)
        } else {
            LoadingPlaceholder(uiState.embeddedUiRatio)
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


@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
fun PlayerUIPreviewEmbeded() {
    VideoPlayerTheme {
        NewPlayerUI(viewModel = NewPlayerViewModelDummy())
    }
}