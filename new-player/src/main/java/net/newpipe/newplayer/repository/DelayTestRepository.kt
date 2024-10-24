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

import android.graphics.Bitmap
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.delay
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle

/**
 * This is a meta repository implementation meant for testing. You can give it an actual repository
 * and a delay, each request to your actual repository will then be delayed. This can be used to
 * test a slow network.
 *
 * @param actualRepo your actual MediaRepository
 * @param delayInMS is the delay in milliseconds
 */
class DelayTestRepository(val actualRepo: MediaRepository, var delayInMS: Long) : MediaRepository {
    override fun getRepoInfo() = actualRepo.getRepoInfo()

    override suspend fun getMetaInfo(item: String): MediaMetadata {
        delay(delayInMS)
        return actualRepo.getMetaInfo(item)
    }

    override suspend fun getStreams(item: String): List<Stream> {
        delay(delayInMS)
        return actualRepo.getStreams(item)
    }

    override suspend fun getSubtitles(item: String): List<Subtitle> {
        delay(delayInMS)
        return actualRepo.getSubtitles(item)
    }

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long): Bitmap? {
        delay(delayInMS)
        return actualRepo.getPreviewThumbnail(item, timestampInMs)
    }

    override suspend fun getCountOfPreviewThumbnails(item: String): Long {
        delay(delayInMS)
        return actualRepo.getCountOfPreviewThumbnails(item)
    }

    override suspend fun getChapters(item: String): List<Chapter> {
        delay(delayInMS)
        return actualRepo.getChapters(item)
    }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long): String {
        delay(delayInMS)
        return actualRepo.getTimestampLink(item, timestampInSeconds)
    }
}