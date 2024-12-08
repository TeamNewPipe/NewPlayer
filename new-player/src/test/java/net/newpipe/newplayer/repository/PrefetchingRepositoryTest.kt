package net.newpipe.newplayer.repository;

import io.mockk.clearMocks
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrefetchingRepositoryTest {
    val mockMediaRepository = spyk(MockMediaRepository())
    val cachingRepository = spyk(CachingRepository(mockMediaRepository))
    val repository = PrefetchingRepository(cachingRepository)

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
    fun clean() {
        clearMocks(mockMediaRepository)
        clearMocks(cachingRepository)
        repository.reset()
    }

    @Test
    fun getMetaInfo() = runTest {
        val metaInfo = repository.getMetaInfo("item")
        assertEquals("Test title", metaInfo.title)
    }

    @Test
    fun getMetaInfo_prefetchAllItemData() = runTest {
        repository.getMetaInfo("item")

        coVerify (exactly = 2) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
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
    fun getStreams_prefetchAllItemData() = runTest {
        repository.getStreams("item")

        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 2) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
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
    fun getSubtitles_prefetchAllItemData() = runTest {
        repository.getSubtitles("item")

        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 2) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
    }

    @Test
    fun getPreviewThumbnail() = runTest {
        val previewThumbnail = repository.getPreviewThumbnail("item", 1000)
        assertNull(previewThumbnail)
    }

    @Test
    fun getPreviewThumbnail_prefetchAllItemData() = runTest {
        repository.getPreviewThumbnail("item", 1000)

        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 2) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnail("item",1000) }
    }

    @Test
    fun getPreviewThumbnailsInfo() = runTest {
        val previewThumbnailsInfo = repository.getPreviewThumbnailsInfo("item")
        assertEquals(10, previewThumbnailsInfo.count)
        assertEquals(500, previewThumbnailsInfo.distanceInMS)
    }

    @Test
    fun getPreviewThumbnailsInfo_prefetchAllItemData() = runTest {
        repository.getPreviewThumbnailsInfo("item")

        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 2) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
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
    fun getChapters_prefetchAllItemData() = runTest {
        repository.getChapters("item")

        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 2) { mockMediaRepository.getChapters("item") }
    }

    @Test
    fun prefetch_prefetchInformationOfNewItem() = runTest {
        repository.prefetch("item")
        //TODO: understand why without this call the spy is not updated and the test fails
        repository.getTimestampLink("item", 1000)

        coVerify (exactly = 1) { mockMediaRepository.getMetaInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getStreams("item") }
        coVerify (exactly = 1) { mockMediaRepository.getSubtitles("item") }
        coVerify (exactly = 1) { mockMediaRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 1) { mockMediaRepository.getChapters("item") }
    }

    @Test
    fun prefetch_notPrefetchInformationOfAlreadySeenItem() = runTest {
        repository.getMetaInfo("item")
        clearMocks(cachingRepository)
        repository.prefetch("item")

        coVerify (exactly = 0) { cachingRepository.getMetaInfo("item") }
        coVerify (exactly = 0) { cachingRepository.getStreams("item") }
        coVerify (exactly = 0) { cachingRepository.getSubtitles("item") }
        coVerify (exactly = 0) { cachingRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 0) { cachingRepository.getChapters("item") }
    }

    @Test
    fun reset_resetAlreadySeenItemsInfo() = runTest {
        repository.getMetaInfo("item")
        repository.reset()
        repository.getMetaInfo("item")
        repository.reset()
        repository.getMetaInfo("item")
        repository.reset()
        //TODO: understand why without this call the spy is not updated and the test fails
        repository.getTimestampLink("item", 1000)

        coVerify (exactly = 6) { cachingRepository.getMetaInfo("item") }
        coVerify (exactly = 3) { cachingRepository.getStreams("item") }
        coVerify (exactly = 3) { cachingRepository.getSubtitles("item") }
        coVerify (exactly = 3) { cachingRepository.getPreviewThumbnailsInfo("item") }
        coVerify (exactly = 3) { cachingRepository.getChapters("item") }
    }

}
