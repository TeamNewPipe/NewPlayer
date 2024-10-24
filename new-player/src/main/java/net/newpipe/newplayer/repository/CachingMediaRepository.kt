package net.newpipe.newplayer.repository

import android.graphics.Bitmap
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle

/**
 * This is a meta media repository that functions as a cache.
 * By default, NewPlayer itself does not cache anything, which is why it asks the repository
 * for anything over and over again. If these requests are not cached your app might unnecessarily
 * perform network requests or other IO. In order to prevent this use this media repository.
 *
 * However, it's discouraged to use this cache if your app already has a cache. Use your own
 * cache instead in order not to cache the same data twice. When using your own cache you will also
 * be able to share cached data between your app and NewPlayer. (IE. NewPipe should not use this).
 *
 */
class CachingMediaRepository(
    val actualRepository: MediaRepository,
    requestDispatcher: CoroutineDispatcher = Dispatchers.Main
) : MediaRepository {

    private val cacheRepoScope = CoroutineScope(requestDispatcher + Job())

    private open inner class Cache<K, T> {
        var cache: HashMap<K, T> = HashMap()
        var requestLock: HashMap<K, Deferred<Unit>> = HashMap()

        suspend fun get(key: K, onCacheMiss: suspend () -> T): T {
            return cache[key] ?: run {
                val deferred = requestLock[key] ?: cacheRepoScope.async {
                    val newValue = onCacheMiss()
                    if (newValue != null) {
                        cache[key] = newValue
                    }
                    Unit
                }
                deferred.await()
                cache[key]!!
            }
        }

        fun flush() {
            cache = HashMap()
            requestLock = HashMap()
        }
    }

    private data class TimestampedItem(val item: String, val timestamp: Long)
    private inner class ItemCache<T> : Cache<String, T>()
    private inner class TimeStampedCache<T> : Cache<TimestampedItem, T>()

    private var metaInfoCache = ItemCache<MediaMetadata>()
    private var streamsCache = ItemCache<List<Stream>>()
    private var subtitlesCache = ItemCache<List<Subtitle>>()
    private var thumbnailCache = TimeStampedCache<Bitmap?>()
    private var chapterCache = ItemCache<List<Chapter>>()
    private var timestampLinkCache = TimeStampedCache<String>()

    override fun getRepoInfo() = actualRepository.getRepoInfo()

    override suspend fun getMetaInfo(item: String) = metaInfoCache.get(item) {
        actualRepository.getMetaInfo(item)
    }

    override suspend fun getStreams(item: String) = streamsCache.get(item) {
        actualRepository.getStreams(item)
    }

    override suspend fun getSubtitles(item: String) = subtitlesCache.get(item) {
        actualRepository.getSubtitles(item)
    }

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long) =
        thumbnailCache.get(TimestampedItem(item, timestampInMs)) {
            actualRepository.getPreviewThumbnail(item, timestampInMs)
        }

    override suspend fun getChapters(item: String) = chapterCache.get(item) {
        actualRepository.getChapters(item)
    }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) =
        timestampLinkCache.get(TimestampedItem(item, timestampInSeconds)) {
            actualRepository.getTimestampLink(item, timestampInSeconds)
        }

    fun flush() {
        cacheRepoScope.cancel()
        metaInfoCache.flush()
        streamsCache.flush()
        subtitlesCache.flush()
        thumbnailCache.flush()
        thumbnailCache.flush()
        chapterCache.flush()
        timestampLinkCache.flush()
    }
}