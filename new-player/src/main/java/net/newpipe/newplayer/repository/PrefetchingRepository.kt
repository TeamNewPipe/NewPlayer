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

import androidx.media3.datasource.HttpDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
 * @param requestDispatcher the thread this repository should use to perform requests with.
 * This should be the same thad as the one NewPlayer is in.
 */
class PrefetchingRepository(
    val cachingRepository: MediaRepository,
    var disableEagerCaching: Boolean = false,
    private val requestDispatcher: CoroutineDispatcher = Dispatchers.Main
) : MediaRepository {
    private var hasBeenSeenBefore = HashSet<String>()

    private val requestScope = CoroutineScope(requestDispatcher + Job())

    private suspend fun requestAll(item: String): Unit = coroutineScope {
        requestScope.launch { cachingRepository.getMetaInfo(item) }
        requestScope.launch { cachingRepository.getStreams(item) }
        requestScope.launch { cachingRepository.getSubtitles(item) }
        requestScope.launch {
            val info = cachingRepository.getPreviewThumbnailsInfo(item)
            for (i in 0..info.count) {
                requestScope.launch {
                    cachingRepository.getPreviewThumbnail(
                        item,
                        info.distanceInMS * i
                    )
                }
            }
        }
        requestScope.launch { cachingRepository.getChapters(item) }
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

    override suspend fun getPreviewThumbnailsInfo(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getPreviewThumbnailsInfo(item)
        }

    override suspend fun getChapters(item: String) =
        requestAllIfNotSeenBefore(item) {
            cachingRepository.getChapters(item)
        }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) =
        cachingRepository.getTimestampLink(item, timestampInSeconds)

    override fun getHttpDataSourceFactory(item: String) =
        cachingRepository.getHttpDataSourceFactory(item)

    /**
     * Manually trigger a prefetch of [item] without performing an actual request.
     */
    suspend fun prefetch(item:String) {
        if(!hasBeenSeenBefore.contains(item)) {
            hasBeenSeenBefore.add(item)
            requestAll(item)
        }
    }

    /**
     * Resets the information weather something was seen before or not.
     */
    fun reset() {
        hasBeenSeenBefore = HashSet()
    }
}