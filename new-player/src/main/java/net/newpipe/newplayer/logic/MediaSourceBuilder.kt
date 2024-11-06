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

package net.newpipe.newplayer.logic

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableSharedFlow
import net.newpipe.newplayer.repository.MediaRepository
import net.newpipe.newplayer.data.MultiSelection
import net.newpipe.newplayer.data.NewPlayerException
import net.newpipe.newplayer.data.SingleSelection
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamSelection

/**
 * This class help to transform a [StreamSelection] into a [MediaSource].
 *
 * @hide
 */
@OptIn(UnstableApi::class)
/** @hide */
internal class MediaSourceBuilder
    (
    private val repository: MediaRepository,
    private val mutableErrorFlow: MutableSharedFlow<Exception>,
    private val httpDataSourceFactory: DataSource.Factory,
) {
    @OptIn(UnstableApi::class)
    
/** @hide */
internal suspend fun buildMediaSource(
        streamSelection: StreamSelection,
        uniqueId: Long
    ): MediaSource {
        when (streamSelection) {
            is SingleSelection -> {
                val mediaItem = toMediaItem(streamSelection.item, streamSelection.stream, uniqueId)
                val mediaItemWithMetadata = addMetadata(mediaItem, streamSelection.item)
                return toMediaSource(mediaItemWithMetadata, streamSelection.stream)
            }

            is MultiSelection -> {
                val mediaItems = ArrayList(streamSelection.streams.map {
                    toMediaItem(
                        streamSelection.item,
                        it,
                        uniqueId
                    )
                })
                mediaItems[0] = addMetadata(mediaItems[0], streamSelection.item)
                val mediaSources = mediaItems.zip(streamSelection.streams)
                    .map { toMediaSource(it.first, it.second) }
                return MergingMediaSource(
                    true, true,
                    *mediaSources.toTypedArray()
                )
            }

            else -> {
                throw NewPlayerException("Unknown stream selection class: ${streamSelection.javaClass}")
            }
        }
    }

    @OptIn(UnstableApi::class)
    private
    fun toMediaItem(item: String, stream: Stream, uniqueId: Long): MediaItem {

        val mediaItemBuilder = MediaItem.Builder()
            .setMediaId(uniqueId.toString())
            .setUri(stream.streamUri)

        if (stream.mimeType != null) {
            mediaItemBuilder.setMimeType(stream.mimeType)
        }

        val mediaItem = mediaItemBuilder.build()
        return mediaItem
    }

    @OptIn(UnstableApi::class)
    private fun toMediaSource(mediaItem: MediaItem, stream: Stream): MediaSource =
        if (stream.isDashOrHls)
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