package net.newpipe.newplayer.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.junit.Assert.*
import org.junit.BeforeClass

@OptIn(ExperimentalCoroutinesApi::class)
class CachingRepositoryTest {
    val mockMediaRepository = MockMediaRepository()
    val delayTestRepository = DelayTestRepository(mockMediaRepository, 100)
    val repository = CachingRepository(delayTestRepository)

    companion object {
        @JvmStatic
        @BeforeClass
        fun init() {
            Dispatchers.setMain(StandardTestDispatcher())
        }

        @JvmStatic
        @BeforeClass
        fun reset() {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun getRepoInfo() {
        val repoInfo = repository.getRepoInfo()
        assertTrue(repoInfo.pullsDataFromNetwork)
        assertTrue(repoInfo.canHandleTimestampedLinks)
    }

    @Test
    fun getMetaInfo() = runTest {
        val metaInfo = repository.getMetaInfo("test")
        assertEquals("Test title", metaInfo.title)
    }

    @Test
    fun getStreams_returnsCorrectList() = runTest {
        val streams = repository.getStreams("item")
        assertEquals(3, streams.size)
        assertEquals("item1", streams.get(0).item)
        assertEquals("item2", streams.get(1).item)
        assertEquals("item3", streams.get(2).item)
    }

    @Test
    fun getSubtitles_returnsCorrectList() = runTest {
        val subtitles = repository.getSubtitles("item")
        assertEquals(3, subtitles.size)
        assertEquals("subtitle1", subtitles.get(0).identifier)
        assertEquals("subtitle2", subtitles.get(1).identifier)
        assertEquals("subtitle3", subtitles.get(2).identifier)
    }

    @Test
    fun getPreviewThumbnail() = runTest {
        val previewThumbnail = repository.getPreviewThumbnail("item", 1000)
        assertNull(previewThumbnail)
    }

    @Test
    fun getPreviewThumbnailInfo() = runTest {
        val previewThumbnailInfo = repository.getPreviewThumbnailsInfo("item")
        assertEquals(10, previewThumbnailInfo.count)
        assertEquals(500, previewThumbnailInfo.distanceInMS)
    }

    @Test
    fun getChapters_returnsCorrectList() = runTest {
        val chapters = repository.getChapters("item")
        assertEquals(3, chapters.size)
        assertEquals("chapter1", chapters.get(0).chapterTitle)
        assertEquals("chapter2", chapters.get(1).chapterTitle)
        assertEquals("chapter3", chapters.get(2).chapterTitle)
    }

    @Test
    fun getTimestampLink() = runTest {
        assertEquals("test/link", repository.getTimestampLink("item", 0))
    }

    @Test
    fun getHttpDataSourceFactory() {
        assertNotNull(repository.getHttpDataSourceFactory("item"))
    }
}