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


interface NewPlayer {
    val player: Player
    var playWhenReady: Boolean

    fun prepare()
    fun play()
    fun pause()

    //TODO: This is only temporary
    fun setStream(uri: String)

    data class Builder(val app: Application) {
        fun build(): NewPlayer {
            return NewPlayerImpl(ExoPlayer.Builder(app).build())
        }
    }
}

class NewPlayerImpl(internal_player: Player) : NewPlayer {
    override val player = internal_player

    override var playWhenReady: Boolean
        set(value) {
            player.playWhenReady = value
        }
        get() = player.playWhenReady

    override fun prepare() {
        player.prepare()
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }


    override fun setStream(uri: String) {
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }

        player.setMediaItem(MediaItem.fromUri(uri))
    }
}