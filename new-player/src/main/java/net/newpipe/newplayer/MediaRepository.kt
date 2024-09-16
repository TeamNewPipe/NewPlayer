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
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException

data class Chapter(val chapterStartInMs: Long, val chapterTitle: String?, val thumbnail: Uri?)

enum class StreamType {
    VIDEO,
    AUDIO,
    AUDIO_AND_VIDEO,
    DYNAMIC
}

data class Stream(
    val streamUri: Uri,
    val identifier: String,
    val streamType: StreamType,
    val language: String?,
    val mimeType: String? = null,
) {
    override fun equals(other: Any?) =
        other is Stream
                && other.streamUri == streamUri
                && other.streamType == streamType
                && other.language == language
                && other.identifier == identifier
                && other.mimeType == mimeType

}

data class RepoMetaInfo(
    val canHandleTimestampedLinks: Boolean,
    val pullsDataFromNetwrok: Boolean
)

data class Subtitle(
    val uri: Uri,
    val identifier: String
)

interface MediaRepository {

    fun getRepoInfo(): RepoMetaInfo

    suspend fun getMetaInfo(item: String): MediaMetadata

    suspend fun getStreams(item: String): List<Stream>

    suspend fun getSubtitles(item: String): List<Subtitle>

    suspend fun getPreviewThumbnails(item: String): HashMap<Long, Uri>?
    suspend fun getChapters(item: String): List<Chapter>

    suspend fun getTimestampLink(item: String, timestampInSeconds: Long): String

    suspend fun tryAndRescueError(item: String?, exception: PlaybackException): Uri?
}