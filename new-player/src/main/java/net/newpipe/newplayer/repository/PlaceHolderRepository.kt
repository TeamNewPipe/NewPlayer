package net.newpipe.newplayer.repository

import androidx.media3.common.MediaMetadata
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle

/**
 * This is a simple placeholder repository that will return no information. It can be used
 * during development to be able to already lay out UI elements or setup and text compile
 * NewPlayer without actually having a functioning media repository.
 */
class PlaceHolderRepository : MediaRepository {
    override fun getRepoInfo() =
        RepoMetaInfo(canHandleTimestampedLinks = true, pullsDataFromNetwork = false)


    override suspend fun getMetaInfo(item: String) = MediaMetadata.Builder().build()

    override suspend fun getStreams(item: String) = emptyList<Stream>()

    override suspend fun getSubtitles(item: String) = emptyList<Subtitle>()

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long) = null

    override suspend fun getChapters(item: String) = emptyList<Chapter>()

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) = ""
}