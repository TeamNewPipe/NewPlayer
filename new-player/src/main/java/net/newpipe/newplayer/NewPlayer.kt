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

import android.app.Application
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.utils.PlayList
import kotlin.Exception

enum class PlayMode {
    EMBEDDED_VIDEO,
    FULLSCREEN_VIDEO,
    PIP,
    BACKGROUND,
    AUDIO_FOREGROUND,
}

private val TAG = "NewPlayer"

interface NewPlayer {
    // preferences
    val preferredStreamVariants: List<String>

    val internalPlayer: Player
    var playWhenReady: Boolean
    val duration: Long
    val bufferedPercentage: Int
    val repository: MediaRepository
    var currentPosition: Long
    var fastSeekAmountSec: Int
    var playBackMode: PlayMode
    var playMode : MutableStateFlow<PlayMode?>

    var playlist: PlayList

    // callbacks

    val errorFlow : SharedFlow<Exception>

    // methods
    fun prepare()
    fun play()
    fun pause()
    fun addToPlaylist(item: String)
    fun playStream(item: String, playMode: PlayMode)
    fun playStream(item: String, streamVariant: String, playMode: PlayMode)

    data class Builder(val app: Application, val repository: MediaRepository) {
        private var mediaSourceFactory: MediaSource.Factory? = null
        private var preferredStreamVariants: List<String> = emptyList()

        fun setMediaSourceFactory(mediaSourceFactory: MediaSource.Factory) {
            this.mediaSourceFactory = mediaSourceFactory
        }

        fun setPreferredStreamVariants(preferredStreamVariants: List<String>) {
            this.preferredStreamVariants = preferredStreamVariants
        }

        fun build(): NewPlayer {
            val exoPlayerBuilder = ExoPlayer.Builder(app)
            mediaSourceFactory?.let {
                exoPlayerBuilder.setMediaSourceFactory(it)
            }
            return NewPlayerImpl(
                app = app,
                internalPlayer = exoPlayerBuilder.build(),
                repository = repository,
                preferredStreamVariants = preferredStreamVariants
            )
        }
    }

}

class NewPlayerImpl(
    val app: Application,
    override val internalPlayer: Player,
    override val preferredStreamVariants: List<String>,
    override val repository: MediaRepository,
) : NewPlayer {

    var mutableErrorFlow =  MutableSharedFlow<Exception>()
    override val errorFlow = mutableErrorFlow.asSharedFlow()

    override val bufferedPercentage: Int
        get() = internalPlayer.bufferedPercentage
    override var currentPosition: Long
        get() = internalPlayer.currentPosition
        set(value) {
            internalPlayer.seekTo(value)
        }

    override var fastSeekAmountSec: Int = 10
    override var playBackMode: PlayMode = PlayMode.EMBEDDED_VIDEO

    private var playerScope = CoroutineScope(Dispatchers.Default + Job())

    override var playMode = MutableStateFlow<PlayMode?>(null)

    override var playWhenReady: Boolean
        set(value) {
            internalPlayer.playWhenReady = value
        }
        get() = internalPlayer.playWhenReady


    override val duration: Long
        get() = internalPlayer.duration

    override var playlist = PlayList(internalPlayer)

    init {
        internalPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                launchJobAndCollectError {
                    val item = internalPlayer.currentMediaItem?.mediaId
                    val newUri = repository.tryAndRescueError(item, exception = error)
                    if (newUri != null) {
                        TODO("Implement handing new uri on fixed error")
                    } else {
                        mutableErrorFlow.emit(error)
                    }
                }
            }
        })
    }

    override fun prepare() {
        internalPlayer.prepare()
    }

    override fun play() {
        if (internalPlayer.currentMediaItem != null) {
            internalPlayer.play()
        } else {
            Log.i(TAG, "Tried to start playing but no media Item was cued")
        }
    }

    override fun pause() {
        internalPlayer.pause()
    }

    override fun addToPlaylist(item: String) {
        launchJobAndCollectError {
            val mediaItem = toMediaItem(item)
            internalPlayer.addMediaItem(mediaItem)
        }
    }

    override fun playStream(item: String, playMode: PlayMode) {
        launchJobAndCollectError {
            val mediaItem = toMediaItem(item)
            internalPlayStream(mediaItem, playMode)
        }
    }

    override fun playStream(item: String, streamVariant: String, playMode: PlayMode) {
        launchJobAndCollectError {
            val stream = toMediaItem(item)
            internalPlayStream(stream, playMode)
        }
    }

    private fun internalPlayStream(mediaItem: MediaItem, playMode: PlayMode) {
        if (internalPlayer.playbackState == Player.STATE_IDLE) {
            internalPlayer.prepare()
        }
        this.playMode.update { playMode }
    }

    private suspend fun toMediaItem(item: String, streamVariant: String): MediaItem {
        val dataStream = repository.getStream(item, streamVariant)
        val mediaItem = MediaItem.Builder().setMediaId(item).setUri(dataStream)
        return mediaItem.build()
    }

    private suspend fun toMediaItem(item: String): MediaItem {

        val availableStream = repository.getAvailableStreamVariants(item)
        var selectedStream = availableStream[availableStream.size / 2]
        for (preferredStream in preferredStreamVariants) {
            if (preferredStream in availableStream) {
                selectedStream = preferredStream
                break;
            }
        }

        return toMediaItem(item, selectedStream)
    }

    private fun launchJobAndCollectError(task: suspend () -> Unit) =
        playerScope.launch {
            try {
                task()
            } catch (e: Exception) {
                mutableErrorFlow.emit(e)
            }
        }
}