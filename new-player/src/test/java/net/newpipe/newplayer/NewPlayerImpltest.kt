package net.newpipe.newplayer

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.Futures
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.newpipe.newplayer.repository.DelayTestRepository
import net.newpipe.newplayer.repository.MockMediaRepository
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewPlayerImpltest {
    val mockExoPlayer = mockk<ExoPlayer>(relaxed = true)
    val mockMediaController = mockk<MediaController>(relaxed = true)
    val mockApp = mockk<Application>(relaxed = true)
    val playerActivityClass = Activity::class.java
    val repository = DelayTestRepository(MockMediaRepository(), 100)
    var player = NewPlayerImpl(mockApp, playerActivityClass, repository)

    init {
        mockkStatic(Looper::class)
        mockkStatic(TextUtils::class)
        mockkStatic(Log::class)
        mockkStatic(ExoPlayer::class)
        mockkStatic(ExoPlayer.Builder::class)
        mockkStatic(TextUtils::class)
        mockkConstructor(ComponentName::class)
        mockkConstructor(Intent::class)
        mockkConstructor(SessionToken::class)
        mockkConstructor(ExoPlayer.Builder::class)
        mockkConstructor(MediaController.Builder::class)

        val mockLooper = mockk<Looper>(relaxed = true)
        val mockPackageManager = mockk<PackageManager>(relaxed = true)
        val mockResolveInfo = mockk<ResolveInfo>(relaxed = true)
        val mockServiceInfo = mockk<ServiceInfo>(relaxed = true)
        mockResolveInfo.serviceInfo = mockServiceInfo
        mockServiceInfo.name = "ComponentNameTest"

        every { Looper.myLooper() } returns mockLooper
        every { TextUtils.isEmpty(any()) } returns true
        every { Log.i(any(), any()) } returns 1
        every { anyConstructed<ComponentName>().packageName } returns "net.newpipe.newplayer.test"
        every { anyConstructed<ComponentName>().className } returns "ComponentNameTest"
        every { anyConstructed<ComponentName>().hashCode() } returns 1
        every { anyConstructed<Intent>().setPackage(any()) } returns mockk<Intent>(relaxed = true)
        every { anyConstructed<ExoPlayer.Builder>().build() } returns mockExoPlayer
        every { TextUtils.equals(any(), any()) } returns true
        every { anyConstructed<MediaController.Builder>().buildAsync() } returns Futures.immediateFuture(mockMediaController)
        every { mockApp.packageManager } returns mockPackageManager
        every { mockPackageManager.queryIntentServices(any(), any<Int>()) } returns listOf(mockResolveInfo)
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun init() {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }

        @JvmStatic
        @BeforeClass
        fun reset() {
            Dispatchers.resetMain()
        }
    }

    @Before
    fun resetSpy() {
        clearMocks(mockExoPlayer)
        player = NewPlayerImpl(mockApp, playerActivityClass, repository)
    }

    @Test
    fun onPlayBackError_pauseExoPlayer() {
        player.prepare()
        player.onPlayBackError(Exception("test"))
        verify (exactly = 1) { mockExoPlayer.pause() }
    }

    @Test
    fun prepare_setupNewExoplayer() {
        player.prepare()
        verify (exactly = 1) { anyConstructed<ExoPlayer.Builder>().build() }
    }

    @Test
    fun prepare_notSetupNewExoPlayerWhenAlreadySetUp() {
        // Setup new ExoPlayer
        player.prepare()

        // Call prepare with an already set up ExoPlayer
        player.prepare()

        //ExoPlayer should be built only one time
        verify (exactly = 1) { anyConstructed<ExoPlayer.Builder>().build() }
    }

    @Test
    fun prepare_prepareExoPlayer() {
        player.prepare()
        verify (exactly = 1) { mockExoPlayer.prepare() }
    }

    @Test
    fun prepare_setUpMediaController() {
        player.prepare()
        verify (exactly = 1) { anyConstructed<MediaController.Builder>().buildAsync() }
    }

    @Test
    fun prepare_notSetUpMediaController() {
        // Setup media controller
        player.prepare()

        // Call prepare with an already set up media controller
        player.prepare()

        //Media controller should be built only one time
        verify (exactly = 1) { anyConstructed<MediaController.Builder>().buildAsync() }
    }

    @Test
    fun play_playIfCurrentMediaItemIsNotNull() {
        player.prepare()
        player.play()
        verify (exactly = 1) { mockExoPlayer.play() }
    }

    @Test
    fun play_notPlayIfCurrentMediaItemIsNull() {
        player.prepare()
        every { mockExoPlayer.currentMediaItem } returns null
        player.play()
        verify (exactly = 0) { mockExoPlayer.play() }
    }

    @Test
    fun pause() {
        player.prepare()
        player.pause()
        verify (exactly = 1) { mockExoPlayer.pause() }
    }

    @Test
    fun addToPlaylist_prepareExoPlayerIfNotPrepared() {
        player.addToPlaylist("item")
        verify (exactly = 1) { mockExoPlayer.prepare() }
    }

    @Test
    fun addToPlaylist_notPrepareExoPlayerIfAlreadyPrepared() {
        player.prepare()
        clearMocks(mockExoPlayer)
        player.addToPlaylist("item")
        verify (exactly = 0) { mockExoPlayer.prepare() }
    }

    @Ignore("Find a way to test code inside launchJobAndCollectError")
    @Test
    fun addToPlaylist_addMediaSource() {
        player.addToPlaylist("item")
//        try {
//            Dispatchers.Main.job.join()
//        } catch (e : Exception) { }
//        coVerify (exactly = 2) { mockExoPlayer.addMediaSource(any()) }
    }

    @Test
    fun movePlaylistItem() {
        player.prepare()
        player.movePlaylistItem(0, 1)
        verify (exactly = 1){ mockExoPlayer.moveMediaItem(0, 1) }
    }

    @Test
    fun removePlaylistItem_removeItem() {
        player.prepare()
        val mediaItem = MediaItem.Builder().setMediaId("123").build()
        every { mockExoPlayer.mediaItemCount } returns 1
        every { mockExoPlayer.getMediaItemAt(any()) } returns mediaItem
        player.removePlaylistItem(123)
        verify (exactly = 1) { mockExoPlayer.removeMediaItem(0) }
    }

    @Test
    fun removePlaylistItem_notRemoveItem() {
        player.prepare()
        val mediaItem = MediaItem.Builder().setMediaId("123").build()
        every { mockExoPlayer.mediaItemCount } returns 1
        every { mockExoPlayer.getMediaItemAt(any()) } returns mediaItem
        player.removePlaylistItem(124)
        verify (exactly = 0) { mockExoPlayer.removeMediaItem(0) }
    }

    @Ignore("Mock currentChapters.value and test the selection of a chapter")
    @Test
    fun selectChapter() {
        player.selectChapter(0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun selectChapter_throwsException() {
        player.selectChapter(3)
    }

    @Test
    fun release() {
        player.prepare()
        player.release()
        verify (exactly = 1) { mockMediaController.release() }
        verify (exactly = 1) { mockExoPlayer.release() }
    }

}