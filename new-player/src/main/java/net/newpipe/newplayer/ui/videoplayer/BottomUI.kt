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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.seeker.Seeker
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun BottomUI(
    modifier: Modifier,
    isFullscreen: Boolean,
    seekPosition: Float,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    seekPositionChanged: (Float) -> Unit,
    seekingFinished: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text("00:06:45")
        Seeker(
            Modifier.weight(1F),
            value = seekPosition,
            onValueChange = seekPositionChanged,
            onValueChangeFinished = seekingFinished
        )

        //Slider(value = 0.4F, onValueChange = {}, modifier = Modifier.weight(1F))

        Text("00:09:40")

        IconButton(onClick = if (isFullscreen) switchToEmbeddedView else switchToFullscreen) {
            Icon(
                imageVector = if (isFullscreen) Icons.Filled.FullscreenExit
                else Icons.Filled.Fullscreen,
                contentDescription = stringResource(R.string.widget_description_toggle_fullscreen)
            )
        }
    }
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerBottomUIPreview() {
    VideoPlayerTheme {
        Surface(color = Color.Black) {
            BottomUI(
                modifier = Modifier,
                isFullscreen = true,
                seekPosition = 0.4F,
                switchToFullscreen = {  },
                switchToEmbeddedView = {  },
                seekPositionChanged = {}
            ) {

            }
        }
    }
}