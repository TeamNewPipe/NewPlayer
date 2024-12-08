package net.newpipe.newplayer.repository

import android.net.Uri
import androidx.media3.common.MediaMetadata
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle

/**
 * Simple MediaRepository with mock values
 */
class MockMediaRepository : MediaRepository {
    val uriMock: Uri

    init {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk("Uri")
        uriMock = Uri.parse("test/uri")
    }

    override fun getRepoInfo() = MediaRepository.RepoMetaInfo(canHandleTimestampedLinks = true, pullsDataFromNetwork = true)

    override suspend fun getMetaInfo(item: String) = MediaMetadata.Builder().setTitle("Test title").build()

    override suspend fun getStreams(item: String) = listOf(
        Stream("item1", uriMock, emptyList()),
        Stream("item2", uriMock, emptyList()),
        Stream("item3", uriMock, emptyList())
    )

    override suspend fun getSubtitles(item: String) = listOf(
        Subtitle(uriMock, "subtitle1"),
        Subtitle(uriMock, "subtitle2"),
        Subtitle(uriMock, "subtitle3")
    )

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long) = null

    override suspend fun getPreviewThumbnailsInfo(item: String) = MediaRepository.PreviewThumbnailsInfo(10, 500)

    override suspend fun getChapters(item: String) = listOf(
        Chapter(0, "chapter1", null),
        Chapter(5000, "chapter2", null),
        Chapter(1000, "chapter3", null),
    )

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) = "test/link"

    override fun getHttpDataSourceFactory(item: String): HttpDataSource.Factory {
        val factory = DefaultHttpDataSource.Factory()
        factory.setUserAgent("TestUserAgent")
        return factory
    }
}