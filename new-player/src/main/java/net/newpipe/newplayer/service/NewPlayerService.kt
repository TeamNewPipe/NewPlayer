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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.PlayMode
import javax.inject.Inject

private const val TAG = "NewPlayerService"

@AndroidEntryPoint
class NewPlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var customCommands: List<CustomCommand>

    @Inject
    lateinit var newPlayer: NewPlayer

    private var serviceScope = CoroutineScope(Dispatchers.Main + Job())

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        customCommands = buildCustomCommandList(this)

        if(newPlayer.exoPlayer.value != null) {
            mediaSession = MediaSession.Builder(this, newPlayer.exoPlayer.value!!)
                .setCallback(object : MediaSession.Callback {
                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ): MediaSession.ConnectionResult {
                        val connectionResult = super.onConnect(session, controller)
                        val availableSessionCommands =
                            connectionResult.availableSessionCommands.buildUpon()

                        customCommands.forEach { command ->
                            command.commandButton.sessionCommand?.let {
                                availableSessionCommands.add(it)
                            }
                        }

                        return MediaSession.ConnectionResult.accept(
                            availableSessionCommands.build(),
                            connectionResult.availablePlayerCommands
                        )
                    }

                    override fun onPostConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ) {
                        super.onPostConnect(session, controller)
                        mediaSession?.setCustomLayout(customCommands.map { it.commandButton })
                    }

                    override fun onCustomCommand(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        customCommand: SessionCommand,
                        args: Bundle
                    ): ListenableFuture<SessionResult> {
                        when (customCommand.customAction) {
                            CustomCommand.NEW_PLAYER_NOTIFICATION_COMMAND_CLOSE_PLAYBACK -> {
                                newPlayer.release()
                            }

                            else -> {
                                Log.e(TAG, "Unknown custom command: ${customCommand.customAction}")
                                return Futures.immediateFuture(SessionResult(SessionError.ERROR_NOT_SUPPORTED))
                            }
                        }
                        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                    }

                })
                .build()
        } else {
            stopSelf()
        }


        serviceScope.launch {
            newPlayer.playBackMode.collect { mode ->
                if (mode == PlayMode.IDLE) {
                    stopSelf()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        newPlayer.release()
        mediaSession?.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Check if the player is not ready to play or there are no items in the media queue
        if (!(newPlayer.exoPlayer.value?.playWhenReady
                ?: false) || newPlayer.playlist.value.size == 0
        ) {
            // Stop the service
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession
}