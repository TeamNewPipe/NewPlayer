package net.newpipe.newplayer

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer


interface NewPlayer {
    val player: Player

    data class Builder(val app: Application) {
        fun build(): NewPlayer {
            return NewPlayerImpl(ExoPlayer.Builder(app).build())
        }
    }
}

class NewPlayerImpl(internal_player: Player) : NewPlayer {
    override val player = internal_player
}