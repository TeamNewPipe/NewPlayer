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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun FadedAnimationForSeekFeedback(
    fastSeekSeconds: Int,
    backwards: Boolean = false,
    content: @Composable (fastSeekSecondsToDisplay:Int) -> Unit
) {

    var lastSecondsValue by remember {
        mutableStateOf(0)
    }

    val vissible = if (backwards) {
        fastSeekSeconds < 0
    } else {
        0 < fastSeekSeconds
    }

    val disapearEmediatly = if (backwards) {
        0 < fastSeekSeconds
    } else {
        fastSeekSeconds < 0
    }

    val valueToDisplay = if(vissible) {
        lastSecondsValue = fastSeekSeconds
        fastSeekSeconds
    } else {
        lastSecondsValue
    }

    if (!disapearEmediatly) {
        AnimatedVisibility(
            visible = vissible,
            enter = fadeIn(animationSpec = tween(SEEK_ANIMATION_FADE_IN)),
            exit = fadeOut(
                animationSpec = tween(SEEK_ANIMATION_FADE_OUT)
            )
        ) {
            content(valueToDisplay)
        }
    }
}

