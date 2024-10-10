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
import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
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
import net.newpipe.newplayer.utils.ActionResponse
import net.newpipe.newplayer.utils.MediaSourceBuilder
import net.newpipe.newplayer.utils.NewPlayerException
import net.newpipe.newplayer.utils.NewPlayerLoadErrorHandlingPolicy
import net.newpipe.newplayer.utils.NoResponse
import net.newpipe.newplayer.utils.SingleSelection
import net.newpipe.newplayer.utils.StreamExceptionResponse
import net.newpipe.newplayer.utils.StreamSelection
import net.newpipe.newplayer.utils.StreamSelectionResponse
import net.newpipe.newplayer.utils.StreamSelector

private const val TAG = "NewPlayerImpl"

class NewPlayerImpl(
    val app: Application,
    override val playerActivityClass: Class<Activity>,
    override val repository: MediaRepository,
    override val preferredVideoVariants: List<String> = emptyList(),
    override val preferredStreamLanguage: List<String> = emptyList(),
    override val preferredAudioVariants: List<String> = emptyList(),
    override val notificationIcon: IconCompat = IconCompat.createWithResource(
        app,
        R.drawable.new_player_tiny_icon
    ),
    val rescueStreamFault: suspend (
        item: String?,
        mediaItem: MediaItem?,
        exception: Exception,
        repository: MediaRepository
    ) -> StreamExceptionResponse
    = { _, _, _, _ -> NoResponse() }
) : NewPlayer {
    private val mutableExoPlayer = MutableStateFlow<ExoPlayer?>(null)
    override val exoPlayer = mutableExoPlayer.asStateFlow()

    private var playerScope = CoroutineScope(Dispatchers.Main + Job())

    private var uniqueIdToIdLookup = HashMap<Long, String>()
    private var uniqueIdToStreamVariantSelection = HashMap<Long, StreamSelection>()

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
            Player.REPEAT_MODE_OFF -> RepeatMode.DO_NOT_REPEAT
            Player.REPEAT_MODE_ALL -> RepeatMode.REPEAT_ALL
            Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_ONE
            else -> throw NewPlayerException("Unknown Repeatmode option returned by ExoPlayer: ${exoPlayer.value?.repeatMode}")
        }
        set(value) {
            when (value) {
                RepeatMode.DO_NOT_REPEAT -> exoPlayer.value?.repeatMode = Player.REPEAT_MODE_OFF
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

    private val mutableCurrentlySelectedLanguage = MutableStateFlow<String?>(null)
    override val currentlySelectedLanguage = mutableCurrentlySelectedLanguage.asStateFlow()

    private val mutableCurrentlySelectedStreamVariant = MutableStateFlow<String?>(null)
    override val currentlySelectedStreamVariant =
        mutableCurrentlySelectedStreamVariant.asStateFlow()


    override var currentlyPlayingPlaylistItem: Int
        get() = exoPlayer.value?.currentMediaItemIndex ?: -1
        set(value) {
            assert(value in 0..<playlist.value.size) {
                throw NewPlayerException("Playlist item selection out of bound: selected item index: $value, available chapters: ${playlist.value.size}")
            }
            exoPlayer.value?.seekTo(value, 0)
        }

    private var mutableAvailableStreamVariants = MutableStateFlow<StreamVariants?>(null)
    override val availableStreamVariants = mutableAvailableStreamVariants.asStateFlow()

    private fun setupNewExoplayer() {
        val newExoPlayer = ExoPlayer.Builder(app)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(if (repository.getRepoInfo().pullsDataFromNetwrok) C.WAKE_MODE_NETWORK else C.WAKE_MODE_LOCAL)
            .build()

        newExoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                onPlayBackError(error)
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
                if (mediaItem != null) {
                    val item = uniqueIdToIdLookup[mediaItem.mediaId.toLong()]!!
                    updateStreamVariants(item)
                } else {
                    mutableAvailableStreamVariants.update { null }
                }
            }

            @OptIn(UnstableApi::class)
            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                updateStreamSelection()
            }
        })
        mutableExoPlayer.update {
            newExoPlayer
        }
    }

    fun onPlayBackError(exception: Exception) {
        exoPlayer.value?.pause()

        launchJobAndCollectError {
            val item = exoPlayer.value?.currentMediaItem?.mediaId?.let {
                uniqueIdToIdLookup[it.toLong()]
            }
            val response = rescueStreamFault(
                item,
                exoPlayer.value?.currentMediaItem!!,
                exception,
                repository
            )
            when (response) {
                is ActionResponse -> {
                    response.action()
                }

                is StreamSelectionResponse -> {
                    replaceCurrentStream(response.streamSelection)
                }

                is NoResponse -> {
                    try {
                        throw NewPlayerException(
                            "Playback Exception happened, but no response was send by rescueStreamFault(). You may consider to implement this function.",
                            exception
                        )
                    } catch (e: Exception) {
                        mutableErrorFlow.emit(e)
                    }
                }

                else -> {
                    throw NewPlayerException("Unknwon exception response ${response.javaClass}")
                }
            }
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
            val mediaSource = toMediaSource(item)
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

    @OptIn(UnstableApi::class)
    override fun playStream(item: String, playMode: PlayMode) {
        launchJobAndCollectError {
            val mediaSource = toMediaSource(item)
            internalPlayStream(mediaSource, playMode)
        }
        updateStreamVariants(item)
    }


    private fun updateStreamVariants(item: String) {
        launchJobAndCollectError {
            val streams = repository.getStreams(item)
            mutableAvailableStreamVariants.update {
                StreamVariants(
                    identifiers = streams.map { it.identifier },
                    languages = streams.mapNotNull { it.language }
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun updateStreamSelection() {
        // TODO: Track and Stream selection is not working right now.
        // TIP: If you work on this try to find a way to only do track selection.
        // Stream selection is horrible code and must be done manually, while track selection
        // can be offloaded to exoplayer.
        // Now Track selection is the only way to go for DASH/HLS and Progressive Streams,
        // but if you have a MergingMediaSource it is kind of tricky. The problem here:
        // If you have multiple audio streams in your MergingMediaSource,
        // and the streams are all marked to be "English" (The same language) you can only select
        // each individual stream as stream, but when represented as track within a
        // MergingMediaSource, you have no chance to select each individual track by language.
        // Maybe this will change later on when ExoPlayer/Media3 is getting developed further on,
        // or you will find another way to map each individual track to the stream it originates from.
        currentlyPlaying.value?.let { mediaItem ->
            when (val selection = uniqueIdToStreamVariantSelection[mediaItem.mediaId.toLong()]) {
                is SingleSelection -> {
                    if (selection.stream.streamType != StreamType.DYNAMIC) {
                        mutableCurrentlySelectedLanguage.update { selection.stream.language }
                        mutableCurrentlySelectedStreamVariant.update { selection.stream.identifier }
                        return
                    }
                }
            }

            exoPlayer.value?.currentTracks?.let { tracks ->
                println("gurken trackgroups : ${tracks.groups.size}")
                var group_num = 0
                for (group in tracks.groups) {
                    println("   gurken group: ${++group_num}")
                    for (i in 0 until group.length) {
                        val format = group.getTrackFormat(i)
                        format.metadata
                        println("   gurken track $i: language: ${format.language}, labels: ${format.labels.size}, width: ${format.width} height: ${format.height}")
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun selectChapter(index: Int) {
        val chapters = currentChapters.value
        assert(index in chapters.indices) {
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
        mutableAvailableStreamVariants.update { null }
        uniqueIdToIdLookup = HashMap()
    }

    override fun getItemLinkOfMediaItem(mediaItem: MediaItem) =
        uniqueIdToIdLookup[mediaItem.mediaId.toLong()]
            ?: throw NewPlayerException("Could not find Media item with mediaId: ${mediaItem.mediaId}")

    override fun getItemFromMediaItem(mediaItem: MediaItem) =
        uniqueIdToIdLookup[mediaItem.mediaId.toLong()]
            ?: throw NewPlayerException("Could not find an item corresponding to a media item with uniqueid: ${mediaItem.mediaId}")


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
    fun toMediaSource(item: String): MediaSource {
        val streamSelector = StreamSelector(
            preferredVideoIdentifier = preferredVideoVariants,
            preferredAudioIdentifier = preferredAudioVariants,
            preferredLanguage = preferredStreamLanguage
        )

        val selection = streamSelector.selectStream(
            item,
            availableStreams = repository.getStreams(item),
            demuxedStreamBundeling = StreamSelector.DemuxedStreamBundeling.BUNDLE_STREAMS_WITH_SAME_ID
        )
        return toMediaSource(selection, item)
    }

    @OptIn(UnstableApi::class)
    private suspend fun toMediaSource(streamSelection: StreamSelection, item: String): MediaSource {
        val builder = MediaSourceBuilder(
            repository = repository,
            uniqueIdToIdLookup = uniqueIdToIdLookup,
            mutableErrorFlow = mutableErrorFlow,
            httpDataSourceFactory = repository.getHttpDataSourceFactory(item),
            loadErrorHandlingPolicy = NewPlayerLoadErrorHandlingPolicy(this::onPlayBackError)
        )
        val mediaSource = builder.buildMediaSource(streamSelection)
        uniqueIdToStreamVariantSelection[mediaSource.mediaItem.mediaId.toLong()] = streamSelection
        return mediaSource
    }

    /**
     * Replaces the current stream and continues playing at the position the previous stream stopped.
     * This can be used to replace a faulty stream or change to a stream with a different language/quality.
     */
    @OptIn(UnstableApi::class)
    private suspend fun replaceCurrentStream(stream: StreamSelection) {
        val currentPosition = this.currentPosition
        val currentlyPlayingPlaylistItem = this.currentlyPlayingPlaylistItem
        val item = uniqueIdToIdLookup[this.currentlyPlaying.value?.mediaId?.toLong()]!!

        val mediaSource = toMediaSource(stream, item)
        this.exoPlayer.value?.removeMediaItem(currentlyPlayingPlaylistItem)
        this.exoPlayer.value?.addMediaSource(currentlyPlayingPlaylistItem, mediaSource)
        if (this.exoPlayer.value?.playbackState == Player.STATE_IDLE) {
            prepare()
        }
        this.currentlyPlayingPlaylistItem = currentlyPlayingPlaylistItem
        this.exoPlayer.value?.seekTo(currentPosition)
        this.exoPlayer.value?.play()
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