package net.newpipe.newplayer.utils

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableSharedFlow
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.PlayMode
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.Stream
import kotlin.random.Random

class MediaSourceBuilder(
    private val repository: MediaRepository,
    private val uniqueIdToIdLookup: HashMap<Long, String>,
    private val preferredLanguage: List<String>,
    private val preferredAudioId: List<String>,
    private val preferredVideoId: List<String>,
    private val playMode: PlayMode,
    private val mutableErrorFlow: MutableSharedFlow<Exception>,
    private val httpDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSource.Factory(),
) {
    @OptIn(UnstableApi::class)
    suspend fun buildMediaSource(item: String): MediaSource {
        val availableStreams = repository.getStreams(item)
        val selectedStream = StreamSelect.selectStream(
            item,
            playMode,
            availableStreams,
            preferredAudioId,
            preferredAudioId,
            preferredLanguage
        )

        val mediaSource = when (selectedStream) {
            is StreamSelect.SingleSelection -> {
                val mediaItem = toMediaItem(item, selectedStream.stream)
                val mediaItemWithMetadata = addMetadata(mediaItem, item)
                toMediaSource(mediaItemWithMetadata, selectedStream.stream)
            }

            is StreamSelect.MultiSelection -> {
                val mediaItems = ArrayList(selectedStream.streams.map { toMediaItem(item, it) })
                mediaItems[0] = addMetadata(mediaItems[0], item)
                val mediaSources = mediaItems.zip(selectedStream.streams)
                    .map { toMediaSource(it.first, it.second) }
                MergingMediaSource(
                    true, true,
                    *mediaSources.toTypedArray()
                )
            }

            else -> throw NewPlayerException("Unknown stream selection class: ${selectedStream.javaClass}")
        }

        return mediaSource
    }

    @OptIn(UnstableApi::class)
    private
    fun toMediaItem(item: String, stream: Stream): MediaItem {

        val uniqueId = Random.nextLong()
        uniqueIdToIdLookup[uniqueId] = item
        val mediaItemBuilder = MediaItem.Builder()
            .setMediaId(uniqueId.toString())
            .setUri(stream.streamUri)

        if (stream.mimeType != null) {
            mediaItemBuilder.setMimeType(stream.mimeType)
        }

        return mediaItemBuilder.build()
    }

    @OptIn(UnstableApi::class)
    private fun toMediaSource(mediaItem: MediaItem, stream: Stream): MediaSource =
        if (stream.streamType == StreamType.DYNAMIC)
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