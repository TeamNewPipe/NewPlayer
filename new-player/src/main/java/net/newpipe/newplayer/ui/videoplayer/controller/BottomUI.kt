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

package net.newpipe.newplayer.ui.videoplayer.controller

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.common.NewPlayerSeeker
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.common.getEmbeddedUiConfig
import net.newpipe.newplayer.ui.common.getLocale
import net.newpipe.newplayer.ui.common.getTimeStringFromMs


private const val TAG = "BottomUI"

@OptIn(UnstableApi::class)
@Composable
fun BottomUI(
    modifier: Modifier, viewModel: NewPlayerViewModel, uiState: NewPlayerUIState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        val locale = getLocale()!!
        Text(getTimeStringFromMs(uiState.playbackPositionInMs, getLocale() ?: locale))

        NewPlayerSeeker(
            modifier = Modifier
                .weight(1F)
                .padding(start = 4.dp, end = 4.dp),
            viewModel = viewModel,
            uiState = uiState
        )

        Text(getTimeStringFromMs(uiState.durationInMs, getLocale() ?: locale))

        val embeddedUiConfig = when (LocalContext.current) {
            is Activity -> getEmbeddedUiConfig(LocalContext.current as Activity)
            else -> EmbeddedUiConfig.DUMMY
        }

        IconButton(
            onClick = if (uiState.uiMode.fullscreen) {
                {
                    viewModel.changeUiMode(UIModeState.EMBEDDED_VIDEO, embeddedUiConfig)
                }
            } else {
                {
                    viewModel.changeUiMode(UIModeState.FULLSCREEN_VIDEO, embeddedUiConfig)
                }
            }
        ) {
            Icon(
                imageVector = if (uiState.uiMode.fullscreen) Icons.Filled.FullscreenExit
                else Icons.Filled.Fullscreen,
                contentDescription = stringResource(R.string.widget_description_toggle_fullscreen)
            )
        }
    }
}


///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerBottomUIPreview() {
    VideoPlayerTheme {
        Surface(color = Color.Black) {
            BottomUI(
                modifier = Modifier,
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI,
                    seekerPosition = 0.0f,
                    playbackPositionInMs = 3 * 60 * 1000,
                    bufferedPercentage = 0.4f
                ),
            )
        }
    }
}