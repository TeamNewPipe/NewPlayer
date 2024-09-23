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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.ui.common.NewPlayerSeeker
import net.newpipe.newplayer.ui.streamselect.ChapterSelectUI
import net.newpipe.newplayer.ui.streamselect.StreamSelectUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.Thumbnail
import net.newpipe.newplayer.utils.getInsets


private val UI_ENTER_ANIMATION = fadeIn(tween(200))
private val UI_EXIT_ANIMATION = fadeOut(tween(200))

@Composable
fun lightAudioControlButtonColorScheme() = ButtonDefaults.buttonColors().copy(
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onSurface,
    disabledContainerColor = MaterialTheme.colorScheme.background
)

@OptIn(UnstableApi::class)
@Composable
fun AudioPlayerUI(viewModel: NewPlayerViewModel, uiState: NewPlayerUIState) {
    val insets = getInsets()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.AUDIO_CHAPTER_SELECT,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            ChapterSelectUI(viewModel = viewModel, uiState = uiState)
        }

        AnimatedVisibility(
            visible = uiState.uiMode == UIModeState.AUDIO_STREAM_SELECT,
            enter = UI_ENTER_ANIMATION,
            exit = UI_EXIT_ANIMATION
        ) {
            StreamSelectUI(viewModel = viewModel, uiState = uiState)
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
                Box(
                    modifier = Modifier
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
                                    .weight(1f)
                            )
                            Box {
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            )
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

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(0.2f)
                            )
                            NewPlayerSeeker(viewModel = viewModel, uiState = uiState)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(0.2f)
                            )
                            AudioPlaybackController(viewModel = viewModel, uiState = uiState)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(0.2f)
                            )
                        }
                        AudioBottomUI(viewModel, uiState)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(0.025f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_6", showSystemUi = true)
@Composable
fun AudioPlayerUIPreview() {
    VideoPlayerTheme {
        AudioPlayerUI(viewModel = NewPlayerViewModelDummy(), uiState = NewPlayerUIState.DUMMY)
    }
}