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
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.FAST_SEEK_MODE_DURATION

private const val TAG = "EmbeddedGestureUI"

@Composable
fun EmbeddedGestureUI(
    modifier: Modifier = Modifier,
    fastSeekSeconds: Int,
    uiVissible: Boolean,
    switchToFullscreen: () -> Unit,
    embeddedDraggedDownBy: (Float) -> Unit,
    fastSeek: (Int) -> Unit,
    fastSeekFinished: () -> Unit,
    hideUi: () -> Unit,
    showUi: () -> Unit
) {

    val handleDownwardMovement = { movement: TouchedPosition ->
        Log.d(TAG, "${movement.x}:${movement.y}")
        if (0 < movement.y) {
            embeddedDraggedDownBy(movement.y)
        } else {
            switchToFullscreen()
        }
    }

    val defaultOnRegularTap = {
        if (uiVissible) {
            hideUi()
        } else {
            showUi()
        }
    }

    Row(modifier = modifier) {
        GestureSurface(
            modifier = Modifier
                .weight(1f),
            multiTapTimeoutInMs = FAST_SEEK_MODE_DURATION,
            onRegularTap = defaultOnRegularTap,
            onMultiTap = {
                fastSeek(-it)
            },
            onMultiTapFinished = fastSeekFinished,
            onMovement = handleDownwardMovement
        ) {
            FadedAnimationForSeekFeedback(
                fastSeekSeconds,
                backwards = true
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
            modifier = Modifier
                .weight(1f),
            multiTapTimeoutInMs = FAST_SEEK_MODE_DURATION,
            onRegularTap = defaultOnRegularTap,
            onMovement = handleDownwardMovement,
            onMultiTap = fastSeek,
            onMultiTapFinished = fastSeekFinished
        ) {
            FadedAnimationForSeekFeedback(fastSeekSeconds) { fastSeekSecondsToDisplay ->
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
                hideUi = { },
                showUi = { },
                uiVissible = false,
                fastSeekSeconds = 0,
                switchToFullscreen = { println("switch to fullscreen") },
                embeddedDraggedDownBy = { println("embedded dragged down") },
                fastSeek = { println("Fast seek by $it steps") },
                fastSeekFinished = {})
        }
    }
}

