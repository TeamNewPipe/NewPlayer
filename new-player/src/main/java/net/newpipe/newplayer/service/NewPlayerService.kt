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


package net.newpipe.newplayer.service

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.NewPlayer
import javax.inject.Inject

@AndroidEntryPoint
class NewPlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var newPlayer: NewPlayer

    override fun onDestroy() {
        super.onDestroy()
        newPlayer.release()
        mediaSession?.release()
        mediaSession = null
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        println("gurken get session")
        if (mediaSession == null) {
            mediaSession = MediaSession.Builder(this, newPlayer.internalPlayer).build()
        }
        return mediaSession
    }
}