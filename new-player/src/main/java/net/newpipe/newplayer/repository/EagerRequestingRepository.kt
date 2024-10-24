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

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * This is a meta media repository that performs requests to all possible values of an item
 * as soon as it gets an item it has not seen before. This can be used in combination with a
 * caching repository to perform eager caching.
 *
 * An exception to this are requests to `getTimestampLink()` since actual requests this is rare.
 *
 * @param cachingRepository a MediaRepository implementation that performs caching
 * @param disableEagerCaching this allows to temporarily disable eager requests. This can be uses
 * when the system is in power saving mode.
 */
class EagerRequestingRepository(
    val cachingRepository: MediaRepository,
    var disableEagerCaching: Boolean = false
) : MediaRepository {
    var hasBeenSeenBefore = HashSet<String>()

    private suspend fun requestAll(item: String): Unit = coroutineScope {
        async { cachingRepository.getMetaInfo(item) }
        async { cachingRepository.getStreams(item) }
        async { cachingRepository.getSubtitles(item) }
        async {
            for (i in 0..cachingRepository.getCountOfPreviewThumbnails(item)) {
                async { cachingRepository.getPreviewThumbnail(item, i) }
            }
        }
        async { cachingRepository.getChapters(item) }
    }

    private suspend fun <T> requestAllIfNotSeenBefore(item: String, request: suspend () -> T): T {
        if (!disableEagerCaching && !hasBeenSeenBefore.contains(item)) {
            hasBeenSeenBefore.add(item)
            requestAll(item)
        }
        return request()
    }

    override fun getRepoInfo() = cachingRepository.getRepoInfo()

    override suspend fun getMetaInfo(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getMetaInfo(item)
        }

    override suspend fun getStreams(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getStreams(item)
        }

    override suspend fun getSubtitles(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getSubtitles(item)
        }

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getPreviewThumbnail(item, timestampInMs)
        }

    override suspend fun getCountOfPreviewThumbnails(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getCountOfPreviewThumbnails(item)
        }

    override suspend fun getChapters(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getChapters(item)
        }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) =
        cachingRepository.getTimestampLink(item, timestampInSeconds)

    /**
     * Resets the information weather something was seen before or not.
     */
    fun reset() {
        hasBeenSeenBefore = HashSet()
    }
}