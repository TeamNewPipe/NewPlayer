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

package net.newpipe.newplayer.ui.videoplayer.streamselect

import android.view.MotionEvent
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import coil.compose.AsyncImage
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.CONTROLLER_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.BitmapThumbnail
import net.newpipe.newplayer.utils.OnlineThumbnail
import net.newpipe.newplayer.utils.Thumbnail
import net.newpipe.newplayer.utils.VectorThumbnail
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StreamItem(
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
    Row(modifier = modifier
        .clickable { onClicked(id) }
        .height(80.dp)) {
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