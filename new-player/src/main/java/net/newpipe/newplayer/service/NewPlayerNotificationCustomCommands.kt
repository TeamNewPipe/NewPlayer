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
 *
 */

package net.newpipe.newplayer.service

import android.content.Context
import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import net.newpipe.newplayer.R
import net.newpipe.newplayer.NewPlayerImpl

internal data class CustomCommand(
    val action: String,
    val commandButton: CommandButton
) {
    companion object {
        const val NEW_PLAYER_NOTIFICATION_COMMAND_CLOSE_PLAYBACK = "NEW_PLAYER_CLOSE_PLAYBACK"
    }
}

/**
 * There could be more custom commands, other then just "Close".
 * In order to match NewPipe's current implementation there could also be a mechanism for
 * the [NewPlayerImpl] object that allows you to define which commands should be visible.
 * This way the user can decide which additional commands should be shown.
 */
internal fun buildCustomCommandList(context: Context) = listOf(
    CustomCommand(
        CustomCommand.NEW_PLAYER_NOTIFICATION_COMMAND_CLOSE_PLAYBACK,
        CommandButton.Builder()
            .setDisplayName(context.getString(R.string.close))
            .setDisplayName("Close")
            .setSessionCommand(
                SessionCommand(
                    CustomCommand.NEW_PLAYER_NOTIFICATION_COMMAND_CLOSE_PLAYBACK,
                    Bundle()
                )
            )
            .setIconResId(R.drawable.close_24px)
            .build()
    )
)

