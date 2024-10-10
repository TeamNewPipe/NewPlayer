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

package net.newpipe.newplayer.utils

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableSharedFlow
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.Stream
import kotlin.random.Random

internal class MediaSourceBuilder(
    private val repository: MediaRepository,
    private val uniqueIdToIdLookup: HashMap<Long, String>,
    private val mutableErrorFlow: MutableSharedFlow<Exception>,
    private val httpDataSourceFactory: HttpDataSource.Factory,
) {
    @OptIn(UnstableApi::class)
    internal suspend fun buildMediaSource(selectedStream: StreamSelection): MediaSource {

        val mediaSource = when (selectedStream) {
            is SingleSelection -> {
                val mediaItem = toMediaItem(selectedStream.item, selectedStream.stream)
                val mediaItemWithMetadata = addMetadata(mediaItem, selectedStream.item)
                toMediaSource(mediaItemWithMetadata, selectedStream.stream)
            }

            is MultiSelection -> {
                val mediaItems = ArrayList(selectedStream.streams.map { toMediaItem(selectedStream.item, it) })
                mediaItems[0] = addMetadata(mediaItems[0], selectedStream.item)
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