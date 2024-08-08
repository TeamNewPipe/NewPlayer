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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun CenterUI(
    modifier: Modifier,
    isPlaying: Boolean,
    isLoading: Boolean,
    play: () -> Unit,
    pause: () -> Unit,
    nextStream: () -> Unit,
    prevStream: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        if (!isLoading) {
            CenterControllButton(
                buttonModifier = Modifier.size(80.dp),
                iconModifier = Modifier.size(40.dp),
                icon = Icons.Filled.SkipPrevious,
                contentDescriptoion = stringResource(R.string.widget_description_previous_stream),
                onClick = prevStream
            )

            CenterControllButton(
                buttonModifier = Modifier.size(80.dp),
                iconModifier = Modifier.size(60.dp),
                icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescriptoion = stringResource(
                    if (isPlaying) R.string.widget_description_pause
                    else R.string.widget_description_play
                ),
                onClick = if (isPlaying) pause else play
            )

            CenterControllButton(
                buttonModifier = Modifier.size(80.dp),
                iconModifier = Modifier.size(40.dp),
                icon = Icons.Filled.SkipNext,
                contentDescriptoion = stringResource(R.string.widget_description_next_stream),
                onClick = nextStream
            )
        }
    }
}

@Composable
private fun CenterControllButton(
    buttonModifier: Modifier,
    iconModifier: Modifier,
    icon: ImageVector,
    contentDescriptoion: String?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        modifier = buttonModifier
    ) {
        Icon(
            imageVector = icon, modifier = iconModifier, contentDescription = contentDescriptoion
        )
    }
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerUICenterUIPreview() {
    VideoPlayerTheme {
        Surface(color = Color.Black) {
            CenterUI(
                modifier = Modifier,
                isPlaying = true,
                isLoading = false,
                play = {  },
                pause = {  },
                nextStream = { }) {
            }
        }
    }
}