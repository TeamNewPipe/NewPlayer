package net.newpipe.newplayer

import android.app.Activity
import android.app.Application
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.newpipe.newplayer.repository.DelayTestRepository
import net.newpipe.newplayer.repository.MockMediaRepository
import org.junit.BeforeClass
import org.junit.Test

//TODO
//  * onPlayBackError
//  * prepare
//  * play
//  * pause
//  * addToPlaylist
//  * movePlaylistItem
//  * removePlaylistItem
//  * playStream
//  * selectChapter
//  * release
//  * getItemFromMediaItem

@OptIn(ExperimentalCoroutinesApi::class)
class NewPlayerImpltest {
    val app = mockk<Application>(relaxed = true)
    val playerActivityClass = Activity::class.java
    val repository = DelayTestRepository(MockMediaRepository(), 100)
    val player = NewPlayerImpl(app, playerActivityClass, repository)

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

}