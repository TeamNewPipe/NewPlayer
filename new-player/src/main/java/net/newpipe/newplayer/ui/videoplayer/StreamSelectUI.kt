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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import net.newpipe.newplayer.ui.videoplayer.streamselect.isActiveChapter
import net.newpipe.newplayer.utils.ReorderHapticFeedbackType
import net.newpipe.newplayer.utils.getInsets
import net.newpipe.newplayer.utils.rememberReorderHapticFeedback
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

val ITEM_CORNER_SHAPE = RoundedCornerShape(10.dp)

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
            Box(modifier = Modifier.padding(innerPadding)) {
                if (isChapterSelect) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        items(uiState.chapters.size) { chapterIndex ->
                            val chapter = uiState.chapters[chapterIndex]
                            ChapterItem(
                                id = chapterIndex,
                                chapterTitle = chapter.chapterTitle ?: "",
                                chapterStartInMs = chapter.chapterStartInMs,
                                thumbnail = chapter.thumbnail,
                                onClicked = {
                                    viewModel.chapterSelected(chapterIndex)
                                },
                                isCurrentChapter = isActiveChapter(
                                    chapterIndex,
                                    uiState.chapters,
                                    uiState.playbackPositionInMs
                                )
                            )
                        }

                    }
                } else {
                    ReorderableStreamItemsList(
                        padding = PaddingValues(start = 5.dp, end = 5.dp),
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
            }
        }
    }
}

@Composable
fun ReorderableStreamItemsList(
    padding: PaddingValues,
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
            .padding(padding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = lazyListState
    ) {
        itemsIndexed(uiState.playList, key = { _, item -> item.uniqueId }) { index, playlistItem ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = playlistItem.uniqueId
            ) { isDragging ->
                StreamItem(
                    playlistItem = playlistItem,
                    onClicked = { viewModel.streamSelected(index) },
                    reorderableScope = this@ReorderableItem,
                    haptic = haptic,
                    onDragFinished = viewModel::onStreamItemDragFinished,
                    isDragging = isDragging,
                    isCurrentlyPlaying = playlistItem.uniqueId == uiState.currentlyPlaying.uniqueId,
                    onDelete = {
                        viewModel.removePlaylistItem(playlistItem.uniqueId)
                    }
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
                uiState = VideoPlayerUIState.DUMMY
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
                    currentlyPlaying = PlaylistItem.DUMMY.copy(uniqueId = 1)
                )
            )
        }
    }
}