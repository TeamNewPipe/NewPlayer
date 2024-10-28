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

package net.newpipe.newplayer.data


import net.newpipe.newplayer.data.PlayMode.IDLE
/**
 * The playlist repeat mode.
 */
enum class RepeatMode {
    /**
     * Don't repeat. Quit playback and switch to [IDLE] mode after being done playing the active
     * playlist.
     */
    DO_NOT_REPEAT,

    /**
     * Repeats the currently active playlist after playing the last item of the playlist.
     */
    REPEAT_ALL,

    /**
     * Keeps repeating the current item of a playlist.
     */
    REPEAT_ONE
}