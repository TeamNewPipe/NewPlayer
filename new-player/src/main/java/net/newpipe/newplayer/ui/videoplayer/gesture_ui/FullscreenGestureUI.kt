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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.FAST_SEEK_MODE_DURATION

private enum class IndicatorMode {
    NONE,
    VOLUME_INDICATOR_VISSIBLE,
    BRIGHTNESS_INDICATOR_VISSIBLE
}

@Composable
fun FullscreenGestureUI(
    modifier: Modifier = Modifier,
    uiVissible: Boolean,
    fastSeekSeconds: Int,
    volume: Float,
    brightnes: Float,
    hideUi: () -> Unit,
    showUi: () -> Unit,
    fastSeek: (Int) -> Unit,
    fastSeekFinished: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    volumeChange: (Float) -> Unit,
    brightnesChange: (Float) -> Unit
) {

    var heightPx by remember {
        mutableStateOf(0f)
    }

    var indicatorMode by remember {
        mutableStateOf(IndicatorMode.NONE)
    }

    val defaultOnRegularTap = {

        if (uiVissible) {
            hideUi()
        } else {
            showUi()
        }
    }

    Box(modifier = modifier.onGloballyPositioned { coordinates ->
        heightPx = coordinates.size.height.toFloat()
    }) {
        Row {
            GestureSurface(
                modifier = Modifier
                    .weight(1f),
                multiTapTimeoutInMs = FAST_SEEK_MODE_DURATION,
                onRegularTap = defaultOnRegularTap,
                onMultiTap = {
                    println("multitap ${-it}")
                    fastSeek(-it)
                },
                onMultiTapFinished = fastSeekFinished,
                onUp = {
                    indicatorMode = IndicatorMode.NONE
                },
                onMovement = { change ->
                    if (indicatorMode == IndicatorMode.NONE
                        || indicatorMode == IndicatorMode.BRIGHTNESS_INDICATOR_VISSIBLE
                    ) {
                        indicatorMode = IndicatorMode.BRIGHTNESS_INDICATOR_VISSIBLE

                        if (heightPx != 0f) {
                            brightnesChange(-change.y / heightPx)
                        }
                    }
                }
            ) {
                FadedAnimationForSeekFeedback(
                    fastSeekSeconds,
                    backwards = true
                ) { fastSeekSecondsToDisplay ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        FastSeekVisualFeedback(
                            seconds = -fastSeekSecondsToDisplay,
                            backwards = true,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
            GestureSurface(
                modifier = Modifier
                    .weight(1f),
                onRegularTap = defaultOnRegularTap,
                multiTapTimeoutInMs = FAST_SEEK_MODE_DURATION,
                onMovement = { movement ->
                    if (0 < movement.y) {
                        switchToEmbeddedView()
                    }
                }
            )
            GestureSurface(
                modifier = Modifier
                    .weight(1f),
                onRegularTap = defaultOnRegularTap,
                multiTapTimeoutInMs = FAST_SEEK_MODE_DURATION,
                onMultiTap = fastSeek,
                onMultiTapFinished = fastSeekFinished,
                onUp = {
                    indicatorMode = IndicatorMode.NONE
                },
                onMovement = { change ->
                    if (indicatorMode == IndicatorMode.NONE
                        || indicatorMode == IndicatorMode.VOLUME_INDICATOR_VISSIBLE
                    ) {
                        indicatorMode = IndicatorMode.VOLUME_INDICATOR_VISSIBLE
                        if (heightPx != 0f) {
                            volumeChange(-change.y / heightPx)
                        }
                    }
                }
            ) {
                FadedAnimationForSeekFeedback(fastSeekSeconds) { fastSeekSecondsToDisplay ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        FastSeekVisualFeedback(
                            modifier = Modifier.align(Alignment.CenterStart),
                            seconds = fastSeekSecondsToDisplay,
                            backwards = false
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = indicatorMode == IndicatorMode.VOLUME_INDICATOR_VISSIBLE,
            enter = scaleIn(initialScale = 0.95f, animationSpec = tween(100)),
            exit = scaleOut(targetScale = 0.95f, animationSpec = tween(100))
        ) {
            VolumeCircle(volumeFraction = volume)
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = indicatorMode == IndicatorMode.BRIGHTNESS_INDICATOR_VISSIBLE,
            enter = scaleIn(initialScale = 0.95f, animationSpec = tween(100)),
            exit = scaleOut(targetScale = 0.95f, animationSpec = tween(100))
        ) {
            VolumeCircle(
                volumeFraction = brightnes,
                modifier = Modifier.align(Alignment.Center),
                isBrightness = true
            )
        }
    }
}


@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun FullscreenGestureUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.DarkGray) {
            FullscreenGestureUI(
                modifier = Modifier,
                hideUi = { },
                showUi = { },
                uiVissible = false,
                fastSeekSeconds = 0,
                volume = 0f,
                brightnes = 0f,
                fastSeek = { println("fast seek by $it steps") },
                fastSeekFinished = {},
                switchToEmbeddedView = {},
                brightnesChange = {},
                volumeChange = {})
        }
    }
}

@Preview(device = "spec:parent=pixel_8,orientation=landscape")
@Composable
fun FullscreenGestureUIPreviewInteractive() {

    var seekSeconds by remember {
        mutableStateOf(0)
    }

    var brightnesValue by remember {
        mutableStateOf(0f)
    }

    var soundVolume by remember {
        mutableStateOf(0f)
    }

    VideoPlayerTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = Color.Gray) {
            FullscreenGestureUI(
                modifier = Modifier,
                hideUi = { },
                showUi = { },
                uiVissible = false,
                fastSeekSeconds = seekSeconds,
                volume = soundVolume,
                brightnes = brightnesValue,
                fastSeek = { seekSeconds = it * 10 },
                fastSeekFinished = {
                    seekSeconds = 0
                },
                switchToEmbeddedView = {},
                brightnesChange = {
                    brightnesValue = (brightnesValue + it).coerceIn(0f, 1f)
                },
                volumeChange = {
                    soundVolume = (soundVolume + it).coerceIn(0f, 1f)
                })
        }
    }
}
