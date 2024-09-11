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
import android.content.ComponentName
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.newpipe.newplayer.service.NewPlayerService
import kotlin.random.Random

private const val TAG = "NewPlayerImpl"

class NewPlayerImpl(
    val app: Application,
    private val repository: MediaRepository,
    override val preferredStreamVariants: List<String> = emptyList(),
    override val preferredStreamLanguage: List<String> = emptyList()
) : NewPlayer {

    private val mutableExoPlayer = MutableStateFlow<ExoPlayer?>(null)
    override val exoPlayer = mutableExoPlayer.asStateFlow()

    private var playerScope = CoroutineScope(Dispatchers.Main + Job())

    private var uniqueIdToIdLookup = HashMap<Long, String>()

    // this is used to take care of the NewPlayerService
    private var mediaController: MediaController? = null

    var mutableErrorFlow = MutableSharedFlow<Exception>()
    override val errorFlow = mutableErrorFlow.asSharedFlow()

    override val bufferedPercentage: Int
        get() = exoPlayer.value?.bufferedPercentage ?: 0

    override var currentPosition: Long
        get() = exoPlayer.value?.currentPosition ?: 0
        set(value) {
            exoPlayer.value?.seekTo(value)
        }

    override var fastSeekAmountSec: Int = 10

    override var playBackMode = MutableStateFlow(PlayMode.IDLE)

    override var shuffle: Boolean
        get() = exoPlayer.value?.shuffleModeEnabled ?: false
        set(value) {
            exoPlayer.value?.shuffleModeEnabled = value
        }

    override var repeatMode: RepeatMode
        get() = when (exoPlayer.value?.repeatMode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.DONT_REPEAT
            Player.REPEAT_MODE_ALL -> RepeatMode.REPEAT_ALL
            Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_ONE
            else -> throw NewPlayerException("Unknown Repeatmode option returned by ExoPlayer: ${exoPlayer.value?.repeatMode}")
        }
        set(value) {
            when (value) {
                RepeatMode.DONT_REPEAT -> exoPlayer.value?.repeatMode = Player.REPEAT_MODE_OFF
                RepeatMode.REPEAT_ALL -> exoPlayer.value?.repeatMode = Player.REPEAT_MODE_ALL
                RepeatMode.REPEAT_ONE -> exoPlayer.value?.repeatMode = Player.REPEAT_MODE_ONE
            }
        }

    private var mutableOnEvent = MutableSharedFlow<Pair<Player, Player.Events>>()
    override val onExoPlayerEvent: SharedFlow<Pair<Player, Player.Events>> =
        mutableOnEvent.asSharedFlow()

    override var playWhenReady: Boolean
        set(value) {
            exoPlayer.value?.playWhenReady = value
        }
        get() = exoPlayer.value?.playWhenReady ?: false


    override val duration: Long
        get() = exoPlayer.value?.duration ?: 0

    private val mutablePlaylist = MutableStateFlow<List<MediaItem>>(emptyList())
    override val playlist: StateFlow<List<MediaItem>> =
        mutablePlaylist.asStateFlow()

    private val mutableCurrentlyPlaying = MutableStateFlow<MediaItem?>(null)
    override val currentlyPlaying: StateFlow<MediaItem?> = mutableCurrentlyPlaying.asStateFlow()

    private val mutableCurrentChapter = MutableStateFlow<List<Chapter>>(emptyList())
    override val currentChapters: StateFlow<List<Chapter>> = mutableCurrentChapter.asStateFlow()

    override var currentlyPlayingPlaylistItem: Int
        get() = exoPlayer.value?.currentMediaItemIndex ?: -1
        set(value) {
            assert(value in 0..<playlist.value.size) {
                throw NewPlayerException("Playlist item selection out of bound: selected item index: $value, available chapters: ${playlist.value.size}")
            }
            exoPlayer.value?.seekTo(value, 0)
        }

    private fun setupNewExoplayer() {
        val newExoPlayer = ExoPlayer.Builder(app)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(if (repository.getRepoInfo().pullsDataFromNetwrok) C.WAKE_MODE_NETWORK else C.WAKE_MODE_LOCAL)
            .build()
        newExoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                launchJobAndCollectError {
                    val item = newExoPlayer.currentMediaItem?.mediaId
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
                mutablePlaylist.update {
                    (0..<newExoPlayer.mediaItemCount).map {
                        newExoPlayer.getMediaItemAt(it)
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                mutableCurrentlyPlaying.update { mediaItem }
            }
        })
        mutableExoPlayer.update {
            newExoPlayer
        }
    }

    init {
        playerScope.launch {
            currentlyPlaying.collect { playing ->
                playing?.let {
                    try {
                        val chapters =
                            repository.getChapters(uniqueIdToIdLookup[playing.mediaId.toLong()]!!)
                        mutableCurrentChapter.update { chapters }
                    } catch (e: Exception) {
                        mutableErrorFlow.emit(e)
                    }
                }
            }
        }
    }

    override fun prepare() {
        if (exoPlayer.value == null) {
            setupNewExoplayer()
        }
        exoPlayer.value?.prepare()
        if (mediaController == null) {
            val sessionToken =
                SessionToken(app, ComponentName(app, NewPlayerService::class.java))
            val mediaControllerFuture = MediaController.Builder(app, sessionToken).buildAsync()
            mediaControllerFuture.addListener({
                mediaController = mediaControllerFuture.get()
            }, MoreExecutors.directExecutor())
        }
    }


    override fun play() {
        exoPlayer.value?.let {
            if (exoPlayer.value?.currentMediaItem != null) {
                exoPlayer.value?.play()
            } else {
                Log.i(TAG, "Tried to start playing but no media Item was cued")
            }
        }
    }

    override fun pause() {
        exoPlayer.value?.pause()
    }

    @OptIn(UnstableApi::class)
    override fun addToPlaylist(item: String) {
        if (exoPlayer.value == null) {
            prepare()
        }
        launchJobAndCollectError {
            val mediaSource = toMediaSource(item, playBackMode.value)
            exoPlayer.value?.addMediaSource(mediaSource)
        }
    }

    override fun movePlaylistItem(fromIndex: Int, toIndex: Int) {
        exoPlayer.value?.moveMediaItem(fromIndex, toIndex)
    }

    override fun removePlaylistItem(uniqueId: Long) {
        for (i in 0..<(exoPlayer.value?.mediaItemCount ?: 0)) {
            val id = exoPlayer.value?.getMediaItemAt(i)?.mediaId?.toLong() ?: 0
            if (id == uniqueId) {
                exoPlayer.value?.removeMediaItem(i)
                break
            }
        }
    }

    override fun playStream(item: String, playMode: PlayMode) {
        launchJobAndCollectError {
            val mediaItem = toMediaSource(item, playMode)
            internalPlayStream(mediaItem, playMode)
        }
    }

    override fun playStream(
        item: String,
        streamVariant: StreamVariant,
        playMode: PlayMode
    ) {
        launchJobAndCollectError {
            val stream = toMediaSource(item, streamVariant)
            internalPlayStream(stream, playMode)
        }
    }

    @OptIn(UnstableApi::class)
    override fun selectChapter(index: Int) {
        val chapters = currentChapters.value
        assert(index in 0..<chapters.size) {
            throw NewPlayerException("Chapter selection out of bound: selected chapter index: $index, available chapters: ${chapters.size}")
        }
        val chapter = chapters[index]
        currentPosition = chapter.chapterStartInMs
    }

    override fun release() {
        mediaController?.release()
        exoPlayer.value?.release()
        playBackMode.update {
            PlayMode.IDLE
        }
        mutableExoPlayer.update {
            null
        }
        mediaController = null
        uniqueIdToIdLookup = HashMap()
    }

    override fun getItemLinkOfMediaItem(mediaItem: MediaItem) =
        uniqueIdToIdLookup[mediaItem.mediaId.toLong()]
            ?: throw NewPlayerException("Could not find Media item with mediaId: ${mediaItem.mediaId}")

    @OptIn(UnstableApi::class)
    private fun internalPlayStream(mediaSource: MediaSource, playMode: PlayMode) {
        if (exoPlayer.value?.playbackState == Player.STATE_IDLE || exoPlayer.value == null) {
            prepare()
        }
        this.playBackMode.update { playMode }

        this.exoPlayer.value?.setMediaSource(mediaSource)
        this.exoPlayer.value?.play()
    }

    @OptIn(UnstableApi::class)
    private suspend
    fun toMediaSource(item: String, streamVariant: StreamVariant): MediaSource {
        val dataStream = repository.getStream(item, streamVariant)

        val uniqueId = Random.nextLong()
        uniqueIdToIdLookup[uniqueId] = item
        val mediaItemBuilder = MediaItem.Builder()
            .setMediaId(uniqueId.toString())
            .setUri(dataStream)

        try {
            val metadata = repository.getMetaInfo(item)
            mediaItemBuilder.setMediaMetadata(metadata)
        } catch (e: Exception) {
            mutableErrorFlow.emit(e)
        }

        val mediaItem = mediaItemBuilder.build()

        return ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
            .createMediaSource(mediaItem)
    }

    private suspend
    fun toMediaSource(item: String, playMode: PlayMode): MediaSource {
        val availableStreams = repository.getAvailableStreamVariants(item)
        var selectedStream = availableStreams[availableStreams.size / 2]
        for (preferredStream in preferredStreamVariants) {
            for (availableStream in availableStreams) {
                if (preferredStream == availableStream.streamVariantIdentifier) {
                    selectedStream = availableStream
                }
            }
        }

        return toMediaSource(item, selectedStream)
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