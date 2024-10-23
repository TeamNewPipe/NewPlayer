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
import net.newpipe.newplayer.data.NewPlayerException
import net.newpipe.newplayer.data.StreamTrack
import net.newpipe.newplayer.logic.StreamExceptionResponse
import net.newpipe.newplayer.logic.NoResponse
import kotlin.Exception
import android.app.Application
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.data.RepeatMode

/**
 * The default implementation of the NewPlayer interface is [NewPlayerImpl].
 *
 * An instance of NewPlayer will contain the business logic of the whole NewPlayer.
 * [NewPlayerImpl] will therefore also contain the instance of [Player].
 *
 * Keep ind mind that a NewPlayer instance must live in an [Application] instance.
 * Otherwise the NewPlayer instance can not outlive an Activity or Service. NewPlayer must be
 * able to do this though in order to
 * 1. Run in background
 * 2. If its executed on a TV: Stop when the main activity is closed. (The reason the NewPlayer
 * instance should not be hosted inside a MediaService, since on TV there should not be a playback
 * service.)
 */
interface NewPlayer {
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
    val playerActivityClass: Class<out Activity>

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

    /**
     * Same as ExoPlayer's repeatMode. See [Player.getRepeatMode]
     * However, ExoPlayer's repeatMode ist based on an integer, while NewPayer defines it as
     * an enum class.
     */
    var repeatMode: RepeatMode

    /**
     * The media repository from which media data is pulled from. This has to be provided by
     * the user.
     */
    val repository: MediaRepository

    /**
     * Represents the current playlist of ExoPlayer. It is derived from
     * ExoPlayer's [Player.getCurrentTimeline], and its updated on a timeline change.
     */
    val playlist: StateFlow<List<MediaItem>>

    /**
     * The currently playing MediaItem. If null no MediaItem is playing.
     */
    val currentlyPlaying: StateFlow<MediaItem?>

    /**
     * The index into the [playlist]. It points to the currently playing MediaItem.
     */
    var currentlyPlayingPlaylistItem: Int

    /**
     * The chapters which are available for the currently playing stream.
     * If the current stream has no chapters the list will be empty.
     */
    val currentChapters: StateFlow<List<Chapter>>

    /**
     * The tracks that are currently reproduced by ExoPlayer
     */
    val currentlyPlayingTracks: StateFlow<List<StreamTrack>>

    /**
     * All the tracks that the current item provides.
     * The list contains the tracks of all available stream.
     */
    val currentlyAvailableTracks: StateFlow<List<StreamTrack>>

    /**
     * Overrides the [preferredStreamLanguages] and picks one specific language
     * This value then only works for the current stream. If the stream changes this
     * value switches back to null, and the [preferredStreamLanguages] are used for stream selection.
     * This is used to pick one specific stream.
     */
    var currentStreamLanguageConstraint: String?

    /**
     * This will emit errors that can not be recovered.
     * The collector should not try to handle the errors but instead directly forward it
     * to the central exception management.
     *
     * For handling exceptions and potentially recover from them, the error handling function
     * [rescueStreamFault] should be implemented.
     */
    val errorFlow: SharedFlow<Exception>

    /**
     * This callback allows to recover from errors that happened during stream playback.
     * When this callback is called the user has a chance to recover from that error.
     * By returning a [StreamExceptionResponse] the user can decide how NewPlayer should continue.
     * The default implementation by [NewPlayerImpl] will always return [NoResponse]. This will
     * cause the exception to be directly forwarded to [errorFlow].
     *
     * @param item the item of the stream during which playback failed.
     * @param mediaItem the [MediaItem] of the stream during which playback failed.
     * @param excpetion the exception that occurred in ExoPlayer.
     * @param repository the [MediaRepository] that NewPlayer utilizes.
     * @return an implementation of StreamExceptionResponse. This tells NewPlayer how to deal with the exception.
     */
    val rescueStreamFault: suspend (
        item: String?,
        mediaItem: MediaItem?,
        exception: Exception,
        repository: MediaRepository
    ) -> StreamExceptionResponse

    val onExoPlayerEvent: SharedFlow<Pair<Player, Player.Events>>

    // methods

    /**
     * Same as ExoPlayer's [Player.prepare].
     */
    fun prepare()

    /**
     * Same as ExoPlayer's [Player.play].
     */
    fun play()

    /**
     * Same as ExoPlayer's [Player.pause]
     */
    fun pause()

    /**
     * Adds a new item to the [playlist].
     */
    fun addToPlaylist(item: String)

    // TODO probably need also functions to enqueue next, add multiple items, enqueue next multiple
    //  items

    /**
     * Same as ExoPlayer's movePlaylistItem. See [Player.moveMediaItem]
     */
    fun movePlaylistItem(fromIndex: Int, toIndex: Int)

    /**
     * Same as ExoPlayer's removePlaylistItem. See [Player.removeMediaItem]
     */
    fun removePlaylistItem(uniqueId: Long)

    /**
     * Starts playing a new item. This function will clear the [playlist],
     * and start with a new playlist that only contains [item].
     * @param item the item that should be played.
     * @playMode the mode the item should be displayed in.
     */
    fun playStream(item: String, playMode: PlayMode)

    /**
     * This will seek to the chapter selected by [index].
     * If its tried to select a chapter which does not exist an out of bound exception is though.
     *
     * Please mind that selecting a chapter can lead to a race condition. This can happen when
     * ExoPlayer changes the track while a chapter is selected.
     * Because of this you must always wrap a call to this function in a try/catch statement.
     *
     * @param index the index of the chapter to which to seek.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun selectChapter(index: Int)

    /**
     * Same as ExoPlayer's release. See [Player.release]
     * In addition to releasing the internal ExoPlayer, NewPlayer will also clean up other things
     * that do not need to exist while NewPlayer is in IDLE mode. This includes the ExoPlayer
     * instance itself.
     */
    fun release()

    /**
     * Returns the Item corresponding to the [MediaItem]
     * Please keep int mind that only [MediaItem] re supported that were created by NewPlayer
     * itself. Otherwise the corresponding item can not be found and the function throws a
     * [NewPlayerException].
     *
     * @param mediaItem the MediaItem to which the item should be found
     * @returns the item that corresponds to [mediaItem]
     */
    @Throws(NewPlayerException::class)
    fun getItemFromMediaItem(mediaItem: MediaItem): String
}
