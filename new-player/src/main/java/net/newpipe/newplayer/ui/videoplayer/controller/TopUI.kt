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
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.data.VideoStreamTrack
import net.newpipe.newplayer.logic.TrackUtils
import net.newpipe.newplayer.uiModel.EmbeddedUiConfig
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.theme.video_player_onSurface
import net.newpipe.newplayer.ui.common.getEmbeddedUiConfig
import net.newpipe.newplayer.ui.common.showNotYetImplementedToast

@OptIn(UnstableApi::class)
@Composable

/** @hide */
internal fun TopUI(
    modifier: Modifier, viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState
) {
    val embeddedUiConfig =
        if (LocalContext.current is Activity)
            getEmbeddedUiConfig(activity = LocalContext.current as Activity)
        else
            EmbeddedUiConfig.DUMMY

    Row(
        // the default height for an app bar is 64.dp according to this source:
        // https://cs.android.com/androidx/platform/frameworks/support/+/7b27816c561b8f271d79d24ab21ba7d08aaad031:compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/tokens/TopAppBarSmallTokens.kt;l=26
        // However tbh it feels a bit to height.
        modifier = modifier.height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (uiState.uiMode.fullscreen) {
            Box(modifier = Modifier.weight(1F), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = uiState.currentlyPlaying?.mediaMetadata?.title.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val creatorOffset = with(LocalDensity.current) {
                    14.sp.toDp()
                }
                Text(
                    modifier = Modifier.offset(y = creatorOffset),
                    text = uiState.currentlyPlaying?.mediaMetadata?.artist.toString(),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Box(modifier = Modifier.weight(1F))
        }
        TrackSelectionMenu(viewModel, uiState)

        val context = LocalContext.current
        IconButton(
            modifier = Modifier.fillMaxHeight(),
            onClick = { /*TODO*/
                showNotYetImplementedToast(context)
                viewModel.resetHideDelayTimer()
            },
        ) {
            Text(
                "1x", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp)
            )
        }
        AnimatedVisibility(visible = uiState.chapters.isNotEmpty()) {
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    viewModel.changeUiMode(
                        uiState.uiMode.getChapterSelectUiState(),
                        embeddedUiConfig
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = stringResource(R.string.widget_description_chapter_selection)
                )
            }
        }
        AnimatedVisibility(visible = 1 < uiState.playList.size) {
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    viewModel.changeUiMode(
                        uiState.uiMode.getStreamSelectUiState(),
                        embeddedUiConfig
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.widget_descriptoin_playlist_item_selection)
                )
            }
        }
        VideoPlayerMenu(
            modifier = Modifier.fillMaxHeight(),
            viewModel = viewModel,
            uiState = uiState
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun TrackSelectionMenu(viewModel: InternalNewPlayerViewModel, uiState: NewPlayerUIState) {
    var menuVisible by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current


    val availableVideoTracks =
        uiState.currentlyAvailableTracks.filterIsInstance<VideoStreamTrack>()

    Box {
        val noOtherTracksText = stringResource(
            id = R.string.no_other_tracks_available_toast
        )

        Button(
            modifier = Modifier,
            onClick = {
                if (1 < availableVideoTracks.size) {
                    viewModel.dialogVisible(true)
                    menuVisible = true
                } else
                    Toast.makeText(
                        context,
                        noOtherTracksText,
                        Toast.LENGTH_SHORT

                    ).show()
                viewModel.resetHideDelayTimer()
            },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = video_player_onSurface
            ),
        ) {
            Text(
                try {
                    uiState.currentlyPlayingTracks.filterIsInstance<VideoStreamTrack>()[0]
                        .toShortIdentifierString()
                } catch (_: IndexOutOfBoundsException) {
                    stringResource(R.string.loading)
                },
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(0.dp)
            )
        }
        DropdownMenu(expanded = menuVisible, onDismissRequest = { menuVisible = false }) {
            for (track in availableVideoTracks.reversed()) {
                DropdownMenuItem(text = { Text(track.toLongIdentifierString()) },
                    onClick = {
                        /* TODO */
                        showNotYetImplementedToast(context)
                        viewModel.dialogVisible(false)
                        menuVisible = false
                    })
            }
        }
    }
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=landscape")
@Composable
private fun VideoPlayerControllerTopUIPreview() {
    VideoPlayerTheme {
        Surface(color = Color.Black) {
            TopUI(
                modifier = Modifier, NewPlayerViewModelDummy(), NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO
                )
            )
        }
    }
}