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

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
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
import net.newpipe.newplayer.ui.videoplayer.DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun GestureSurface(
    modifier: Modifier,
    color: Color = Color.Transparent,
    onMultiTap: (Int) -> Unit = {},
    onMultiTapFinished: () -> Unit = {},
    onRegularTap: () -> Unit = {},
    onUp: () -> Unit = {},
    onMovement: (TouchedPosition) -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    var moveOccured by remember {
        mutableStateOf(false)
    }

    var lastTouchedPosition by remember {
        mutableStateOf(TouchedPosition(0f, 0f))
    }

    var lastFingerUpTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    val composableScope = rememberCoroutineScope()
    var regularTabJob: Job? by remember {
        mutableStateOf(null)
    }

    val defaultActionDown = { event: MotionEvent ->
        lastTouchedPosition = TouchedPosition(event.x, event.y)
        moveOccured = false
        true
    }

    var multitapAmount:Int by remember {
        mutableStateOf(0)
    }

    var cancelMultitapJob: Job? by remember {
        mutableStateOf(null)
    }

    val defaultActionUp = { onMultiTap: (Int) -> Unit, onRegularTap: () -> Unit ->
        onUp()
        val currentTime = System.currentTimeMillis()
        if (!moveOccured) {
            val timeSinceLastTouch = currentTime - lastFingerUpTime
            if (timeSinceLastTouch <= DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS) {
                regularTabJob?.cancel()
                cancelMultitapJob?.cancel()
                multitapAmount++
                onMultiTap(multitapAmount)
                cancelMultitapJob = composableScope.launch {
                    delay(DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS)
                    multitapAmount = 0
                    onMultiTapFinished()
                }
            } else {
                regularTabJob = composableScope.launch {
                    delay(DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS)
                    onRegularTap()
                }
            }
        }
        moveOccured = false
        lastFingerUpTime = currentTime
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

    Box(modifier = modifier.pointerInteropFilter {
        when (it.action) {
            MotionEvent.ACTION_DOWN -> defaultActionDown(it)
            MotionEvent.ACTION_UP -> defaultActionUp(onMultiTap, onRegularTap)
            MotionEvent.ACTION_MOVE -> handleMove(it, onMovement)

            else -> false
        }
    }) {
        content()
        Surface(color = color, modifier = Modifier.fillMaxSize()) {}
    }
}