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


package net.newpipe.newplayer.ui.videoplayer.gesture_ui

import android.app.Activity
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.getEmbeddedUiConfig

private const val TAG = "EmbeddedGestureUI"

@OptIn(UnstableApi::class)
@Composable
fun EmbeddedGestureUI(
    modifier: Modifier = Modifier, viewModel: NewPlayerViewModel, uiState: NewPlayerUIState
) {

    var downwardMovementMode by remember {
        mutableStateOf(false)
    }

    val embeddedUiConfig = if (LocalContext.current is Activity)
        getEmbeddedUiConfig(LocalContext.current as Activity)
    else
        EmbeddedUiConfig.DUMMY

    val handleMovement = { movement: TouchedPosition ->
        Log.d(TAG, "${movement.x}:${movement.y}")
        if (0 < movement.y) {
            viewModel.embeddedDraggedDown(movement.y)
            downwardMovementMode = true
        } else {

            // this check is there to allow a temporary move up in the downward gesture
            if (downwardMovementMode == false) {
                viewModel.changeUiMode(UIModeState.FULLSCREEN_VIDEO, embeddedUiConfig)
            } else {
                viewModel.embeddedDraggedDown(movement.y)
            }
        }
    }

    val handleUp = {
        downwardMovementMode = false
    }

    val defaultOnRegularTap = {
        if (uiState.uiMode.videoControllerUiVisible) {
            viewModel.changeUiMode(uiState.uiMode.getUiHiddenState(), null)
        } else {
            viewModel.changeUiMode(uiState.uiMode.getControllerUiVisibleState(), null)
        }
    }

    Row(modifier = modifier) {
        GestureSurface(
            modifier = Modifier.weight(1f),
            onRegularTap = defaultOnRegularTap,
            onMultiTap = {
                viewModel.fastSeek(-it)
            },
            onMultiTapFinished = viewModel::finishFastSeek,
            onMovement = handleMovement,
            onUp = handleUp
        ) {
            FadedAnimationForSeekFeedback(
                uiState.fastSeekSeconds, backwards = true
            ) { fastSeekSecondsToDisplay ->
                Box(modifier = Modifier.fillMaxSize()) {
                    FastSeekVisualFeedback(
                        modifier = Modifier.align(Alignment.Center),
                        seconds = -fastSeekSecondsToDisplay,
                        backwards = true
                    )
                }
            }
        }

        GestureSurface(
            modifier = Modifier.weight(1f),
            onRegularTap = defaultOnRegularTap,
            onMultiTap = { count ->
                if (count == 1) {
                    if (uiState.playing) {
                        viewModel.pause()
                        viewModel.changeUiMode(uiState.uiMode.getControllerUiVisibleState(), null)
                    } else {
                        viewModel.play()
                    }
                }
            },
            onMultiTapFinished = viewModel::finishFastSeek,
            onMovement = handleMovement,
            onUp = handleUp
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }

        GestureSurface(
            modifier = Modifier.weight(1f),
            onRegularTap = defaultOnRegularTap,
            onMovement = handleMovement,
            onMultiTap = viewModel::fastSeek,
            onMultiTapFinished = viewModel::finishFastSeek,
            onUp = handleUp
        ) {
            FadedAnimationForSeekFeedback(uiState.fastSeekSeconds) { fastSeekSecondsToDisplay ->
                Box(modifier = Modifier.fillMaxSize()) {
                    FastSeekVisualFeedback(
                        modifier = Modifier.align(Alignment.Center),
                        seconds = fastSeekSecondsToDisplay,
                        backwards = false
                    )
                }
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Preview(device = "spec:width=600px,height=400px,dpi=440,orientation=landscape")
@Composable
fun EmbeddedGestureUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.DarkGray) {
            EmbeddedGestureUI(
                modifier = Modifier,
                viewModel = object : NewPlayerViewModelDummy() {
                    override fun embeddedDraggedDown(offset: Float) {
                        println("embedded view dragged down by $offset")
                    }

                    override fun fastSeek(steps: Int) {
                        println("fast seek by $steps steps")
                    }
                },
                uiState = NewPlayerUIState.DEFAULT,
            )
        }
    }
}

