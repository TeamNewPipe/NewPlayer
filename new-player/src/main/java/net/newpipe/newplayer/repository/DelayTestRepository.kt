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

    override suspend fun getChapters(item: String): List<Chapter> {
        delay(delayInMS)
        return actualRepo.getChapters(item)
    }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long): String {
        delay(delayInMS)
        return actualRepo.getTimestampLink(item, timestampInSeconds)
    }
}