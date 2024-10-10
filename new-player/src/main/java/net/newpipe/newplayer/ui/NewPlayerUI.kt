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
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.utils.NewPlayerException
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.model.InternalNewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.audioplayer.AudioPlayerUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.VideoPlayerUi
import net.newpipe.newplayer.ui.common.LockScreenOrientation
import net.newpipe.newplayer.ui.common.getDefaultBrightness
import net.newpipe.newplayer.ui.common.isInPowerSaveMode
import net.newpipe.newplayer.ui.common.setScreenBrightness

private const val TAG = "VideoPlayerUI"

@OptIn(UnstableApi::class)
@Composable
fun NewPlayerUI(
    viewModel: NewPlayerViewModel?,
) {
    assert(viewModel is InternalNewPlayerViewModel?) {
        throw NewPlayerException("The view model given to NewPlayerUI must be of type InternalNewPlayerViewModel. This can not be implemented externally, so do not extend NewPlayerViewModel")
    }
    val viewModel = viewModel as InternalNewPlayerViewModel?

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

        when (uiState.uiMode) {

            UIModeState.FULLSCREEN_VIDEO,
            UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI,
            UIModeState.FULLSCREEN_VIDEO_CHAPTER_SELECT,
            UIModeState.FULLSCREEN_VIDEO_STREAM_SELECT,
            UIModeState.EMBEDDED_VIDEO,
            UIModeState.EMBEDDED_VIDEO_CONTROLLER_UI,
            UIModeState.EMBEDDED_VIDEO_STREAM_SELECT,
            UIModeState.EMBEDDED_VIDEO_CHAPTER_SELECT,
            UIModeState.PIP -> {
                VideoPlayerUi(viewModel = viewModel, uiState = uiState)
            }

            UIModeState.FULLSCREEN_AUDIO,
            UIModeState.EMBEDDED_AUDIO,
            UIModeState.AUDIO_STREAM_SELECT,
            UIModeState.AUDIO_CHAPTER_SELECT -> {
                val windowSize = currentWindowSize()
                AudioPlayerUI(
                    viewModel = viewModel, uiState = uiState,
                    isLandScape = windowSize.height < windowSize.width
                )
            }

            else -> {
                LoadingPlaceholder(uiState.embeddedUiRatio)
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
private fun PlayerUIPreviewEmbeded() {
    VideoPlayerTheme {
        NewPlayerUI(viewModel = NewPlayerViewModelDummy())
    }
}