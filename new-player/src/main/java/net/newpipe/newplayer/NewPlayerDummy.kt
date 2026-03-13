package net.newpipe.newplayer

import android.app.Activity
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.data.RepeatMode
import net.newpipe.newplayer.data.StreamTrack
import net.newpipe.newplayer.logic.NoResponse
import net.newpipe.newplayer.logic.StreamExceptionResponse
import net.newpipe.newplayer.repository.MediaRepository
import net.newpipe.newplayer.repository.PlaceHolderRepository

open class NewPlayerDummy : NewPlayer {
    override var preferredStreamLanguages: List<String> = emptyList()

    override val notificationIcon: IconCompat
        get() = throw UnsupportedOperationException("DummyNewPlayer")

    override val playerActivityClass: Class<out Activity>
        get() = throw UnsupportedOperationException("DummyNewPlayer")

    override val exoPlayer: StateFlow<Player?> = MutableStateFlow(null)

    override var playWhenReady: Boolean = false

    override val duration: Long = 0L

    override val bufferedPercentage: Int = 0

    override var currentPosition: Long = 0L

    override var fastSeekAmountSec: Int = 10

    override val playBackMode: MutableStateFlow<PlayMode> = MutableStateFlow(PlayMode.IDLE)

    override var shuffle: Boolean = false

    override var repeatMode: RepeatMode = RepeatMode.DO_NOT_REPEAT

    override val repository: MediaRepository = PlaceHolderRepository()

    override val playlist: StateFlow<List<MediaItem>> = MutableStateFlow(emptyList())

    override val currentlyPlaying: StateFlow<MediaItem?> = MutableStateFlow(null)

    override var currentlyPlayingPlaylistItem: Int = 0

    override val currentChapters: StateFlow<List<Chapter>> = MutableStateFlow(emptyList())

    override val currentlyPlayingTracks: StateFlow<List<StreamTrack>> =
        MutableStateFlow(emptyList())

    override val currentlyAvailableTracks: StateFlow<List<StreamTrack>> =
        MutableStateFlow(emptyList())

    override var currentStreamLanguageConstraint: String? = null

    override val errorFlow: SharedFlow<Exception> = MutableSharedFlow()

    override val rescueStreamFault: suspend (
        item: String?,
        mediaItem: MediaItem?,
        exception: Exception,
        repository: MediaRepository,
    ) -> StreamExceptionResponse = { _, _, _, _ -> NoResponse() }

    override val onExoPlayerEvent: SharedFlow<Pair<Player, Player.Events>> = MutableSharedFlow()

    override fun prepare() {}

    override fun play() {}

    override fun pause() {}

    override fun addToPlaylist(item: String) {}

    override fun movePlaylistItem(fromIndex: Int, toIndex: Int) {}

    override fun removePlaylistItem(uniqueId: Long) {}

    override fun playStream(item: String, playMode: PlayMode) {}

    override fun selectChapter(index: Int) {}

    override fun release() {}

    override fun getItemFromMediaItem(mediaItem: MediaItem): String = ""
}