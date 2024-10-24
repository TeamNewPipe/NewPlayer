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

package net.newpipe.newplayer.repository

import androidx.media3.common.MediaMetadata
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle

/**
 * This is a simple placeholder repository that will return no information. It can be used
 * during development to be able to already lay out UI elements or setup and text compile
 * NewPlayer without actually having a functioning media repository.
 */
class PlaceHolderRepository : MediaRepository {
    override fun getRepoInfo() =
        RepoMetaInfo(canHandleTimestampedLinks = true, pullsDataFromNetwork = false)


    override suspend fun getMetaInfo(item: String) = MediaMetadata.Builder().build()

    override suspend fun getStreams(item: String) = emptyList<Stream>()

    override suspend fun getSubtitles(item: String) = emptyList<Subtitle>()

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long) = null

    override suspend fun getCountOfPreviewThumbnails(item: String): Long = 0

    override suspend fun getChapters(item: String) = emptyList<Chapter>()

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) = ""
}