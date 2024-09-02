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

package net.newpipe.newplayer.ui.videoplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.CONTROLLER_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.playerInternals.PlaylistItem
import net.newpipe.newplayer.ui.STREAMSELECT_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.videoplayer.streamselect.ChapterItem
import net.newpipe.newplayer.ui.videoplayer.streamselect.ChapterSelectTopBar
import net.newpipe.newplayer.ui.videoplayer.streamselect.StreamItem
import net.newpipe.newplayer.ui.videoplayer.streamselect.StreamSelectTopBar
import net.newpipe.newplayer.utils.getInsets

@Composable
fun StreamSelectUI(
    isChapterSelect: Boolean = false,
    viewModel: VideoPlayerViewModel,
    uiState: VideoPlayerUIState
) {
    val insets = getInsets()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = STREAMSELECT_UI_BACKGROUND_COLOR
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets),
            containerColor = Color.Transparent,
            topBar = {
                if (isChapterSelect) {
                    ChapterSelectTopBar(
                        onClose =
                        viewModel::closeStreamSelection
                    )
                } else {
                    StreamSelectTopBar(viewModel = viewModel, uiState = uiState)
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 8.dp, end = 4.dp)
            ) {
                if (isChapterSelect) {
                    items(uiState.chapters.size) { chapterIndex ->
                        val chapter = uiState.chapters[chapterIndex]
                        ChapterItem(
                            id = chapterIndex,
                            chapterTitle = chapter.chapterTitle ?: "",
                            chapterStartInMs = chapter.chapterStartInMs,
                            thumbnail = chapter.thumbnail,
                            onClicked = {
                                viewModel.chapterSelected(chapter)
                            }
                        )
                    }
                } else {
                    items(uiState.playList.size) { playlistItemIndex ->
                        val playlistItem = uiState.playList[playlistItemIndex]
                        StreamItem(
                            id = playlistItemIndex,
                            title = playlistItem.title,
                            creator = playlistItem.creator,
                            thumbnail = playlistItem.thumbnail,
                            lengthInMs = playlistItem.lengthInS.toLong() * 1000,
                            onDragStart = {},
                            onDragEnd = {},
                            onClicked = { viewModel.streamSelected(playlistItemIndex) }
                        )
                    }
                }
            }
        }
    }
}


@Preview(device = "id:pixel_5")
@Composable
fun VideoPlayerChannelSelectUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Red) {
            StreamSelectUI(
                isChapterSelect = true,
                viewModel = VideoPlayerViewModelDummy(),
                uiState = VideoPlayerUIState.DEFAULT.copy(
                    chapters = arrayListOf(
                        Chapter(
                            chapterStartInMs = 5000,
                            chapterTitle = "First Chapter",
                            thumbnail = null
                        ),
                        Chapter(
                            chapterStartInMs = 10000,
                            chapterTitle = "Second Chapter",
                            thumbnail = null
                        ),
                        Chapter(
                            chapterStartInMs = 20000,
                            chapterTitle = "Third Chapter",
                            thumbnail = null
                        ),
                    )
                )
            )
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
fun VideoPlayerStreamSelectUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Red) {
            StreamSelectUI(
                isChapterSelect = false,
                viewModel = VideoPlayerViewModelDummy(),
                uiState = VideoPlayerUIState.DEFAULT.copy(
                    playList = arrayListOf(
                        PlaylistItem(
                            id = "6502",
                            title = "Stream 1",
                            creator = "The Creator",
                            lengthInS = 6 * 60 + 5,
                            thumbnail = null
                        ),
                        PlaylistItem(
                            id = "6502",
                            title = "Stream 2",
                            creator = "The Creator 2",
                            lengthInS = 2 * 60 + 5,
                            thumbnail = null
                        ),
                        PlaylistItem(
                            id = "6502",
                            title = "Stream 3",
                            creator = "The Creator 3",
                            lengthInS = 29 * 60 + 5,
                            thumbnail = null
                        )
                    )
                )
            )
        }
    }
}