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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.logic.TrackUtils
import net.newpipe.newplayer.uiModel.EmbeddedUiConfig
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.pip.supportsPip
import net.newpipe.newplayer.ui.common.getEmbeddedUiConfig
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable
internal fun VideoPlayerMenu(viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState) {
    var showMainMenu: Boolean by remember { mutableStateOf(false) }
    var showLanguageMenu: Boolean by remember { mutableStateOf(false) }

    val pixel_density = LocalDensity.current

    var offsetY by remember {
        mutableStateOf(0.dp)
    }

    val embeddedUiConfig = if (LocalContext.current is Activity)
        getEmbeddedUiConfig(activity = LocalContext.current as Activity)
    else
        EmbeddedUiConfig.DUMMY

    val availableLanguages = TrackUtils.getAvailableLanguages(uiState.currentlyAvailableTracks)

    Box {
        IconButton(onClick = {
            showMainMenu = true
            viewModel.dialogVisible(true)
        }, modifier = Modifier.onPlaced {
            offsetY = with(pixel_density) {
                it.size.height.toDp()
            }

        }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.menu_item_more_settings)
            )
        }
        DropdownMenu(modifier = Modifier.align(Alignment.TopStart),
            offset = DpOffset(x = 0.dp, y = -offsetY),
            expanded = showMainMenu,
            onDismissRequest = {
                showMainMenu = false
                if (!showLanguageMenu)
                    viewModel.dialogVisible(false)
            }) {

            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_open_in_browser)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = stringResource(R.string.menu_item_open_in_browser)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_share_timestamp)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.menu_item_share_timestamp)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.audio_mode)) }, leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Headset,
                    contentDescription = stringResource(R.string.audio_mode)
                )
            }, onClick = {
                viewModel.changeUiMode(UIModeState.FULLSCREEN_AUDIO, embeddedUiConfig)
                showMainMenu = false
            })
            if (supportsPip(LocalContext.current)) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pip_button_description)) },
                    onClick = {
                        viewModel.changeUiMode(UIModeState.PIP, embeddedUiConfig)
                        showMainMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.PictureInPicture,
                            contentDescription = stringResource(
                                id = R.string.pip_button_description
                            )
                        )
                    })
            }
            if (uiState.uiMode.fullscreen) {
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_fit_screen)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.FitScreen,
                            contentDescription = stringResource(R.string.menu_item_fit_screen)
                        )
                    },
                    onClick = { /*TODO*/ showMainMenu = false })
            }
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_sub_titles)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Subtitles,
                        contentDescription = stringResource(R.string.menu_item_sub_titles)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            if (2 <= availableLanguages.size) {
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_language)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = stringResource(R.string.menu_item_language)
                        )
                    },
                    onClick = {
                        showLanguageMenu = true
                        showMainMenu = false
                    })
            }
        }

        DropdownMenu(expanded = showLanguageMenu, onDismissRequest = {
            showLanguageMenu = false
            viewModel.dialogVisible(false)
        }) {
            for (language in availableLanguages) {
                val locale = Locale(language)

                DropdownMenuItem(
                    text = {
                        Text(locale.displayLanguage)
                    },
                    onClick = { /*TODO*/ showLanguageMenu = false
                        viewModel.dialogVisible(false)
                    })
            }
        }
    }

}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=1080px,dpi=440,orientation=landscape")
@Composable
private fun VideoPlayerControllerDropDownPreview() {
    VideoPlayerTheme {
        Box(Modifier.fillMaxSize()) {
            VideoPlayerMenu(NewPlayerViewModelDummy(), NewPlayerUIState.DUMMY)
        }
    }
}
