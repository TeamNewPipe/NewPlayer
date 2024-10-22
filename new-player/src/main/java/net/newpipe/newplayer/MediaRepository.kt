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

package net.newpipe.newplayer

import android.graphics.Bitmap
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle
import net.newpipe.newplayer.data.StreamTrack

/**
 * General information about the repository and the data it provides.
 */
data class RepoMetaInfo(
    /**
     * Depict weather it is possible to create timestamped likes to items.
     * For example you can create a time stamped url to a youtube video.
     * But you can not even share a link to a video that is stored locally on your phones storage.
     */
    val canHandleTimestampedLinks: Boolean,

    /**
     * Tells if the repository pulls data from the Network or if the data the repository provides
     * is stored locally. Please keep in mind that this setting will also influence the sleep lock
     * of your device.
     */
    val pullsDataFromNetwork: Boolean
)

/**
 * You, dear Developer who uses NewPlayer, will want to implement MediaRepository.
 * This interface is the thing that will enable NewPlayer to access your content.
 * Just like the ViewModel and the View, the Repository is an element of the MVVM architecture.
 * This repository is basically the "view" to your data.
 *
 * ## How does MediaRepository work
 * Every piece of media that can be defined as a unit is referred to as an *item*.
 * An item can be a YouTube video or a media.ccc talk, or a video stored on your phones storage
 *
 * With each *item* associated are so called [Stream]s. Every *item* has at least one or multiple
 * [Stream]s associated with it.
 * A stream referees to one individual media file that can either be accessed from the internet
 * via its unique URL or is stored locally on your phone's storage, and can be referred to via
 * URI. In other words a [Stream] refers to one media container (like an mpeg4 or a webm file).
 *
 * Every [Stream] contains one or multiple tracks, which NewPlayer calls [StreamTrack]s.
 * Each track refers to either one encoded stream of Video or one encoded stream of Audio.
 *
 * The way it works is that you tell [NewPlayer] to play a certain **item**. [NewPlayer] will then
 * *ask* the repository for information about this *item*.
 * [Stream]s and everything else, like [Subtitle]s, [Chapter]s, this **item**'s [Metadata],
 * preview thumbnails etc., basically things [NewPlayer] might want to know about an **item**, is
 * supplied through the repository.
 *
 * ## What is an item
 * An item is a string or tag that uniquely identifies one specific piece or unit of media.
 * Something like a youtube video.
 * For example an **item** can be the link to a youtube video or a media.ccc video, or the filepath
 * and name of a video stored on your phone.
 *
 * You can also use uuid's or randomly generated values to create items. You basically define
 * what an item should be, as long as the string meets these two criteria:
 * - The string must be unique for one piece of media (the item must be relational)
 * - The repository must be able to understand that item string and be able to return
 *   the information related to that **item**.
 *
 * So the way you identify and use **item** with [NewPlayer] depends on how [MediaRepository]
 * is implemented.
 */
interface MediaRepository {

    /**
     * Returns the information of the repository. Please see the documentation for [RepoMetaInfo]
     * for more details.
     */
    fun getRepoInfo(): RepoMetaInfo

    /**
     * Supply a custom [HttpDataSource.Factory]. This is important for Youtube.
     */
    fun getHttpDataSourceFactory(item: String): HttpDataSource.Factory =
        DefaultHttpDataSource.Factory()

    /**
     * Get MediaMetadata information for a certain item. Please refer to the media3 documentation
     * for [MediaMetadata].
     */
    suspend fun getMetaInfo(item: String): MediaMetadata

    /**
     * This should return all the [Stream]s associated with an **item**.
     */
    suspend fun getStreams(item: String): List<Stream>

    /**
     * This should return all the [Subtitle]s associated with an **item**.
     */
    suspend fun getSubtitles(item: String): List<Subtitle>

    /**
     * This should return a thumbnail associated with the provided timestamp of the provided
     * **item**. The reason this function does not simply return a Uri to a bitmap is that
     * some platforms like Youtube provide the preview thumbnail as a collage of multiple
     * thumbnails within one jpeg file. It is then the task of the client to crop this jpeg to only
     * show the piece of that image that depicts the thumbnail for the given timestamp.
     *
     * So because of this you will have to download, or precache, the thumbnails by yourself.
     */
    suspend fun getPreviewThumbnail(item: String, timestampInMs: Long): Bitmap?

    /**
     * This should return the chapters associated with an **item**. If an **item** does not have
     * chapters you may return an empty list.
     */
    suspend fun getChapters(item: String): List<Chapter>

    /**
     * Should return the link to a certain timestamp of the video/audio stream associated by **item**.
     * An example for a timestamp link would be this (don't watch it though its dangerous):
     * https://www.youtube.com/watch?v=H8ZH_mkfPUY&t=19s
     */
    suspend fun getTimestampLink(item: String, timestampInSeconds: Long): String
}