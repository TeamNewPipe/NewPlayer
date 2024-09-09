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

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.ITEM_CORNER_SHAPE
import net.newpipe.newplayer.utils.Thumbnail
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs

fun isActiveChapter(chapterId: Int, chapters: List<Chapter>, playbackPosition: Long) : Boolean {
    assert(0 <= chapterId && chapterId < chapters.size) {
        throw NewPlayerException("Chapter Id out of bounds: id: $chapterId, chapters.size: ${chapters.size}")
    }
    val chapterStart = chapters[chapterId].chapterStartInMs
    val chapterEnd =
        if (chapterId + 1 < chapters.size) chapters[chapterId + 1].chapterStartInMs
        else Long.MAX_VALUE
    return playbackPosition in chapterStart..<chapterEnd
}

@Composable
fun ChapterItem(
    modifier: Modifier = Modifier,
    id: Int,
    thumbnail: Uri?,
    chapterTitle: String,
    chapterStartInMs: Long,
    onClicked: (Int) -> Unit,
    isCurrentChapter: Boolean
) {
    val locale = getLocale()!!
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(ITEM_CORNER_SHAPE)
            .clickable { onClicked(id) }
    ) {
        AnimatedVisibility(
            isCurrentChapter,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(400))
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White.copy(alpha = 0.2f),
            ) {}
        }



        Row {
            val contentDescription = stringResource(R.string.chapter_thumbnail)
            Thumbnail(
                modifier = Modifier.fillMaxHeight(),
                thumbnail = thumbnail,
                contentDescription = contentDescription,
                shape = ITEM_CORNER_SHAPE
            )
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = chapterTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    getTimeStringFromMs(chapterStartInMs, locale),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
                onClicked = {},
                isCurrentChapter = false
            )
        }
    }
}