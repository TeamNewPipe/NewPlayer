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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.EmbeddedGestureUI
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.FastSeekVisualFeedback
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.FullscreenGestureUI
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.FullscreenGestureUIPreview
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.TouchSurface
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.TouchedPosition

private const val TAG = "TouchUi"


const val DELAY_UNTIL_SHOWING_UI_AFTER_TOUCH_IN_MS: Long = 200
const val SEEK_ANIMATION_DURATION_IN_MS = 400
const val FAST_SEEKMODE_DURATION = 500L
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
    brightnes: Float,
    soundVolume: Float,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    embeddedDraggedDownBy: (Float) -> Unit,
    fastSeek: (Int) -> Unit,
    fastSeekFinished: () -> Unit,
    volumeChange: (Float) -> Unit,
    brightnesChange: (Float) -> Unit,
) {
    val defaultOnRegularTap = {
        if (uiVissible) {
            hideUi()
        } else {
            showUi()
        }
    }

    if (fullscreen) {
        FullscreenGestureUI(
            uiVissible = uiVissible,
            fastSeekSeconds = fastSeekSeconds,
            hideUi = hideUi,
            showUi = showUi,
            fastSeek = fastSeek,
            brightnes = brightnes,
            volume = soundVolume,
            switchToEmbeddedView = switchToEmbeddedView,
            fastSeekFinished = fastSeekFinished,
            volumeChange = volumeChange,
            brightnesChange = brightnesChange)
    } else {
        EmbeddedGestureUI(
            fastSeekSeconds = fastSeekSeconds,
            uiVissible = uiVissible,
            switchToFullscreen = switchToFullscreen,
            embeddedDraggedDownBy = embeddedDraggedDownBy,
            fastSeek = fastSeek,
            fastSeekFinished = fastSeekFinished,
            hideUi = hideUi,
            showUi = showUi)
    }
}