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
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.newpipe.newplayer.playerInternals.PlaylistItem
import net.newpipe.newplayer.playerInternals.fetchPlaylistItem
import net.newpipe.newplayer.playerInternals.getPlaylistItemsFromExoplayer
import kotlin.Exception
import kotlin.random.Random

enum class PlayMode {
    EMBEDDED_VIDEO,
    FULLSCREEN_VIDEO,
    PIP,
    BACKGROUND,
    AUDIO_FOREGROUND,
}

enum class RepeatMode {
    DONT_REPEAT,
    REPEAT_ALL,
    REPEAT_ONE
}

private val TAG = "NewPlayer"

interface NewPlayer {
    // preferences
    val preferredStreamVariants: List<String>

    val internalPlayer: Player
    var playWhenReady: Boolean
    val duration: Long
    val bufferedPercentage: Int
    val sharingLinkWithOffsetPossible: Boolean
    var currentPosition: Long
    var fastSeekAmountSec: Int
    val playBackMode: MutableStateFlow<PlayMode?>
    var shuffle: Boolean
    var repeatMode: RepeatMode

    val playlist: StateFlow<List<PlaylistItem>>
    val currentlyPlaying: StateFlow<PlaylistItem?>
    var currentlyPlayingPlaylistItem: Int

    val currentChapters: StateFlow<List<Chapter>>

    // callbacks

    val errorFlow: SharedFlow<Exception>
    val onExoPlayerEvent: SharedFlow<Pair<Player, Player.Events>>

    // methods
    fun prepare()
    fun play()
    fun pause()
    fun addToPlaylist(item: String)
    fun movePlaylistItem(fromIndex: Int, toIndex: Int)
    fun removePlaylistItem(index: Int)
    fun playStream(item: String, playMode: PlayMode)
    fun selectChapter(index: Int)
    fun playStream(item: String, streamVariant: String, playMode: PlayMode)

    data class Builder(val app: Application, val repository: MediaRepository) {
        private var mediaSourceFactory: MediaSource.Factory? = null
        private var preferredStreamVariants: List<String> = emptyList()
        private var sharingLinkWithOffsetPossible = false

        fun setMediaSourceFactory(mediaSourceFactory: MediaSource.Factory): Builder {
            this.mediaSourceFactory = mediaSourceFactory
            return this
        }

        fun setPreferredStreamVariants(preferredStreamVariants: List<String>): Builder {
            this.preferredStreamVariants = preferredStreamVariants
            return this
        }

        fun setSharingLinkWithOffsetPossible(possible: Boolean): Builder {
            this.sharingLinkWithOffsetPossible = false
            return this
        }

        fun build(): NewPlayer {
            val exoPlayerBuilder = ExoPlayer.Builder(app)
            mediaSourceFactory?.let {
                exoPlayerBuilder.setMediaSourceFactory(it)
            }
            return NewPlayerImpl(
                app = app,
                internalPlayer = exoPlayerBuilder.build(),
                repository = repository,
                preferredStreamVariants = preferredStreamVariants,
                sharingLinkWithOffsetPossible = sharingLinkWithOffsetPossible
            )
        }
    }

}

class NewPlayerImpl(
    val app: Application,
    override val internalPlayer: Player,
    override val preferredStreamVariants: List<String>,
    private val repository: MediaRepository,
    override val sharingLinkWithOffsetPossible: Boolean
) : NewPlayer {

    private var uniqueIdToIdLookup = HashMap<Long, String>()

    var mutableErrorFlow = MutableSharedFlow<Exception>()
    override val errorFlow = mutableErrorFlow.asSharedFlow()

    override val bufferedPercentage: Int
        get() = internalPlayer.bufferedPercentage

    override var currentPosition: Long
        get() = internalPlayer.currentPosition
        set(value) {
            internalPlayer.seekTo(value)
        }

    override var fastSeekAmountSec: Int = 10

    private var playerScope = CoroutineScope(Dispatchers.Main + Job())

    override var playBackMode = MutableStateFlow<PlayMode?>(null)

    override var shuffle: Boolean
        get() = internalPlayer.shuffleModeEnabled
        set(value) {
            internalPlayer.shuffleModeEnabled = value
        }

    override var repeatMode: RepeatMode
        get() = when (internalPlayer.repeatMode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.DONT_REPEAT
            Player.REPEAT_MODE_ALL -> RepeatMode.REPEAT_ALL
            Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_ONE
            else -> throw NewPlayerException("Unknown Repeatmode option returned by ExoPlayer: ${internalPlayer.repeatMode}")
        }
        set(value) {
            when (value) {
                RepeatMode.DONT_REPEAT -> internalPlayer.repeatMode = Player.REPEAT_MODE_OFF
                RepeatMode.REPEAT_ALL -> internalPlayer.repeatMode = Player.REPEAT_MODE_ALL
                RepeatMode.REPEAT_ONE -> internalPlayer.repeatMode = Player.REPEAT_MODE_ONE
            }
        }

    private var mutableOnEvent = MutableSharedFlow<Pair<Player, Player.Events>>()
    override val onExoPlayerEvent: SharedFlow<Pair<Player, Player.Events>> =
        mutableOnEvent.asSharedFlow()

    override var playWhenReady: Boolean
        set(value) {
            internalPlayer.playWhenReady = value
        }
        get() = internalPlayer.playWhenReady


    override val duration: Long
        get() = internalPlayer.duration

    private val mutablePlaylist = MutableStateFlow<List<PlaylistItem>>(emptyList())
    override val playlist: StateFlow<List<PlaylistItem>> =
        mutablePlaylist.asStateFlow()

    private val mutableCurrentlyPlaying = MutableStateFlow<PlaylistItem?>(null)
    override val currentlyPlaying: StateFlow<PlaylistItem?> = mutableCurrentlyPlaying.asStateFlow()

    private val mutableCurrentChapter = MutableStateFlow<List<Chapter>>(emptyList())
    override val currentChapters: StateFlow<List<Chapter>> = mutableCurrentChapter.asStateFlow()

    override var currentlyPlayingPlaylistItem: Int
        get() = internalPlayer.currentMediaItemIndex
        set(value) {
            assert(value in 0..<playlist.value.size) {
                throw NewPlayerException("Playlist item selection out of bound: selected item index: $value, available chapters: ${playlist.value.size}")
            }
            internalPlayer.seekTo(value, 0)
        }

    init {
        internalPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                launchJobAndCollectError {
                    val item = internalPlayer.currentMediaItem?.mediaId
                    val newUri = repository.tryAndRescueError(item, exception = error)
                    if (newUri != null) {
                        TODO("Implement handing new uri on fixed error")
                    } else {
                        mutableErrorFlow.emit(error)
                    }
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                launchJobAndCollectError {
                    mutableOnEvent.emit(Pair(player, events))
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                updatePlaylistItems()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                mediaItem?.let {
                    val playlistItem = getPlaylistItem(mediaItem.mediaId.toLong())
                    if (playlistItem != null) {
                        mutableCurrentlyPlaying.update {
                            playlistItem
                        }
                    } else {
                        launchJobAndCollectError {
                            val item = fetchPlaylistItem(
                                uniqueId = mediaItem.mediaId.toLong(),
                                mediaRepo = repository,
                                idLookupTable = uniqueIdToIdLookup
                            )
                            mutableCurrentlyPlaying.update { item }
                        }
                    }
                }
            }
        })

        playerScope.launch {
            currentlyPlaying.collect { playing ->
                playing?.let {
                    try {
                        val chapters = repository.getChapters(playing.id)
                        mutableCurrentChapter.update { chapters }
                    } catch (e: Exception) {
                        mutableErrorFlow.emit(e)
                    }
                }
            }
        }
    }

    private fun updatePlaylistItems() {
        playerScope.launch {
            val playlist =
                getPlaylistItemsFromExoplayer(internalPlayer, repository, uniqueIdToIdLookup)
            var playlistDuration = 0
            for (item in playlist) {
                playlistDuration += item.lengthInS
            }

            mutablePlaylist.update {
                playlist
            }
        }
    }

    private fun getPlaylistItem(uniqueId: Long): PlaylistItem? {
        for (item in playlist.value) {
            if (item.uniqueId == uniqueId) {
                return item
            }
        }
        return null
    }

    override fun prepare() {
        internalPlayer.prepare()
    }

    override fun play() {
        if (internalPlayer.currentMediaItem != null) {
            internalPlayer.play()
        } else {
            Log.i(TAG, "Tried to start playing but no media Item was cued")
        }
    }

    override fun pause() {
        internalPlayer.pause()
    }

    override fun addToPlaylist(item: String) {
        launchJobAndCollectError {
            val mediaItem = toMediaItem(item)
            internalPlayer.addMediaItem(mediaItem)
        }
    }

    override fun movePlaylistItem(fromIndex: Int, toIndex: Int) {
        internalPlayer.moveMediaItem(fromIndex, toIndex)
    }

    override fun removePlaylistItem(index: Int) {
        internalPlayer.removeMediaItem(index)
    }

    override fun playStream(item: String, playMode: PlayMode) {
        launchJobAndCollectError {
            val mediaItem = toMediaItem(item)
            internalPlayStream(mediaItem, playMode)
        }
    }

    override fun playStream(item: String, streamVariant: String, playMode: PlayMode) {
        launchJobAndCollectError {
            val stream = toMediaItem(item, streamVariant)
            internalPlayStream(stream, playMode)
        }
    }

    override fun selectChapter(index: Int) {
        val chapters = currentChapters.value
        assert(index in 0..<chapters.size) {
            throw NewPlayerException("Chapter selection out of bound: selected chapter index: $index, available chapters: ${chapters.size}")
        }
        val chapter = chapters[index]
        currentPosition = chapter.chapterStartInMs
    }

    private fun internalPlayStream(mediaItem: MediaItem, playMode: PlayMode) {
        if (internalPlayer.playbackState == Player.STATE_IDLE) {
            internalPlayer.prepare()
        }
        this.playBackMode.update { playMode }
        this.internalPlayer.setMediaItem(mediaItem)
        this.internalPlayer.play()
    }

    private suspend fun toMediaItem(item: String, streamVariant: String): MediaItem {
        val dataStream = repository.getStream(item, streamVariant)
        val uniqueId = Random.nextLong()
        uniqueIdToIdLookup[uniqueId] = item
        val mediaItem = MediaItem.Builder().setMediaId(uniqueId.toString()).setUri(dataStream)
        return mediaItem.build()
    }

    private suspend fun toMediaItem(item: String): MediaItem {

        val availableStream = repository.getAvailableStreamVariants(item)
        var selectedStream = availableStream[availableStream.size / 2]
        for (preferredStream in preferredStreamVariants) {
            if (preferredStream in availableStream) {
                selectedStream = preferredStream
                break;
            }
        }

        return toMediaItem(item, selectedStream)
    }

    private fun launchJobAndCollectError(task: suspend () -> Unit) =
        playerScope.launch {
            try {
                task()
            } catch (e: Exception) {
                mutableErrorFlow.emit(e)
            }
        }

}