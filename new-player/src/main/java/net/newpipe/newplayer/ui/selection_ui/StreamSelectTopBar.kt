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

package net.newpipe.newplayer.ui.selection_ui

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.common.RepeatModeButton
import net.newpipe.newplayer.ui.common.ShuffleModeButton
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.getEmbeddedUiConfig
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getPlaylistDurationInMS
import net.newpipe.newplayer.utils.getTimeStringFromMs

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamSelectTopBar(
    modifier: Modifier = Modifier,
    viewModel: NewPlayerViewModel,
    uiState: NewPlayerUIState
) {

    val embeddedUiConfig =
        if (LocalContext.current is Activity)
            getEmbeddedUiConfig(activity = LocalContext.current as Activity)
        else
            EmbeddedUiConfig.DUMMY

    TopAppBar(modifier = modifier,
        colors = topAppBarColors(containerColor = Color.Transparent),
        title = {
            val locale = getLocale()!!
            val duration = getPlaylistDurationInMS(uiState.playList)
            val durationString = getTimeStringFromMs(timeSpanInMs = duration, locale)
            val playbackPositionString = getTimeStringFromMs(
                timeSpanInMs = uiState.playbackPositionInPlaylistMs, locale = locale
            )
            Text(
                text = "$playbackPositionString/$durationString",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }, actions = {
            RepeatModeButton(viewModel = viewModel, uiState = uiState)

            ShuffleModeButton(viewModel = viewModel, uiState = uiState)

            IconButton(
                onClick = viewModel::onStorePlaylist
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = stringResource(R.string.store_playlist)
                )
            }

            IconButton(
                onClick = {
                    viewModel.changeUiMode(
                        uiState.uiMode.getNextModeWhenBackPressed() ?: uiState.uiMode,
                        embeddedUiConfig
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_stream_selection)
                )
            }
        })
}


@androidx.annotation.OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=150px,dpi=440,orientation=landscape")
@Composable
fun StreamSelectTopBarPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
            StreamSelectTopBar(
                modifier = Modifier.fillMaxSize(),
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DEFAULT
            )
        }
    }
}