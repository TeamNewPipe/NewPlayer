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

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy

@OptIn(UnstableApi::class)
@Composable
fun AudioBottomUI(viewModel: NewPlayerViewModel, uiState: NewPlayerUIState) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { /*TODO*/ },
                colors = lightAudioControlButtonColorScheme()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArtTrack,
                    contentDescription = stringResource(
                        id = R.string.details_view_button_description
                    )
                )
            }
            Button(onClick = { /*TODO*/ }, colors = lightAudioControlButtonColorScheme()) {
                Icon(
                    imageVector = Icons.Filled.LiveTv,
                    contentDescription = stringResource(
                        id = R.string.fullscreen_button_description
                    )
                )
            }
            Button(onClick = { /*TODO*/ }, colors = lightAudioControlButtonColorScheme()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = stringResource(
                        id = R.string.chapter
                    )
                )
            }
            Button(onClick = { /*TODO*/ }, colors = lightAudioControlButtonColorScheme()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(
                        id = R.string.widget_descriptoin_playlist_item_selection
                    )
                )
            }
        }
        Menu()
    }
}

@Composable
private fun Menu() {
    var showMenu: Boolean by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = {
            showMenu = true
        }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.menu_item_more_settings)
            )
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_playback_speed)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = stringResource(R.string.menu_item_playback_speed)
                    )
                },
                onClick = { /*TODO*/ showMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_language)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = stringResource(R.string.menu_item_language)
                    )
                },
                onClick = { /*TODO*/ showMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_share_timestamp)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.menu_item_share_timestamp)
                    )
                },
                onClick = { /*TODO*/ showMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_open_in_browser)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = stringResource(R.string.menu_item_open_in_browser)
                    )
                },
                onClick = { /*TODO*/ showMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.pip_button_description)) }, onClick = { /*TODO*/ }, leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.PictureInPicture,
                    contentDescription = stringResource(
                        id = R.string.pip_button_description
                    )
                )
            })
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6")
@Composable
fun AudioBottomUIPreview() {
    Box(modifier = Modifier.fillMaxWidth()) {
        AudioBottomUI(viewModel = NewPlayerViewModelDummy(), uiState = NewPlayerUIState.DUMMY)
    }
}