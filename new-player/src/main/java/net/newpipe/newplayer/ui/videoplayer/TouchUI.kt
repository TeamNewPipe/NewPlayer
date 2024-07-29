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

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TouchUi(
    modifier: Modifier,
    hideUi: () -> Unit,
    showUi: () -> Unit,
    uiVissible: Boolean,
    fullscreen: Boolean,
) {
    Box(modifier = Modifier
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (uiVissible) {
                        hideUi()
                    } else {
                        showUi()
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    true
                }

                else -> false
            }
        }) {
        Surface(color = Color.Transparent, modifier = Modifier.fillMaxSize()) {

        }
    }
}
