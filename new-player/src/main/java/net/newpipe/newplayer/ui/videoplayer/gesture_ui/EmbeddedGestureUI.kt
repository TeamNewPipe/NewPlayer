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

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

private const val TAG = "EmbeddedGestureUI"

@Composable
fun EmbeddedGestureUI(
    modifier: Modifier = Modifier, viewModel: VideoPlayerViewModel, uiState: VideoPlayerUIState
) {

    val handleDownwardMovement = { movement: TouchedPosition ->
        Log.d(TAG, "${movement.x}:${movement.y}")
        if (0 < movement.y) {
            viewModel.embeddedDraggedDown(movement.y)
        } else {
            viewModel.switchToFullscreen()
        }
    }

    val defaultOnRegularTap = {
        if (uiState.uiMode.controllerUiVisible) {
            viewModel.hideUi()
        } else {
            viewModel.showUi()
        }
    }

    Row(modifier = modifier) {
        GestureSurface(
            modifier = Modifier.weight(1f), onRegularTap = defaultOnRegularTap, onMultiTap = {
                viewModel.fastSeek(-it)
            }, onMultiTapFinished = viewModel::finishFastSeek, onMovement = handleDownwardMovement
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
            onMovement = handleDownwardMovement,
            onMultiTap = viewModel::fastSeek,
            onMultiTapFinished = viewModel::finishFastSeek
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


@Preview(device = "spec:width=600px,height=400px,dpi=440,orientation=landscape")
@Composable
fun EmbeddedGestureUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.DarkGray) {
            EmbeddedGestureUI(
                modifier = Modifier,
                viewModel = object : VideoPlayerViewModelDummy() {
                    override fun switchToEmbeddedView() {
                        println("switch to fullscreen")
                    }

                    override fun embeddedDraggedDown(offset: Float) {
                        println("embedded view dragged down by $offset")
                    }

                    override fun fastSeek(steps: Int) {
                        println("fast seek by $steps steps")
                    }
                },
                uiState = VideoPlayerUIState.DEFAULT,
            )
        }
    }
}

