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

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "TouchUi"

private data class TouchedPosition(val x: Float, val y: Float) {
    operator fun minus(other: TouchedPosition) = TouchedPosition(this.x - other.x, this.y - other.y)
}

const val DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS:Long = 150

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TouchUi(
    modifier: Modifier,
    hideUi: () -> Unit,
    showUi: () -> Unit,
    uiVissible: Boolean,
    fullscreen: Boolean,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    embeddedDraggedDownBy: (Float) -> Unit,
    fastSeekBackward: () -> Unit,
    fastSeekForward: () -> Unit,
) {

    var moveOccured by remember {
        mutableStateOf(false)
    }

    var lastTouchedPosition by remember {
        mutableStateOf(TouchedPosition(0f, 0f))
    }

    var lastTouchTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    val composableScope = rememberCoroutineScope()
    var showUiJob: Job? by remember{
        mutableStateOf(null)
    }

    val defaultActionDown = { event: MotionEvent ->
        lastTouchedPosition = TouchedPosition(event.x, event.y)
        moveOccured = false
        true
    }

    val defaultActionUp = { onDoubleTap: () -> Unit ->
        val currentTime = System.currentTimeMillis()
        if (!moveOccured) {
            val timeSinceLastTouch = currentTime - lastTouchTime
            if(timeSinceLastTouch <= DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS) {
                showUiJob?.cancel()
                onDoubleTap()
            } else {
                if (uiVissible) {
                    showUiJob = composableScope.launch {
                        delay(DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS)
                        hideUi()
                    }

                } else {
                    showUiJob = composableScope.launch {
                        delay(DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS)
                        showUi()
                    }
                }
            }
        }
        moveOccured = false
        lastTouchTime = currentTime
        true
    }

    val handleMove = { event: MotionEvent, lambda: (movement: TouchedPosition) -> Unit ->
        val currentTouchedPosition = TouchedPosition(event.x, event.y)
        val movement = currentTouchedPosition - lastTouchedPosition
        lastTouchedPosition = currentTouchedPosition
        moveOccured = true
        lambda(movement)
        true
    }

    if (fullscreen) {
        Row(modifier = modifier) {
            TouchSurface(modifier = Modifier.weight(1f)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> defaultActionDown(it)
                        MotionEvent.ACTION_UP -> defaultActionUp(fastSeekBackward)
                        MotionEvent.ACTION_MOVE -> handleMove(it) { movement ->

                        }

                        else -> false
                    }

                })
            TouchSurface(modifier = Modifier
                .weight(1f)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> defaultActionDown(it)
                        MotionEvent.ACTION_UP -> defaultActionUp({})
                        MotionEvent.ACTION_MOVE -> handleMove(it) { movement ->
                            if (0 < movement.y) {
                                switchToEmbeddedView()
                            }
                        }

                        else -> false
                    }
                })
            TouchSurface(modifier = Modifier.weight(1f)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> defaultActionDown(it)
                        MotionEvent.ACTION_UP -> defaultActionUp(fastSeekForward)
                        MotionEvent.ACTION_MOVE -> handleMove(it) { movement ->

                        }

                        else -> false
                    }
                })
        }
    } else {
        Row(modifier = modifier) {
            TouchSurface(modifier = Modifier
                .weight(1f)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> defaultActionDown(it)
                        MotionEvent.ACTION_UP -> defaultActionUp(fastSeekBackward)
                        MotionEvent.ACTION_MOVE -> handleMove(it) { movement ->
                            Log.d(TAG, "${it.x}:${it.y}")
                            if (0 < movement.y) {
                                embeddedDraggedDownBy(movement.y)
                            } else {
                                switchToFullscreen()
                            }
                        }

                        else -> false
                    }
                })
            TouchSurface(modifier = Modifier
                .weight(1f)
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> defaultActionDown(it)
                        MotionEvent.ACTION_UP -> defaultActionUp(fastSeekForward)
                        MotionEvent.ACTION_MOVE -> handleMove(it) { movement ->
                            if (0 < movement.y) {
                                embeddedDraggedDownBy(movement.y)
                            } else {
                                switchToFullscreen()
                            }
                        }

                        else -> false
                    }
                })
        }
    }
}

@Composable
private fun TouchSurface(modifier: Modifier, color: Color = Color.Transparent) {
    Box(modifier = modifier) {
        Surface(color = color, modifier = Modifier.fillMaxSize()) {}
    }
}