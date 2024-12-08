package net.newpipe.newplayer.repository

import io.mockk.clearMocks
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore

@OptIn(ExperimentalCoroutinesApi::class)
class CachingRepositoryTest {
    val mockMediaRepository = spyk(MockMediaRepository())
    val repository = CachingRepository(mockMediaRepository)

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

    @Before
    fun resetSpy() {
        clearMocks(mockMediaRepository)
    }

    @Test
    fun getRepoInfo() {
        val repoInfo = repository.getRepoInfo()
        assertTrue(repoInfo.pullsDataFromNetwork)
        assertTrue(repoInfo.canHandleTimestampedLinks)
    }

    @Test
    fun getMetaInfo() = runTest {
        val metaInfo = repository.getMetaInfo("item")
        assertEquals("Test title", metaInfo.title)
    }

    @Test
    fun getMetaInfo_callActualRepositoryOnceForSameItem() = runTest {
        repository.getMetaInfo("item")
        repository.getMetaInfo("item")
        repository.getMetaInfo("item")
        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
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
    fun getStreams_callActualRepositoryOnceForSameItem() = runTest {
        repository.getStreams("item")
        repository.getStreams("item")
        repository.getStreams("item")
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
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
    fun getSubtitles_callActualRepositoryOnceForSameItem() = runTest {
        repository.getSubtitles("item")
        repository.getSubtitles("item")
        repository.getSubtitles("item")
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
    }

    @Test
    fun getPreviewThumbnail() = runTest {
        val previewThumbnail = repository.getPreviewThumbnail("item", 1000)
        assertNull(previewThumbnail)
    }

    @Test
    fun getPreviewThumbnail_callActualRepositoryOnceForSameItem() = runTest {
        repository.getPreviewThumbnail("item", 1000)
        repository.getPreviewThumbnail("item", 1000)
        repository.getPreviewThumbnail("item", 1000)
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnail("item", 1000) }
    }

    @Test
    fun getPreviewThumbnailsInfo() = runTest {
        val previewThumbnailsInfo = repository.getPreviewThumbnailsInfo("item")
        assertEquals(10, previewThumbnailsInfo.count)
        assertEquals(500, previewThumbnailsInfo.distanceInMS)
    }

    @Test
    fun getPreviewThumbnailsInfo_callActualRepositoryOnceForSameItem() = runTest {
        repository.getPreviewThumbnailsInfo("item")
        repository.getPreviewThumbnailsInfo("item")
        repository.getPreviewThumbnailsInfo("item")
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
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
    fun getChapters_callActualRepositoryOnceForSameItem() = runTest {
        repository.getChapters("item")
        repository.getChapters("item")
        repository.getChapters("item")
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
    }

    @Test
    fun getTimestampLink() = runTest {
        assertEquals("test/link", repository.getTimestampLink("item", 0))
    }

    @Test
    fun getTimestampLink_callActualRepositoryOnceForSameItem() = runTest {
        repository.getTimestampLink("item", 0)
        repository.getTimestampLink("item", 0)
        repository.getTimestampLink("item", 0)
        coVerify (exactly = 1) { mockMediaRepository.getTimestampLink("item", 0) }
    }

    @Test
    fun getHttpDataSourceFactory() {
        assertNotNull(repository.getHttpDataSourceFactory("item"))
    }

    @Ignore("Test flush if the job is cancelled using cacheRepoScope and the test is interrupted")
    @Test
    fun flush_flushTheCaches() = runTest {
        repository.getMetaInfo("item")
        repository.getStreams("item")
        repository.getSubtitles("item")
        repository.getPreviewThumbnail("item", 1000)
        repository.getPreviewThumbnailsInfo("item")
        repository.getChapters("item")
        repository.getTimestampLink("item", 0)

        repository.flush()

        repository.getMetaInfo("item")
        repository.getStreams("item")
        repository.getSubtitles("item")
        repository.getPreviewThumbnail("item", 1000)
        repository.getPreviewThumbnailsInfo("item")
        repository.getChapters("item")
        repository.getTimestampLink("item", 0)

        coVerify (exactly = 2) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 2) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 2) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 2) { mockMediaRepository.getPreviewThumbnail("item", 1000) }
        coVerify (exactly = 2) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 2) { mockMediaRepository.getChapters("item") }
        coVerify (exactly = 2) { mockMediaRepository.getTimestampLink("item", 0) }
    }
}