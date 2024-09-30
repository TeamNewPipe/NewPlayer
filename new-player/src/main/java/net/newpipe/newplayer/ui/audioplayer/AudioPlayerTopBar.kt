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



package net.newpipe.newplayer.ui.audioplayer

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.ui.common.getEmbeddedUiConfig

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerTopBar(
    modifier: Modifier = Modifier,
    viewModel: NewPlayerViewModel,
    uiState: NewPlayerUIState
) {
    val embeddedUiConfig =
        if (LocalContext.current is Activity)
            getEmbeddedUiConfig(activity = LocalContext.current as Activity)
        else EmbeddedUiConfig.DUMMY
    TopAppBar(modifier = modifier,
        title = { }, actions = {
            AnimatedVisibility(visible = uiState.chapters.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.changeUiMode(UIModeState.AUDIO_CHAPTER_SELECT, embeddedUiConfig)
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = stringResource(R.string.widget_description_chapter_selection)
                    )
                }
            }
            AnimatedVisibility(visible = 1 < uiState.playList.size) {
                IconButton(
                    onClick = {
                        viewModel.changeUiMode(UIModeState.AUDIO_STREAM_SELECT, embeddedUiConfig)
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.widget_descriptoin_playlist_item_selection)
                    )
                }

            }
        })
}