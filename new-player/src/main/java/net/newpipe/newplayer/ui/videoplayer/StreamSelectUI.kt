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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.playerInternals.PlaylistItem
import net.newpipe.newplayer.ui.STREAMSELECT_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.videoplayer.streamselect.ChapterItem
import net.newpipe.newplayer.ui.videoplayer.streamselect.ChapterSelectTopBar
import net.newpipe.newplayer.ui.videoplayer.streamselect.StreamItem
import net.newpipe.newplayer.ui.videoplayer.streamselect.StreamSelectTopBar
import net.newpipe.newplayer.utils.ReorderHapticFeedbackType
import net.newpipe.newplayer.utils.getInsets
import net.newpipe.newplayer.utils.rememberReorderHapticFeedback
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


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
            if (isChapterSelect) {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(start = 8.dp, end = 4.dp)
                ) {

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

                }
            } else {
                ReorderableStreamItemsList(
                    innerPadding = innerPadding,
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
fun ReorderableStreamItemsList(
    innerPadding: PaddingValues,
    viewModel: VideoPlayerViewModel,
    uiState: VideoPlayerUIState
) {
    val haptic = rememberReorderHapticFeedback()

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState = lazyListState) { from, to ->
            haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
            viewModel.movePlaylistItem(from.index, to.index)
        }

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        state = lazyListState
    ) {
        itemsIndexed(uiState.playList, key = {_, item -> item.uniqueId}) { index, playlistItem ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = playlistItem.uniqueId
            ) { isDragging ->
                StreamItem(
                    uniqueId = playlistItem.uniqueId,
                    title = playlistItem.title,
                    creator = playlistItem.creator,
                    thumbnail = playlistItem.thumbnail,
                    lengthInMs = playlistItem.lengthInS.toLong() * 1000,
                    onClicked = { viewModel.streamSelected(0) },
                    reorderableScope = this@ReorderableItem,
                    haptic = haptic,
                    onDragFinished = viewModel::onStreamItemDragFinished,
                    isDragging = isDragging
                )
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
                uiState = VideoPlayerUIState.DUMMY.copy(
                    playList = arrayListOf(
                        PlaylistItem(
                            id = "6502",
                            title = "Stream 1",
                            creator = "The Creator",
                            lengthInS = 6 * 60 + 5,
                            thumbnail = null,
                            uniqueId = 0
                        ),
                        PlaylistItem(
                            id = "6502",
                            title = "Stream 2",
                            creator = "The Creator 2",
                            lengthInS = 2 * 60 + 5,
                            thumbnail = null,
                            uniqueId = 1
                        ),
                        PlaylistItem(
                            id = "6502",
                            title = "Stream 3",
                            creator = "The Creator 3",
                            lengthInS = 29 * 60 + 5,
                            thumbnail = null,
                            uniqueId = 2
                        )
                    )
                )
            )
        }
    }
}