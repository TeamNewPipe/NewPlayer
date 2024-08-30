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

import android.view.MotionEvent
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.CONTROLLER_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs
import coil.compose.AsyncImage
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.utils.BitmapThumbnail
import net.newpipe.newplayer.utils.OnlineThumbnail
import net.newpipe.newplayer.utils.Thumbnail
import net.newpipe.newplayer.utils.VectorThumbnail
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
        color = CONTROLLER_UI_BACKGROUND_COLOR
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets),
            containerColor = Color.Transparent,
            topBar = {
                if (isChapterSelect) {
                    ChapterSelectTopBar(onClose = {
                        viewModel.closeStreamSelection()
                    })
                } else {
                    StreamSelectTopBar()
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
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

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterSelectTopBar(modifier: Modifier = Modifier, onClose: () -> Unit) {
    TopAppBar(modifier = modifier,
        colors = topAppBarColors(containerColor = Color.Transparent),
        title = {
            Text("Chapter TODO")
            //Text(stringResource(R.string.chapter))
        }, actions = {
            IconButton(
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_chapter_selection)
                )
            }
        })
}

@Composable
private fun StreamSelectTopBar() {

}

@Composable
private fun ChapterItem(
    modifier: Modifier = Modifier,
    id: Int,
    thumbnail: Thumbnail?,
    chapterTitle: String,
    chapterStartInMs: Long,
    onClicked: (Int) -> Unit
) {
    val locale = getLocale()!!
    Row(
        modifier = modifier
            .padding(
                start = 8.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 4.dp
            ).height(80.dp)
            .clickable { onClicked(id) }
    ) {
        val contentDescription = stringResource(R.string.chapter)
        if (thumbnail != null) {
            when (thumbnail) {
                is OnlineThumbnail -> AsyncImage(
                    model = thumbnail.url,
                    contentDescription = contentDescription
                )

                is BitmapThumbnail -> Image(
                    bitmap = thumbnail.img,
                    contentDescription = contentDescription
                )

                is VectorThumbnail -> Image(
                    imageVector = thumbnail.vec,
                    contentDescription = contentDescription
                )
            }
            AsyncImage(
                model = thumbnail,
                contentDescription = contentDescription
            )
        } else {
            Image(
                painterResource(R.drawable.tiny_placeholder),
                contentDescription = stringResource(R.string.chapter_thumbnail)
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = chapterTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(getTimeStringFromMs(chapterStartInMs, locale))
        }

    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun StreamItem(
    modifier: Modifier = Modifier,
    id: Int,
    title: String,
    creator: String?,
    thumbnail: Thumbnail?,
    lengthInMs: Long,
    onDragStart: (Int) -> Unit,
    onDragEnd: (Int) -> Unit,
    onClicked: (Int) -> Unit
) {
    val locale = getLocale()!!
    Row(modifier = modifier.clickable { onClicked(id) }) {
        Box {
            val contentDescription = stringResource(R.string.chapter)
            if (thumbnail != null) {
                when (thumbnail) {
                    is OnlineThumbnail -> AsyncImage(
                        model = thumbnail.url,
                        contentDescription = contentDescription
                    )

                    is BitmapThumbnail -> Image(
                        bitmap = thumbnail.img,
                        contentDescription = contentDescription
                    )

                    is VectorThumbnail -> Image(
                        imageVector = thumbnail.vec,
                        contentDescription = contentDescription
                    )
                }
                AsyncImage(
                    model = thumbnail,
                    contentDescription = contentDescription
                )
            } else {
                Image(
                    painterResource(R.drawable.tiny_placeholder),
                    contentDescription = stringResource(R.string.chapter_thumbnail)
                )
            }
            Surface(
                color = CONTROLLER_UI_BACKGROUND_COLOR,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 2.dp,
                        bottom = 2.dp
                    ), text = getTimeStringFromMs(lengthInMs, locale)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .fillMaxSize()
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (creator != null) {
                Text(text = creator)
            }
        }

        Box(modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_UP -> {
                        onDragEnd(id)
                        false
                    }

                    MotionEvent.ACTION_DOWN -> {
                        onDragStart(id)
                        false
                    }

                    else -> true
                }
            }) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                imageVector = Icons.Filled.DragHandle,
                //contentDescription = stringResource(R.string.stream_item_drag_handle)
                contentDescription = "placeholer, TODO: FIXME"
            )
        }
    }
}

@Preview(device = "spec:width=1080px,height=300px,dpi=440,orientation=landscape")
@Composable
fun ChapterItemPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
            ChapterItem(
                id = 0,
                thumbnail = null,
                modifier = Modifier.fillMaxSize(),
                chapterTitle = "Chapter Title",
                chapterStartInMs = (4 * 60 + 32) * 1000,
                onClicked = {}
            )
        }
    }
}

@Preview(device = "spec:width=1080px,height=200px,dpi=440,orientation=landscape")
@Composable
fun StreamItemPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
            StreamItem(
                id = 0,
                modifier = Modifier.fillMaxSize(),
                title = "Video Title",
                creator = "Video Creator",
                thumbnail = null,
                lengthInMs = 15 * 60 * 1000,
                onDragStart = {},
                onDragEnd = {},
                onClicked = {}
            )
        }
    }
}

@Preview(device = "spec:width=1080px,height=150px,dpi=440,orientation=landscape")
@Composable
fun ChapterTopBarPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.DarkGray) {
            ChapterSelectTopBar(modifier = Modifier.fillMaxSize()) {}
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
fun VideoPlayerStreamSelectUIPreview() {
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