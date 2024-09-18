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

package net.newpipe.newplayer.ui.streamselect

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.ui.videoplayer.CONTROLLER_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.ITEM_CORNER_SHAPE
import net.newpipe.newplayer.utils.ReorderHapticFeedback
import net.newpipe.newplayer.utils.ReorderHapticFeedbackType
import net.newpipe.newplayer.utils.Thumbnail
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs
import sh.calvin.reorderable.ReorderableCollectionItemScope

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamItem(
    modifier: Modifier = Modifier,
    playlistItem: MediaItem,
    onClicked: (Long) -> Unit,
    onDragFinished: () -> Unit,
    onDelete: () -> Unit,
    reorderableScope: ReorderableCollectionItemScope?,
    haptic: ReorderHapticFeedback?,
    isDragging: Boolean,
    isCurrentlyPlaying: Boolean
) {
    val locale = getLocale()!!

    val interactionSource = remember { MutableInteractionSource() }

    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        when (it) {
            SwipeToDismissBoxValue.StartToEnd -> {
                onDelete()
                true
            }

            SwipeToDismissBoxValue.EndToStart -> {
                onDelete()
                true
            }

            SwipeToDismissBoxValue.Settled -> true
        }
    })

    SwipeToDismissBox(
        modifier = modifier.height(60.dp), state = dismissState,
        backgroundContent = {
        },
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clip(ITEM_CORNER_SHAPE)
            .clickable {
                onClicked(playlistItem.mediaId.toLong())
            }) {

            androidx.compose.animation.AnimatedVisibility(
                visible = isDragging,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = 8.dp,
                    shape = ITEM_CORNER_SHAPE
                ) {}
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isCurrentlyPlaying,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.2f),
                ) {}
            }

            Row(
                modifier = modifier
                    .padding(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(16f / 9f)
                        .fillMaxSize()
                ) {
                    val contentDescription = stringResource(R.string.stream_item_thumbnail)
                    Thumbnail(
                        modifier = Modifier.fillMaxHeight(),
                        thumbnail = playlistItem.mediaMetadata.artworkUri,
                        contentDescription = contentDescription,
                        shape = ITEM_CORNER_SHAPE
                    )
                    Surface(
                        color = CONTROLLER_UI_BACKGROUND_COLOR,
                        shape = ITEM_CORNER_SHAPE,
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                start = 4.dp,
                                end = 4.dp,
                                top = 0.5.dp,
                                bottom = 0.5.dp
                            ),
                            text = getTimeStringFromMs(
                                playlistItem.mediaMetadata.durationMs ?: 0L,
                                locale,
                                leadingZerosForMinutes = false
                            ),
                            fontSize = 14.sp,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .weight(1f)
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Text(
                        text = playlistItem.mediaMetadata.title.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playlistItem.mediaMetadata.artist.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    modifier = if (reorderableScope != null) {
                        with(reorderableScope) {
                            Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                                .draggableHandle(
                                    onDragStarted = {
                                        haptic?.performHapticFeedback(ReorderHapticFeedbackType.START)
                                    },
                                    onDragStopped = {
                                        haptic?.performHapticFeedback(ReorderHapticFeedbackType.END)
                                        onDragFinished()
                                    },
                                    interactionSource = interactionSource,
                                )
                        }
                    } else {
                        Modifier
                            .aspectRatio(1f)
                            .fillMaxSize()
                    },
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.DragHandle,
                        contentDescription = stringResource(R.string.stream_item_drag_handle)
                    )
                }
            }
        }
    }
}


@androidx.annotation.OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=400px,dpi=440,orientation=landscape")
@Composable
fun StreamItemPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
            Box(modifier = Modifier.fillMaxSize()) {
                StreamItem(
                    playlistItem = NewPlayerUIState.DUMMY.currentlyPlaying!!,
                    onClicked = {},
                    reorderableScope = null,
                    haptic = null,
                    onDragFinished = {},
                    isDragging = false,
                    isCurrentlyPlaying = true,
                    onDelete = {println("has ben deleted")}
                )
            }
        }
    }
}