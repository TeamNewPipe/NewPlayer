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

import android.graphics.Bitmap
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle

data class RepoMetaInfo(
    val canHandleTimestampedLinks: Boolean,
    val pullsDataFromNetwork: Boolean
)

interface MediaRepository {

    fun getRepoInfo(): RepoMetaInfo

    fun getHttpDataSourceFactory(item: String): HttpDataSource.Factory =
        DefaultHttpDataSource.Factory()

    suspend fun getMetaInfo(item: String): MediaMetadata

    suspend fun getStreams(item: String): List<Stream>

    suspend fun getSubtitles(item: String): List<Subtitle>

    suspend fun getPreviewThumbnail(item: String, timestampInMs: Long): Bitmap?

    suspend fun getChapters(item: String): List<Chapter>

    suspend fun getTimestampLink(item: String, timestampInSeconds: Long): String
}