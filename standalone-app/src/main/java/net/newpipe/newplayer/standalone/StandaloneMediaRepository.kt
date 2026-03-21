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

package net.newpipe.newplayer.standalone

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.inspector.MetadataRetriever
import net.newpipe.newplayer.data.AudioStreamTrack
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamTrack
import net.newpipe.newplayer.data.Subtitle
import net.newpipe.newplayer.data.VideoStreamTrack
import net.newpipe.newplayer.repository.MediaRepository
import net.newpipe.newplayer.repository.MediaRepository.PreviewThumbnailsInfo
import net.newpipe.newplayer.repository.MediaRepository.RepoMetaInfo
import kotlinx.coroutines.guava.await

/**
 * MediaRepository implementation for the standalone app.
 *
 * The item identifier is the URI/URL string itself:
 * - Local files: a content:// URI string obtained via SAF
 * - Network streams: an http/https URL string
 */
class StandaloneMediaRepository(private val context: Context) : MediaRepository {

    override fun getRepoInfo() = RepoMetaInfo(
        canHandleTimestampedLinks = false,
        pullsDataFromNetwork = true,
    )

    @OptIn(UnstableApi::class)
    override suspend fun getMetaInfo(item: String): MediaMetadata {
        val uri = item.toUri()

        val mediaItem = MediaItem.fromUri(uri);
        val metadataBuilder = MediaMetadata.Builder()

        MetadataRetriever.Builder(context, mediaItem).build().use { retriever ->
            val trackGroupsFuture = retriever.retrieveTrackGroups()
            val durationInUsFuture = retriever.retrieveDurationUs()

            metadataBuilder.setDurationMs(durationInUsFuture.await() / 1000)

            val trackGroups = trackGroupsFuture.await()
            for (groupId in 0 until trackGroups.length) {
                val trackGroup = trackGroups.get(groupId)
                for (j in 0 until trackGroup.length) {
                    trackGroup.getFormat(j).metadata?.let { metadata ->
                        metadataBuilder.populateFromMetadata(metadata)
                    }
                }
            }
        }

        if (metadataBuilder.build().title == null) {
            val title = when (uri.scheme) {
                "content" -> queryDisplayName(uri) ?: uri.lastPathSegment ?: item
                else -> uri.lastPathSegment ?: item
            }

            metadataBuilder.setTitle(title)
        }

        return metadataBuilder.build()
    }

    override suspend fun getStreams(item: String): List<Stream> {
        val uri = item.toUri()
        val mimeType = when (uri.scheme) {
            "content" -> context.contentResolver.getType(uri)
            else -> null
        }
        val tracks: List<StreamTrack> = if (mimeType?.startsWith("audio/") == true) {
            listOf(AudioStreamTrack(bitrate = 0, fileFormat = ""))
        } else {
            listOf(VideoStreamTrack(width = 0, height = 0, frameRate = 0, fileFormat = ""))
        }
        return listOf(
            Stream(
                item = item,
                streamUri = uri,
                streamTracks = tracks,
                mimeType = mimeType,
            )
        )
    }

    override suspend fun getSubtitles(item: String): List<Subtitle> = emptyList()

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long): Bitmap? = null

    override suspend fun getPreviewThumbnailsInfo(item: String) =
        PreviewThumbnailsInfo(count = 0L, distanceInMS = 0L)

    override suspend fun getChapters(item: String): List<Chapter> = emptyList()

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) = ""

    private fun queryDisplayName(uri: Uri): String? =
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null, null, null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
}