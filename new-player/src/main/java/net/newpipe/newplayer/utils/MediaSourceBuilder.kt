package net.newpipe.newplayer.utils

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableSharedFlow
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.StreamVariant
import kotlin.random.Random

class MediaSourceBuilder(
    private val repository: MediaRepository,
    private val uniqueIdToIdLookup: HashMap<Long, String>,
    private val mutableErrorFlow: MutableSharedFlow<Exception>,
    private val httpDataSourceFactory: HttpDataSource.Factory
) {
    suspend fun buildMediaSource(item: String) {
        val availableStreamVariants = repository.getAvailableStreamVariants(item)


        val tracks: Tracks? = null
        MergingMediaSource

    }

    @OptIn(UnstableApi::class)
    private suspend
    fun toMediaItem(item: String, streamVariant: StreamVariant): MediaItem {
        val dataStream = repository.getStream(item, streamVariant)

        val uniqueId = Random.nextLong()
        uniqueIdToIdLookup[uniqueId] = item
        val mediaItemBuilder = MediaItem.Builder()
            .setMediaId(uniqueId.toString())
            .setUri(dataStream.streamUri)

        if (dataStream.mimeType != null) {
            mediaItemBuilder.setMimeType(dataStream.mimeType)
        }

        return mediaItemBuilder.build()
    }

    @OptIn(UnstableApi::class)
    private fun toMediaSource(mediaItem: MediaItem, streamVariant: StreamVariant) =
        if (streamVariant.streamType == StreamType.DYNAMIC)
            DashMediaSource.Factory(httpDataSourceFactory)
                .createMediaSource(mediaItem)
        else
            ProgressiveMediaSource.Factory(httpDataSourceFactory)
                .createMediaSource(mediaItem)


    private suspend fun
            addMetadata(mediaItem: MediaItem, item: String): MediaItem {
        val mediaItemBuilder = mediaItem.buildUpon()

        try {
            val metadata = repository.getMetaInfo(item)
            mediaItemBuilder.setMediaMetadata(metadata)
        } catch (e: Exception) {
            mutableErrorFlow.emit(e)
        }

        return mediaItemBuilder.build()
    }
}