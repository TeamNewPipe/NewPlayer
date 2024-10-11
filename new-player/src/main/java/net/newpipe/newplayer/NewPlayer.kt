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

import android.app.Activity
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.utils.Chapter
import net.newpipe.newplayer.utils.LanguageIdentifier
import net.newpipe.newplayer.utils.Stream
import kotlin.Exception

enum class PlayMode {
    IDLE,
    EMBEDDED_VIDEO,
    FULLSCREEN_VIDEO,
    PIP,
    BACKGROUND_VIDEO,
    BACKGROUND_AUDIO,
    FULLSCREEN_AUDIO,
    EMBEDDED_AUDIO
}

enum class RepeatMode {
    DO_NOT_REPEAT,
    REPEAT_ALL,
    REPEAT_ONE
}

interface NewPlayer {
    // preferences
    var preferredStreamLanguages: List<LanguageIdentifier>
    val notificationIcon: IconCompat
    val playerActivityClass: Class<Activity>

    val exoPlayer: StateFlow<Player?>
    var playWhenReady: Boolean
    val duration: Long
    val bufferedPercentage: Int
    var currentPosition: Long
    var fastSeekAmountSec: Int
    val playBackMode: MutableStateFlow<PlayMode>
    var shuffle: Boolean
    var repeatMode: RepeatMode
    val repository: MediaRepository

    val playlist: StateFlow<List<MediaItem>>
    val currentlyPlaying: StateFlow<MediaItem?>
    var currentlyPlayingPlaylistItem: Int

    val currentChapters: StateFlow<List<Chapter>>

    val currentlyPlayingStream: StateFlow<Stream?>
    val currentlyAvailableStreams: StateFlow<List<Stream>>

    // callbacks

    val errorFlow: SharedFlow<Exception>
    val onExoPlayerEvent: SharedFlow<Pair<Player, Player.Events>>

    // methods
    fun prepare()
    fun play()
    fun pause()
    fun addToPlaylist(item: String)
    fun movePlaylistItem(fromIndex: Int, toIndex: Int)
    fun removePlaylistItem(uniqueId: Long)
    fun playStream(item: String, playMode: PlayMode)
    fun selectChapter(index: Int)
    fun release()
    fun getItemFromMediaItem(mediaItem: MediaItem) : String
}
