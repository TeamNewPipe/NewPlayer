package net.newpipe.newplayer

import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem

interface MediaRepository {
    suspend fun getTitle(item: String) : String
    suspend fun getChannelName(item: String): String
    suspend fun getThumbnail(item: String): Bitmap

    suspend fun getAvailableStreams(item: String): List<String>

    suspend fun getStream(item: String, streamSelector: String) : MediaItem
    suspend fun getLinkWithStreamOffset(item: String) : String

    suspend fun getPreviewThumbnails(item: String) : List<Bitmap>
    suspend fun getChapters(item: String): List<Long>
    suspend fun getChapterThumbnail(item: String, chapter: Long) : Bitmap
}