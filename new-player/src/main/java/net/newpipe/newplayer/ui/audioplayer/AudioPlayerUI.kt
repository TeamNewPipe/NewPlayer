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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.uiModel.UIModeState
import net.newpipe.newplayer.ui.common.NewPlayerSeeker
import net.newpipe.newplayer.ui.common.PlaybackSpeedDialog
import net.newpipe.newplayer.ui.common.ThumbPreview
import net.newpipe.newplayer.ui.selection_ui.ChapterSelectUI
import net.newpipe.newplayer.ui.selection_ui.StreamSelectUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.common.Thumbnail
import net.newpipe.newplayer.ui.common.getInsets
import net.newpipe.newplayer.ui.common.getLocale
import net.newpipe.newplayer.ui.common.getTimeStringFromMs
import net.newpipe.newplayer.ui.seeker.SeekerDefaults


private val UI_ENTER_ANIMATION = fadeIn(tween(200))
private val UI_EXIT_ANIMATION = fadeOut(tween(200))

@Composable

/** @hide */
internal fun lightAudioControlButtonColorScheme() = ButtonDefaults.buttonColors().copy(
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onSurface,
    disabledContainerColor = MaterialTheme.colorScheme.background
)

@OptIn(UnstableApi::class)
@Composable

/** @hide */
internal fun AudioPlayerUI(
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    isLandScape: Boolean
) {
    val insets = getInsets()

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.AUDIO_CHAPTER_SELECT,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            ChapterSelectUI(viewModel = viewModel, uiState = uiState, shownInAudioPlayer = true)
        }

        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.AUDIO_STREAM_SELECT,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            StreamSelectUI(viewModel = viewModel, uiState = uiState, shownInAudioPlayer = true)
        }

        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.EMBEDDED_AUDIO,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            AudioPlayerEmbeddedUI(viewModel = viewModel, uiState = uiState)
        }

        AnimatedVisibility(
            uiState.uiMode == UIModeState.FULLSCREEN_AUDIO,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            Scaffold(modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets),
                topBar = {

                }) { innerPadding ->
                if (isLandScape) {
                    LandscapeLayout(
                        viewModel = viewModel,
                        uiState = uiState,
                        innerPadding = innerPadding
                    )
                } else {
                    PortraitLayout(
                        viewModel = viewModel,
                        uiState = uiState,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun LandscapeLayout(
    modifier: Modifier = Modifier,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    innerPadding: PaddingValues
) {
    var playbackSpeedDialogVisible by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            CoverArt(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.9f), uiState = uiState
            )

            TitleView(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.1f), uiState = uiState
            )
        }

        Box(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
                AudioPlaybackController(
                    viewModel = viewModel,
                    uiState = uiState
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
                ProgressUI(
                    viewModel = viewModel,
                    uiState = uiState
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
            AudioBottomUI(viewModel, uiState, showPlaybackSpeedDialog = {
                playbackSpeedDialogVisible = true
            })
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.025f)
            )
        }
    }

    AnimatedVisibility(playbackSpeedDialogVisible) {
        PlaybackSpeedDialog(
            viewModel = viewModel,
            uiState = uiState,
            onDismiss = { playbackSpeedDialogVisible = false })
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PortraitLayout(
    modifier: Modifier = Modifier,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    innerPadding: PaddingValues
) {
    var playbackSpeedDialogVisible by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.5f)
                )
                CoverArt(uiState = uiState)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.3f)
                )

                TitleView(uiState = uiState)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.45f)
                )
                AudioPlaybackController(viewModel = viewModel, uiState = uiState)


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.2f)
                )
                ProgressUI(viewModel = viewModel, uiState = uiState)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.2f)
                )
            }
            AudioBottomUI(viewModel, uiState, showPlaybackSpeedDialog = {
                playbackSpeedDialogVisible = true
            })
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.025f)
            )
        }
    }

    AnimatedVisibility(playbackSpeedDialogVisible) {
        PlaybackSpeedDialog(
            viewModel = viewModel,
            uiState = uiState,
            onDismiss = { playbackSpeedDialogVisible = false })
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun ProgressUI(
    modifier: Modifier = Modifier,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState
) {
    val locale = getLocale()!!

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .height(0.dp)
                .fillMaxWidth()
                .wrapContentHeight(unbounded = true, align = Alignment.Bottom)
        ) {
            ThumbPreview(
                modifier = Modifier.offset(y = (-20).dp) /* We have this offset to make space for your thumb */,
                uiState = uiState,
                thumbSize = SeekerDefaults.ThumbRadius * 2,
                previewHeight = 120.dp
            )
        }

        NewPlayerSeeker(viewModel = viewModel, uiState = uiState)
        Row {
            Text(
                getTimeStringFromMs(
                    uiState.playbackPositionInMs,
                    getLocale() ?: locale
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Text(
                getTimeStringFromMs(
                    uiState.durationInMs,
                    getLocale() ?: locale
                )
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun TitleView(modifier: Modifier = Modifier, uiState: NewPlayerUIState) {
    Column(modifier = modifier) {
        Text(
            text = uiState.currentlyPlaying?.mediaMetadata?.title.toString(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = 6.em
        )
        Text(
            text = uiState.currentlyPlaying?.mediaMetadata?.artist.toString(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = 4.em
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun CoverArt(modifier: Modifier = Modifier, uiState: NewPlayerUIState) {
    Box {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Thumbnail(
                modifier = Modifier.fillMaxWidth(),
                thumbnail = uiState.currentlyPlaying?.mediaMetadata?.artworkUri,
                contentDescription = stringResource(
                    id = R.string.stream_thumbnail
                ),
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6", showSystemUi = true)
@Composable
private fun AudioPlayerUIPortraitPreview() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(uiMode = UIModeState.FULLSCREEN_AUDIO),
            isLandScape = false
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=landscape", showSystemUi = true)
@Composable
private fun AudioPlayerUILandscapePreview() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(uiMode = UIModeState.FULLSCREEN_AUDIO),
            isLandScape = true
        )
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:parent=pixel_6,orientation=portrait", showSystemUi = true)
@Composable
private fun AudioPlayerUIEmbeddedPreview() {
    VideoPlayerTheme {
        AudioPlayerUI(
            viewModel = NewPlayerViewModelDummy(),
            uiState = NewPlayerUIState.DUMMY.copy(uiMode = UIModeState.EMBEDDED_AUDIO),
            isLandScape = false
        )
    }
}