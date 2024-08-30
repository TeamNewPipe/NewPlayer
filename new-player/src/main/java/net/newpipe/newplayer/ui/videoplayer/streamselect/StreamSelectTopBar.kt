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

package net.newpipe.newplayer.ui.videoplayer.streamselect

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player
import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.playerInternals.getPlaylistDurationInS
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamSelectTopBar(
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel,
    uiState: VideoPlayerUIState
) {

    TopAppBar(modifier = modifier,
        colors = topAppBarColors(containerColor = Color.Transparent),
        title = {
            val locale = getLocale()!!
            val duration = getPlaylistDurationInS(uiState.playList).toLong() * 1000
            val durationString = getTimeStringFromMs(timeSpanInMs = duration, locale)
            Text(
                text = "00:00/$durationString"
            )
        }, actions = {
            IconButton(
                onClick = {
                    viewModel.setRepeatmode(
                        when (uiState.repeatMode) {
                            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                            else -> throw NewPlayerException("Unknown repeat mode: ${uiState.repeatMode}")
                        }
                    )
                }
            ) {
                when (uiState.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Icon(
                        imageVector = Icons.Filled.Repeat,
                        contentDescription = stringResource(R.string.repeat_mode_no_repeat)
                    )

                    Player.REPEAT_MODE_ALL -> Icon(
                        imageVector = Icons.Filled.RepeatOn,
                        contentDescription = stringResource(R.string.repeat_mode_repeat_all)
                    )

                    Player.REPEAT_MODE_ONE -> Icon(
                        imageVector = Icons.Filled.RepeatOneOn,
                        contentDescription = stringResource(R.string.repeat_mode_repeat_all)
                    )

                    else -> throw NewPlayerException("Unknown repeat mode: ${uiState.repeatMode}")
                }
            }

            IconButton(
                onClick = {
                    viewModel.setSuffleEnabled(!uiState.shuffleEnabled)
                }
            ) {
                if (uiState.shuffleEnabled) {
                    Icon(
                        imageVector = Icons.Filled.ShuffleOn,
                        contentDescription = stringResource(R.string.shuffle_off)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Shuffle,
                        contentDescription = stringResource(R.string.shuffle_on)
                    )
                }
            }

            IconButton(
                onClick = viewModel::onStorePlaylist
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = stringResource(R.string.store_playlist)
                )
            }

            IconButton(
                onClick = viewModel::closeStreamSelection
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_stream_selection)
                )
            }
        })
}


@Preview(device = "spec:width=1080px,height=150px,dpi=440,orientation=landscape")
@Composable
fun StreamSelectTopBarPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
            StreamSelectTopBar(
                modifier = Modifier.fillMaxSize(),
                viewModel = VideoPlayerViewModelDummy(),
                uiState = VideoPlayerUIState.DEFAULT
            )
        }
    }
}