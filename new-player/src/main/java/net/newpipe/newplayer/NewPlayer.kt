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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.lang.Exception

enum class PlayMode {
    EMBEDDED_VIDEO,
    FULLSCREEN_VIDEO,
    PIP,
    BACKGROND,
    AUDIO_FORGROUND,
}

interface NewPlayer {
    val internal_player: Player
    var playWhenReady: Boolean
    val duartion: Long
    val bufferedPercentage: Int
    val repository: MediaRepository
    var currentPosition: Long
    var fastSeekAmountSec: Long
    var playBackMode: PlayMode
    var playList: MutableList<String>

    fun prepare()
    fun play()
    fun pause()
    fun fastSeekForward()
    fun fastSeekBackward()
    fun addToPlaylist(newItem: String)
    fun addListener(callbackListener: Listener)

    //TODO: This is only temporary
    fun setStream(uri: String)

    data class Builder(val app: Application, val repository: MediaRepository) {
        fun build(): NewPlayer {
            return NewPlayerImpl(ExoPlayer.Builder(app).build(), repository = repository)
        }
    }

    interface Listener {
        fun onError(exception: Exception)
    }
}

class NewPlayerImpl(override val internal_player: Player, override val repository: MediaRepository) : NewPlayer {
    override val duartion: Long = internal_player.duration
    override val bufferedPercentage: Int = internal_player.bufferedPercentage
    override var currentPosition: Long = internal_player.currentPosition
    override var fastSeekAmountSec: Long = 100
    override var playBackMode: PlayMode = PlayMode.EMBEDDED_VIDEO
    override var playList: MutableList<String> = ArrayList<String>()

    override var playWhenReady: Boolean
        set(value) {
            internal_player.playWhenReady = value
        }
        get() = internal_player.playWhenReady

    override fun prepare() {
        internal_player.prepare()
    }

    override fun play() {
        internal_player.play()
    }

    override fun pause() {
        internal_player.pause()
    }

    override fun fastSeekForward() {
        TODO("Not yet implemented")
    }

    override fun fastSeekBackward() {
        TODO("Not yet implemented")
    }

    override fun addToPlaylist(newItem: String) {
        TODO("Not yet implemented")
    }

    override fun addListener(callbackListener: NewPlayer.Listener) {
        TODO("Not yet implemented")
    }


    override fun setStream(uri: String) {
        if (internal_player.playbackState == Player.STATE_IDLE) {
            internal_player.prepare()
        }

        internal_player.setMediaItem(MediaItem.fromUri(uri))
    }
}