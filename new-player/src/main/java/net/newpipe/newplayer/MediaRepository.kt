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

package net.newpipe.newplayer

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import net.newpipe.newplayer.utils.Thumbnail

data class Chapter(val chapterStartInMs: Long, val chapterTitle: String?)

interface MediaRepository {
    suspend fun getTitle(item: String) : String
    suspend fun getChannelName(item: String): String
    suspend fun getThumbnail(item: String): Thumbnail
    suspend fun getAvailableStreamVariants(item: String): List<String>
    suspend fun getAvailableSubtitleVariants(item: String): List<String>

    suspend fun getStream(item: String, streamSelector: String) : Uri
    suspend fun getSubtitle(item: String, )

    suspend fun getPreviewThumbnails(item: String) : HashMap<Long, Thumbnail>?
    suspend fun getChapters(item: String): List<Chapter>
    suspend fun getChapterThumbnail(item: String, chapter: Long) : Thumbnail

    suspend fun getTimestampLink(item: String, timestampInSeconds: Long)

    suspend fun tryAndRescueError(item: String?, exception: PlaybackException) : Uri?
}