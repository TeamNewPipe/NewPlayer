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