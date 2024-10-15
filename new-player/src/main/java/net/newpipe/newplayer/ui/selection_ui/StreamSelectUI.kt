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

package net.newpipe.newplayer.ui.selection_ui

import androidx.annotation.OptIn
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.common.ReorderHapticFeedbackType
import net.newpipe.newplayer.ui.common.getInsets
import net.newpipe.newplayer.ui.common.rememberReorderHapticFeedback
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

internal val ITEM_CORNER_SHAPE = RoundedCornerShape(10.dp)

@OptIn(UnstableApi::class)
@Composable
internal fun StreamSelectUI(
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState,
    shownInAudioPlayer: Boolean
) {
    val insets = getInsets()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(insets),
        containerColor = if (shownInAudioPlayer)
            MaterialTheme.colorScheme.background
        else
            Color.Transparent,
        topBar = {
            StreamSelectTopBar(viewModel = viewModel, uiState = uiState)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ReorderableStreamItemsList(
                padding = PaddingValues(start = 5.dp, end = 5.dp),
                viewModel = viewModel,
                uiState = uiState
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun ReorderableStreamItemsList(
    padding: PaddingValues,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState
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
        itemsIndexed(
            uiState.playList,
            key = { _, item -> item.mediaId.toLong() }) { index, playlistItem ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = playlistItem.mediaId.toLong()
            ) { isDragging ->
                StreamItem(
                    playlistItem = playlistItem,
                    onClicked = { viewModel.streamSelected(index) },
                    reorderableScope = this@ReorderableItem,
                    haptic = haptic,
                    onDragFinished = viewModel::onStreamItemDragFinished,
                    isDragging = isDragging,
                    isCurrentlyPlaying = playlistItem.mediaId.toLong() == uiState.currentlyPlaying?.mediaId?.toLong(),
                    onDelete = {
                        viewModel.removePlaylistItem(playlistItem.mediaId.toLong())
                    }
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "id:pixel_5")
@Composable
private fun VideoPlayerStreamSelectUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Red) {
            StreamSelectUI(
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY,
                shownInAudioPlayer = false
            )
        }
    }
}