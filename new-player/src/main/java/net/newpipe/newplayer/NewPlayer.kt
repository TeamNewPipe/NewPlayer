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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.StreamTrack
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

    /**
     * Sets the default languages the user prefers. This can be overridden per stream
     * via [currentStreamLanguageConstraint].
     */
    var preferredStreamLanguages: List<String>

    /**
     * The Icon that should be shown in the media notification.
     */
    val notificationIcon: IconCompat

    /**
     * The class of the activity that holds the NewPlayerUI.
     */
    val playerActivityClass: Class<Activity>

    /**
     * The exoPlayer NewPlayer uses.
     * Since NewPlayer might kill its exoplayer depending on the playback state,
     * the player itself is wrapped in a StateFlow. This way the NewPlayerUI can be notified
     * of a new ExoPlayer being created once the playback state switches away from IDLE.
     */
    val exoPlayer: StateFlow<Player?>

    /**
     * Same as ExoPlayer's playWhenReady. See [Player.setPlayWhenReady]
     */
    var playWhenReady: Boolean

    /**
     * Same as ExoPlayer's duration. See [Player.getDuration]
     */
    val duration: Long

    /**
     * Same as ExoPlayer's bufferedPercentage. See [Player.getBufferedPercentage]
     */
    val bufferedPercentage: Int

    /**
     * Same as ExoPlayer's currentPosition. See [Player.getCurrentPosition]
     */
    var currentPosition: Long

    /**
     * Amount of seconds that should be skipped on a fast seek action.
     */
    var fastSeekAmountSec: Int

    val playBackMode: MutableStateFlow<PlayMode>

    /**
     * Same as ExoPlayer's shuffle. See [Player.getShuffleModeEnabled]
     */
    var shuffle: Boolean

    var repeatMode: RepeatMode
    val repository: MediaRepository

    val playlist: StateFlow<List<MediaItem>>
    val currentlyPlaying: StateFlow<MediaItem?>
    var currentlyPlayingPlaylistItem: Int

    val currentChapters: StateFlow<List<Chapter>>

    val currentlyPlayingTracks: StateFlow<List<StreamTrack>>
    val currentlyAvailableTracks: StateFlow<List<StreamTrack>>

    /**
     * Overrides the [preferredStreamLanguages] and picks one specific language
     * This value then only works for the current stream. If the stream changes this
     * value switches back to null, and the [preferredStreamLanguages] are used for stream selection.
     * This is used to pick one specific stream.
     */
    var currentStreamLanguageConstraint: String?

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
