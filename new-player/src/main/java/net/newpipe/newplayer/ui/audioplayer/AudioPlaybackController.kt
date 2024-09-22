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

package net.newpipe.newplayer.ui.audioplayer;

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.common.RepeatModeButton
import net.newpipe.newplayer.ui.common.ShuffleModeButton
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun AudioPlaybackController(viewModel: NewPlayerViewModel, uiState: NewPlayerUIState) {
    Row(modifier = Modifier.background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically) {
        ShuffleModeButton(viewModel = viewModel, uiState = uiState)

        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
            androidx.compose.animation.AnimatedVisibility(
                uiState.currentPlaylistItemIndex != 0,
                enter = fadeIn(animationSpec = tween(400)),
                exit = fadeOut(animationSpec = tween(400))

            ) {
                Button(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    onClick = {},
                    colors = lightAudioControlButtonColorScheme()
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = stringResource(R.string.widget_description_previous_stream)
                    )
                }
            }
        }

        Button(
            modifier = Modifier.size(80.dp),
            onClick = if (uiState.playing) viewModel::pause else viewModel::play,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (uiState.playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    if (uiState.playing) R.string.widget_description_pause
                    else R.string.widget_description_play
                )
            )
        }

        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
            androidx.compose.animation.AnimatedVisibility(
                uiState.currentPlaylistItemIndex < uiState.playList.size - 1,
                enter = fadeIn(animationSpec = tween(400)),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    onClick = {},
                    colors = lightAudioControlButtonColorScheme()
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = stringResource(R.string.widget_description_next_stream)
                    )
                }
            }
        }

        RepeatModeButton(viewModel = viewModel, uiState = uiState)
    }
}


@androidx.annotation.OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6")
@Composable
fun AudioPlayerControllerPreview() {
    VideoPlayerTheme {
        AudioPlaybackController(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY
        )
    }
}