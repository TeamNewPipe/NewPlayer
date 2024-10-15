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


package net.newpipe.newplayer.ui.common

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.RepeatMode
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel

@OptIn(UnstableApi::class)
@Composable
internal fun RepeatModeButton(viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState) {
    IconButton(
        onClick = viewModel::cycleRepeatMode
    ) {
        when (uiState.repeatMode) {
            RepeatMode.DO_NOT_REPEAT -> Icon(
                imageVector = Icons.Filled.Repeat,
                contentDescription = stringResource(R.string.repeat_mode_no_repeat)
            )

            RepeatMode.REPEAT_ALL -> Icon(
                imageVector = Icons.Filled.RepeatOn,
                contentDescription = stringResource(R.string.repeat_mode_repeat_all)
            )

            RepeatMode.REPEAT_ONE -> Icon(
                imageVector = Icons.Filled.RepeatOneOn,
                contentDescription = stringResource(R.string.repeat_mode_repeat_all)
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
internal fun ShuffleModeButton(viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState) {
    IconButton(
        onClick = viewModel::toggleShuffle
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
}