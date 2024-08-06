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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

private const val TAG = "TouchUi"

private data class TouchedPosition(val x: Float, val y: Float) {
    operator fun minus(other: TouchedPosition) = TouchedPosition(this.x - other.x, this.y - other.y)
}

const val DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS: Long = 200
const val SEEK_ANIMATION_DURATION_IN_MS = 400
const val SEEK_ANIMATION_VISSIBLE_IN_MS = 500L
const val SEEK_ANIMATION_FADE_IN = 200
const val SEEK_ANIMATION_FADE_OUT = 500

@Composable
fun GestureUI(
    modifier: Modifier,
    hideUi: () -> Unit,
    showUi: () -> Unit,
    uiVissible: Boolean,
    fullscreen: Boolean,
    fastSeekSeconds: Int,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    embeddedDraggedDownBy: (Float) -> Unit,
    fastSeekBackward: () -> Unit,
    fastSeekForward: () -> Unit,
) {
    val defaultOnRegularTap = {
        if (uiVissible) {
            hideUi()
        } else {
            showUi()
        }
    }

    var showFastSeekBack by remember {
        mutableStateOf(false)
    }

    var showFastSeekForward by remember {
        mutableStateOf(false)
    }

    val composeScope = rememberCoroutineScope()

    val doForwardSeek = {
        showFastSeekForward = true
        composeScope.launch {
            delay(SEEK_ANIMATION_VISSIBLE_IN_MS)
            showFastSeekForward = false
        }
        fastSeekForward()
    }

    val doBackwardSeek = {
        showFastSeekBack = true
        composeScope.launch {
            delay(SEEK_ANIMATION_VISSIBLE_IN_MS)
            showFastSeekBack = false
        }
        fastSeekBackward()
    }


    if (fullscreen) {
        Row(modifier = modifier) {
            TouchSurface(
                modifier = Modifier
                    .weight(1f),
                onRegularTap = defaultOnRegularTap,
                onDoubleTab = doBackwardSeek
            ) {
                FadedAnimationForSeekFeedback(visible = showFastSeekBack) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FastSeekVisualFeedback(
                            seconds = fastSeekSeconds,
                            backwards = true,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
            TouchSurface(
                modifier = Modifier
                    .weight(1f),
                onRegularTap = defaultOnRegularTap,
                onMovement = { movement ->
                    if (0 < movement.y) {
                        switchToEmbeddedView()
                    }
                }
            )
            TouchSurface(
                modifier = Modifier
                    .weight(1f),
                onRegularTap = defaultOnRegularTap,
                onDoubleTab = doForwardSeek
            ) {
                FadedAnimationForSeekFeedback(visible = showFastSeekForward) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FastSeekVisualFeedback(
                            modifier = Modifier.align(Alignment.CenterStart),
                            seconds = fastSeekSeconds,
                            backwards = false
                        )
                    }
                }
            }
        }
    } else { // (!fullscreen)
        val handleDownwardMovement = { movement: TouchedPosition ->
            Log.d(TAG, "${movement.x}:${movement.y}")
            if (0 < movement.y) {
                embeddedDraggedDownBy(movement.y)
            } else {
                switchToFullscreen()
            }
        }

        Row(modifier = modifier) {
            TouchSurface(
                modifier = Modifier
                    .weight(1f),
                onDoubleTab = doBackwardSeek,
                onRegularTap = defaultOnRegularTap,
                onMovement = handleDownwardMovement
            ) {
                FadedAnimationForSeekFeedback(visible = showFastSeekBack) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FastSeekVisualFeedback(
                            modifier = Modifier.align(Alignment.Center),
                            seconds = fastSeekSeconds,
                            backwards = true
                        )
                    }
                }
            }
            TouchSurface(
                modifier = Modifier
                    .weight(1f),
                onDoubleTab = doForwardSeek,
                onRegularTap = defaultOnRegularTap,
                onMovement = handleDownwardMovement
            ) {
                FadedAnimationForSeekFeedback(visible = showFastSeekForward) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FastSeekVisualFeedback(
                            modifier = Modifier.align(Alignment.Center),
                            seconds = fastSeekSeconds,
                            backwards = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FadedAnimationForSeekFeedback(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(SEEK_ANIMATION_FADE_IN)),
        exit = fadeOut(animationSpec = tween(SEEK_ANIMATION_FADE_OUT))
    ) {
        content()
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun TouchSurface(
    modifier: Modifier,
    color: Color = Color.Transparent,
    onDoubleTab: () -> Unit = {},
    onRegularTap: () -> Unit = {},
    onMovement: (TouchedPosition) -> Unit = {},
    content: @Composable () -> Unit = {}
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
    var regularTabJob: Job? by remember {
        mutableStateOf(null)
    }

    val defaultActionDown = { event: MotionEvent ->
        lastTouchedPosition = TouchedPosition(event.x, event.y)
        moveOccured = false
        true
    }


    val defaultActionUp = { onDoubleTap: () -> Unit, onRegularTap: () -> Unit ->
        val currentTime = System.currentTimeMillis()
        if (!moveOccured) {
            val timeSinceLastTouch = currentTime - lastTouchTime
            if (timeSinceLastTouch <= DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS) {
                regularTabJob?.cancel()
                onDoubleTap()
            } else {
                regularTabJob = composableScope.launch {
                    delay(DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS)
                    onRegularTap()
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

    Box(modifier = modifier.pointerInteropFilter {
        when (it.action) {
            MotionEvent.ACTION_DOWN -> defaultActionDown(it)
            MotionEvent.ACTION_UP -> defaultActionUp(onDoubleTab, onRegularTap)
            MotionEvent.ACTION_MOVE -> handleMove(it, onMovement)

            else -> false
        }
    }) {
        content()
        Surface(color = color, modifier = Modifier.fillMaxSize()) {}
    }
}

@Composable
fun FastSeekVisualFeedback(modifier: Modifier = Modifier, seconds: Int, backwards: Boolean) {

    val contentDescription = String.format(
        if (backwards) {
            "Fast seeking backward by %d seconds."
            //stringResource(id = R.string.fast_seeking_backward)
        } else {
            "Fast seeking forward by %d seconds."
            //stringResource(id = R.string.fast_seeking_forward)
        }, seconds
    )

    val infiniteTransition = rememberInfiniteTransition()

    val animatedColor1 by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SEEK_ANIMATION_DURATION_IN_MS
                Color.White.copy(alpha = 1f) at 0 with LinearEasing
                Color.White.copy(alpha = 0f) at SEEK_ANIMATION_DURATION_IN_MS with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "Arrow1 animation"
    )

    val animatedColor2 by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SEEK_ANIMATION_DURATION_IN_MS
                Color.White.copy(alpha = 1f / 3f) at 0 with LinearEasing
                Color.White.copy(alpha = 0f) at SEEK_ANIMATION_DURATION_IN_MS / 3 with LinearEasing
                Color.White.copy(alpha = 1f) at SEEK_ANIMATION_DURATION_IN_MS / 3 + 1 with LinearEasing
                Color.White.copy(alpha = 2f / 3f) at SEEK_ANIMATION_DURATION_IN_MS with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "Arrow2 animation"
    )

    val animatedColor3 by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = SEEK_ANIMATION_DURATION_IN_MS
                Color.White.copy(alpha = 2f / 3f) at 0 with LinearEasing
                Color.White.copy(alpha = 0f) at 2 * SEEK_ANIMATION_DURATION_IN_MS / 3 with LinearEasing
                Color.White.copy(alpha = 1f) at 2 * SEEK_ANIMATION_DURATION_IN_MS / 3 + 1 with LinearEasing
                Color.White.copy(alpha = 2f / 3f) at SEEK_ANIMATION_DURATION_IN_MS with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "Arrow3 animation"
    )


    //val secondsString = stringResource(id = R.string.seconds)
    val secondsString = "Seconds"

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            SeekerIcon(
                backwards = backwards,
                description = contentDescription,
                color = if (backwards) animatedColor3 else animatedColor1
            )
            SeekerIcon(
                backwards = backwards,
                description = contentDescription,
                color = animatedColor2
            )
            SeekerIcon(
                backwards = backwards,
                description = contentDescription,
                color = if (backwards) animatedColor1 else animatedColor3
            )
        }
        Text(text = "$seconds $secondsString")
    }

}


@Composable
fun SeekerIcon(backwards: Boolean, description: String, color: Color) {
    Icon(
        modifier = if (backwards) {
            Modifier.scale(-1f, 1f)
        } else {
            Modifier
        },
        tint = color,
        painter = painterResource(id = R.drawable.ic_play_seek_triangle),
        contentDescription = description
    )
}

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun FullscreenGestureUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.Black) {
            GestureUI(
                modifier = Modifier,
                hideUi = { },
                showUi = { },
                uiVissible = false,
                fullscreen = true,
                fastSeekSeconds = 10,
                switchToFullscreen = { println("switch to fullscreen") },
                switchToEmbeddedView = { println("switch to embedded") },
                embeddedDraggedDownBy = { println("embedded dragged down") },
                fastSeekBackward = { println("fast seek backward") },
                fastSeekForward = { println("fast seek forward") })
        }
    }
}

@Preview(device = "spec:width=600px,height=400px,dpi=440,orientation=landscape")
@Composable
fun EmbeddedGestureUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.Black) {
            GestureUI(
                modifier = Modifier,
                hideUi = { },
                showUi = { },
                uiVissible = false,
                fullscreen = false,
                fastSeekSeconds = 10,
                switchToFullscreen = { println("switch to fullscreen") },
                switchToEmbeddedView = { println("switch to embedded") },
                embeddedDraggedDownBy = { println("embedded dragged down") },
                fastSeekBackward = { println("fast seek backward") },
                fastSeekForward = { println("fast seek forward") })
        }
    }
}

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun FastSeekVisualFeedbackPreviewBackwards() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.Black) {
            FastSeekVisualFeedback(seconds = 10, backwards = true)
        }
    }
}

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun FastSeekVisualFeedbackPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.Black) {
            FastSeekVisualFeedback(seconds = 10, backwards = false)
        }
    }
}