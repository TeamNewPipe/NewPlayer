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

package net.newpipe.newplayer.uiModel

import android.app.Application
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.BundleCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.newpipe.newplayer.data.VideoSize
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.data.NewPlayerException
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.data.RepeatMode
import net.newpipe.newplayer.ui.ContentScale
import java.util.LinkedList
import kotlin.math.abs

val VIDEOPLAYER_UI_STATE = "video_player_ui_state"

private const val TAG = "VideoPlayerViewModel"

private const val GESTURE_SCROLL_RATE_MULTIPLIER = 1.3f

@UnstableApi
@HiltViewModel
class NewPlayerViewModelImpl @Inject constructor(
    application: Application,
) : AndroidViewModel(application), InternalNewPlayerViewModel {

    // private
    private val mutableUiState = MutableStateFlow(NewPlayerUIState.DEFAULT)
    private var currentContentRatio = 1F

    private var playlistItemToBeMoved: Int? = null
    private var playlistItemNewPosition: Int = 0

    private var hideUiDelayedJob: Job? = null
    private var progressUpdaterJob: Job? = null
    private var playlistProgressUpdaterJob: Job? = null
    private var updatePreviewThumbnailJob: Job? = null

    // this is necesary to restore the embedded view UI configuration when returning from fullscreen
    private var embeddedUiConfig: EmbeddedUiConfig? = null

    private var playbackPositionWhenFastSeekStarted = 0L

    private val audioManager =
        getSystemService(application.applicationContext, AudioManager::class.java)!!

    init {
        mutableUiState.update {
            val soundVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                .toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

            it.copy(soundVolume = soundVolume)
        }
    }

    //interface
    override var newPlayer: NewPlayer? = null
        set(value) {
            field = value
            installNewPlayer()
        }

    override val uiState = mutableUiState.asStateFlow()

    override var minContentRatio: Float = 4F / 3F
        set(value) {
            if (value <= 0 || maxContentRatio < value) Log.e(
                TAG,
                "Ignoring maxContentRatio: It must not be 0 or less and it may not be bigger then mmaxContentRatio. It was Set to: $value"
            )
            else {
                field = value
                mutableUiState.update { it.copy(embeddedUiRatio = getEmbeddedUiRatio()) }
            }
        }


    override var maxContentRatio: Float = 16F / 9F
        set(value) {
            if (value <= 0 || value < minContentRatio) Log.e(
                TAG,
                "Ignoring maxContentRatio: It must not be 0 or less and it may not be smaller then minContentRatio. It was Set to: $value"
            )
            else {
                field = value
                mutableUiState.update { it.copy(embeddedUiRatio = getEmbeddedUiRatio()) }
            }
        }

    override var contentFitMode: ContentScale
        get() = mutableUiState.value.contentFitMode
        set(value) {
            mutableUiState.update {
                it.copy(contentFitMode = value)
            }
        }

    private var mutableEmbeddedPlayerDraggedDownBy = MutableSharedFlow<Float>()
    override val embeddedPlayerDraggedDownBy = mutableEmbeddedPlayerDraggedDownBy.asSharedFlow()

    private var mutableOnBackPressed = MutableSharedFlow<Unit>()
    override val onBackPressed: SharedFlow<Unit> = mutableOnBackPressed.asSharedFlow()

    override var deviceInPowerSaveMode: Boolean = false
        get() = field
        set(value) {
            field = value
            if (progressUpdaterJob?.isActive == true) {
                startProgressUpdatePeriodicallyJob()
            }
        }

    private fun installNewPlayer() {
        newPlayer?.let { newPlayer ->
            viewModelScope.launch {
                newPlayer.exoPlayer.collect { player ->

                    Log.d(TAG, "Install player: ${player?.videoSize?.width}")

                    player?.addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            Log.d(TAG, "Playing state changed. Is Playing: $isPlaying")
                            mutableUiState.update {
                                it.copy(playing = isPlaying, isLoading = false)
                            }
                            if (isPlaying && uiState.value.uiMode.videoControllerUiVisible) {
                                startHideUiDelayedJob()
                            } else {
                                // TODO why cancel the job, if the ui is already not visible?
                                hideUiDelayedJob?.cancel()
                            }
                        }

                        override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                            super.onVideoSizeChanged(videoSize)
                            updateContentRatio(VideoSize.fromMedia3VideoSize(videoSize))
                        }


                        override fun onIsLoadingChanged(isLoading: Boolean) {
                            super.onIsLoadingChanged(isLoading)
                            if (!player.isPlaying) {
                                mutableUiState.update {
                                    it.copy(isLoading = isLoading)
                                }
                            }
                        }

                        override fun onRepeatModeChanged(repeatMode: Int) {
                            super.onRepeatModeChanged(repeatMode)
                            mutableUiState.update {
                                // TODO why are you using newPlayer.repeatMode instead of just
                                //  repeatMode? Also below
                                it.copy(repeatMode = newPlayer.repeatMode)
                            }
                        }

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                            mutableUiState.update {
                                it.copy(shuffleEnabled = newPlayer.shuffle)
                            }
                        }
                    })

                }
            }

            viewModelScope.launch {
                newPlayer.playBackMode.collect { newMode ->
                    val currentMode = mutableUiState.value.uiMode.toPlayMode()

                    if (currentMode != newMode) {
                        changeUiMode(UIModeState.fromPlayMode(newMode), embeddedUiConfig)
                    }
                }
            }

            viewModelScope.launch {
                newPlayer.playlist.collect { playlist ->
                    mutableUiState.update {
                        it.copy(
                            playList = playlist,
                        )
                    }
                }
            }
            viewModelScope.launch {
                newPlayer.currentlyPlaying.collect { playlistItem ->
                    mutableUiState.update {
                        it.copy(
                            currentlyPlaying = playlistItem,
                            currentPlaylistItemIndex = newPlayer.currentlyPlayingPlaylistItem
                        )
                    }
                }
            }

            viewModelScope.launch {
                newPlayer.currentChapters.collect { chapters ->
                    mutableUiState.update {
                        it.copy(chapters = chapters)
                    }
                }
            }

            viewModelScope.launch {
                newPlayer.currentlyAvailableTracks.collect { availableTracks ->
                    mutableUiState.update {
                        it.copy(
                            currentlyAvailableTracks = availableTracks
                        )
                    }
                }
            }

            viewModelScope.launch {
                newPlayer.currentlyPlayingTracks.collect { playingTracks ->
                    mutableUiState.update {
                        it.copy(
                            currentlyPlayingTracks = playingTracks
                        )
                    }
                }
            }

            mutableUiState.update {
                // TODO shouldn't this update rather happen in `newPlayer.exoPlayer.collect`?
                it.copy(
                    playing = newPlayer.exoPlayer.value?.isPlaying ?: false,
                    isLoading = !(newPlayer.exoPlayer.value?.isPlaying
                        ?: false) && newPlayer.exoPlayer.value?.isLoading ?: false
                )
            }
        }
    }

    private fun updateContentRatio(videoSize: VideoSize) {
        val newRatio = videoSize.getRatio()
        val ratio = if (newRatio.isNaN()) currentContentRatio else newRatio
        currentContentRatio = ratio
        Log.d(TAG, "Update Content ratio: $ratio")
        mutableUiState.update {
            it.copy(
                contentRatio = currentContentRatio, embeddedUiRatio = getEmbeddedUiRatio()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        Log.d(TAG, "viewmodel cleared")
    }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initUIState(instanceState: Bundle) {
        BundleCompat.getParcelable(
            instanceState, VIDEOPLAYER_UI_STATE, NewPlayerUIState::class.java,
        )?.let { recoveredUiState ->
            mutableUiState.value = recoveredUiState
        }
    }

    override fun play() {
        changeUiMode(uiState.value.uiMode.getUiHiddenState(), null)
        newPlayer?.play()
    }

    override fun pause() {
        hideUiDelayedJob?.cancel()
        newPlayer?.pause()

    }

    override fun prevStream() {
        startHideUiDelayedJob()
        newPlayer?.let { newPlayer ->
            if (0 <= newPlayer.currentlyPlayingPlaylistItem - 1) {
                newPlayer.currentlyPlayingPlaylistItem -= 1
            }
        }
    }

    override fun nextStream() {
        startHideUiDelayedJob()
        newPlayer?.let { newPlayer ->
            if (newPlayer.currentlyPlayingPlaylistItem + 1 <
                (newPlayer.exoPlayer.value?.mediaItemCount ?: 0)
            ) {
                newPlayer.currentlyPlayingPlaylistItem += 1
            }
        }
    }

    // TODO this function is complicated, add more comments
    override fun changeUiMode(newUiModeState: UIModeState, embeddedUiConfig: EmbeddedUiConfig?) {
        if (newUiModeState == uiState.value.uiMode) {
            return;
        }

        if (!uiState.value.uiMode.fullscreen && newUiModeState.fullscreen && embeddedUiConfig != null) {
            this.embeddedUiConfig = embeddedUiConfig
        }

        if ((newUiModeState == UIModeState.EMBEDDED_VIDEO_CONTROLLER_UI ||
                    newUiModeState == UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI)
            && (newPlayer?.exoPlayer?.value?.isPlaying == true)
        ) {
            // TODO shouldn't it hide immediately (?)
            startHideUiDelayedJob()
        } else {
            hideUiDelayedJob?.cancel()
        }

        if (newUiModeState.isStreamSelect) {
            startPlaylistProgressUpdaterJob()
        } else {
            playlistProgressUpdaterJob?.cancel()
        }

        if (newUiModeState.requiresProgressUpdate) {
            startProgressUpdatePeriodicallyJob()
        } else {
            progressUpdaterJob?.cancel()
        }

        // notify the UI itself about the change of the UIMode
        mutableUiState.update {
            if (newUiModeState == UIModeState.PIP) {
                if (it.uiMode.inAudioMode) {
                    it.copy(uiMode = it.uiMode.getVideoEquivalent(), enteringPip = true)
                } else if (it.uiMode.videoControllerUiVisible) {
                    it.copy(uiMode = it.uiMode.getUiHiddenState(), enteringPip = true)
                } else {
                    it.copy(enteringPip = true)
                }
            } else {
                if (it.uiMode.fullscreen && !newUiModeState.fullscreen) {
                    it.copy(uiMode = newUiModeState, embeddedUiConfig = this.embeddedUiConfig)
                } else {
                    it.copy(uiMode = newUiModeState)
                }
            }
        }


        // update play mode in NewPlayer if that value was not updated through the newPlayer object
        val newPlayMode = newUiModeState.toPlayMode()
        // take the next value from the player because changeUiMode is called when the playBackMode
        // of the player changes. If this value was taken from the viewModel instead
        // this would lead to an endless loop. of changeMode state calling itself over and over again
        // through the callback of the newPlayer?.playBackMode change
        val currentPlayMode = newPlayer?.playBackMode?.value ?: PlayMode.IDLE
        if (newPlayMode != currentPlayMode) {
            newPlayer?.playBackMode?.update {
                newPlayMode
            }
        }
    }

    private fun startHideUiDelayedJob() {
        hideUiDelayedJob?.cancel()
        hideUiDelayedJob = viewModelScope.launch {
            delay(2000)
            changeUiMode(uiState.value.uiMode.getUiHiddenState(), null)
        }
    }

    private fun startProgressUpdatePeriodicallyJob() {
        // TODO this function should check whether the UI state every time deviceInPowerSaveMode or
        //  mutableUiState.value.uiMode.requiresProgressUpdate changes, and either start or stop the
        //  job. The condition checks should not be left to the callers.

        progressUpdaterJob?.cancel()
        progressUpdaterJob = viewModelScope.launch {
            while (true) {
                updateProgressOnce()
                delay(if (deviceInPowerSaveMode) 1000 else 1000 / 30/*fps*/)
            }
        }
    }

    private fun updateProgressOnce() {
        mutableUiState.update {
            val progress = newPlayer?.currentPosition ?: 0
            val duration = newPlayer?.duration ?: 1
            val bufferedPercentage = (newPlayer?.bufferedPercentage?.toFloat() ?: 0f) / 100f
            val progressPercentage = progress.toFloat() / duration.toFloat()

            it.copy(
                seekerPosition = progressPercentage,
                durationInMs = duration,
                playbackPositionInMs = progress,
                bufferedPercentage = bufferedPercentage,
            )
        }
    }

    private fun startPlaylistProgressUpdaterJob() {
        playlistProgressUpdaterJob?.cancel()
        playlistProgressUpdaterJob = viewModelScope.launch {
            while (true) {
                updateProgressInPlaylistOnce()
                delay(1000)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun updateProgressInPlaylistOnce() {
        mutableUiState.update {
            // TODO to save power, a cumulative sum of the durations can be computed only once for a
            //  specific playList instance, and then it can be queried in O(1) as many times as
            //  needed (though it isn't so important, since it's only relevant in stream selection
            //  UIs)

            // TODO the UI layer has access to playList, right? So why not perform the calculation
            //  there? It would avoid needing to start jobs, and it would be automatically be
            //  enabled/disabled if the UI is not shown for any reason (e.g. even if the phone is
            //  put in stand-by I think). I imagine something like:
            //  val progressInPreviousItems = remember(state.playList, state.currentPlaylistItemIndex) {
            //      42 // perform the calculation below
            //  }
            //  val progressInPlaylist = progressInPreviousItems + state.playbackPositionInMs

            var progress = 0L
            val currentlyPlaying = it.currentlyPlaying?.mediaId?.toLong() ?: 0L
            for (item in it.playList) {
                if (item.mediaId.toLong() == currentlyPlaying) break;
                progress += item.mediaMetadata.durationMs
                    // TODO it might be possible for an item to have an unknown duration until it is
                    //  played (e.g. shorts), in that case the player should count it as 0, and then
                    //  show a 2:35+ instead of 2:35 when showing the total duration
                    ?: throw NewPlayerException("Media Item not containing duration. Media Item in question: ${item.mediaMetadata.title}")
            }
            progress += (newPlayer?.currentPosition ?: 0)

            it.copy(
                playbackPositionInPlaylistMs = progress
            )
        }
    }

    private fun getSeekerPositionInMs(uiState: NewPlayerUIState) =
        ((newPlayer?.duration?.toFloat() ?: 0F) * uiState.seekerPosition).toLong()

    override fun seekPositionChanged(newValue: Float) {
        if (uiState.value.uiMode.videoControllerUiVisible) {
            changeUiMode(uiState.value.uiMode.getControllerUiVisibleState(), null)
        }
        hideUiDelayedJob?.cancel()
        progressUpdaterJob?.cancel()

        // TODO use it.value instead
        val seekPositionInMs = getSeekerPositionInMs(uiState.value)
        // TODO I would argue that the player position should be changed only when the user
        //  lifts the finger from the screen, to avoid buffering in useless places (?).
        //  So the newPlayer?.currentPosition assignment would only happen in seekingFinished()
        newPlayer?.currentPosition = seekPositionInMs
        Log.i(TAG, "Seek to Ms: $seekPositionInMs")

        updateSeekPreviewThumbnail(seekPositionInMs)
        mutableUiState.update {
            it.copy(
                seekerPosition = newValue,
                playbackPositionInMs = getSeekerPositionInMs(it),
                seekPreviewVisible = true
            )
        }
    }

    private fun updateSeekPreviewThumbnail(seekPositionInMs: Long) {
        updatePreviewThumbnailJob?.cancel()

        updatePreviewThumbnailJob = viewModelScope.launch {
            val item = newPlayer?.currentlyPlaying?.value?.let {
                newPlayer?.getItemFromMediaItem(it)
            }
            item?.let {
                val bitmap =
                    newPlayer?.repository?.getPreviewThumbnail(item, seekPositionInMs)

                mutableUiState.update {
                    it.copy(
                        currentSeekPreviewThumbnail = bitmap?.asImageBitmap(),
                        seekPreviewVisible = true
                    )
                }
            }
        }
    }

    override fun seekingFinished() {
        val seekerPosition = mutableUiState.value.seekerPosition
        val seekPositionInMs = (newPlayer?.duration?.toFloat() ?: 0F) * seekerPosition
        newPlayer?.currentPosition = seekPositionInMs.toLong()
        mutableUiState.update {
            it.copy(seekPreviewVisible = false)
        }

        startHideUiDelayedJob()
        startProgressUpdatePeriodicallyJob()
    }

    override fun embeddedDraggedDown(offset: Float) {
        safeTryEmit(mutableEmbeddedPlayerDraggedDownBy, offset)
    }

    override fun fastSeek(count: Int) {
        if (abs(count) == 1) {
            playbackPositionWhenFastSeekStarted = newPlayer?.currentPosition ?: 0
        }

        val fastSeekAmountInS = count * (newPlayer?.fastSeekAmountSec ?: 10)
        mutableUiState.update {
            it.copy(
                fastSeekSeconds = fastSeekAmountInS
            )
        }

        if (fastSeekAmountInS != 0) {
            Log.d(TAG, "fast seeking seeking by $fastSeekAmountInS seconds")

            newPlayer?.currentPosition =
                playbackPositionWhenFastSeekStarted + (fastSeekAmountInS * 1000)

        }

        if (mutableUiState.value.uiMode.videoControllerUiVisible) {
            startHideUiDelayedJob()
        }
    }

    override fun finishFastSeek() {
        if (mutableUiState.value.uiMode.videoControllerUiVisible) {
            startHideUiDelayedJob()
        }
        mutableUiState.update {
            it.copy(fastSeekSeconds = 0)
        }
    }

    override fun brightnessChange(changeRate: Float, systemBrightness: Float) {
        mutableUiState.update {
            if (it.uiMode.fullscreen) {
                val currentBrightness = it.brightness
                    ?: systemBrightness
                Log.d(
                    TAG,
                    "currentBrightnes: $currentBrightness, sytemBrightness: $systemBrightness, changeRate: $changeRate"
                )

                val newBrightness =
                    (currentBrightness + changeRate * GESTURE_SCROLL_RATE_MULTIPLIER).coerceIn(
                        0f,
                        1f
                    )
                it.copy(brightness = newBrightness)
            } else {
                it
            }
        }
    }

    override fun volumeChange(changeRate: Float) {
        val currentVolume = mutableUiState.value.soundVolume
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        // TODO extract these two 1.3f (this and the one for brightness) to a single constant
        //  value named something like GESTURE_SCROLL_RATE_MULTIPLIER, along with a
        //  javadoc containing the content in the comment below
        // we multiply changeRate by 1.5 so your finger only has to swipe a portion of the whole
        // screen in order to fully enable or disable the volume
        val newVolume = (currentVolume + changeRate * 1.3f).coerceIn(0f, 1f)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, (newVolume * maxVolume).toInt(), 0
        )
        println("Blub: currentVolume: $currentVolume, changeRate: $changeRate, maxVolume: $maxVolume, newvolume: $newVolume")
        mutableUiState.update {
            it.copy(soundVolume = newVolume)
        }
    }

    override fun onBackPressed() {
        val nextMode = uiState.value.uiMode.getNextModeWhenBackPressed()
        if (nextMode != null) {
            changeUiMode(nextMode, null)
        } else {
            safeTryEmit(mutableOnBackPressed, Unit)
        }
    }

    override fun chapterSelected(chapterId: Int) {
        newPlayer?.selectChapter(chapterId)
    }

    override fun streamSelected(streamId: Int) {
        newPlayer?.currentlyPlayingPlaylistItem = streamId
    }

    override fun cycleRepeatMode() {
        newPlayer?.let {
            it.repeatMode = when (it.repeatMode) {
                RepeatMode.DO_NOT_REPEAT -> RepeatMode.REPEAT_ALL
                RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
                RepeatMode.REPEAT_ONE -> RepeatMode.DO_NOT_REPEAT
            }
        }
    }

    override fun toggleShuffle() {
        newPlayer?.let {
            it.shuffle = !it.shuffle
        }
    }

    // TODO shouldn't this be provided by implementors (e.g. the NewPipe app)?
    override fun onStorePlaylist() {
        TODO("Not yet implemented")
    }

    override fun movePlaylistItem(from: Int, to: Int) {
        if (playlistItemToBeMoved == null) {
            playlistItemToBeMoved = from
        }
        playlistItemNewPosition = to

        // TODO: add a comment explaining that if there are performance problems, this can be
        //  reduced to O(|to-from|) using a mutable list.
        mutableUiState.update {
            val tempList = ArrayList(it.playList)
            val item = it.playList[from]
            tempList.removeAt(from)
            tempList.add(to, item)

            it.copy(
                playList = tempList
            )
        }
        startPlaylistProgressUpdaterJob()
    }

    override fun onStreamItemDragFinished() {
        playlistItemToBeMoved?.let {
            newPlayer?.movePlaylistItem(it, playlistItemNewPosition)
        }
        playlistItemToBeMoved = null
    }

    // TODO rename to something like showOrHideUi(visible: Boolean)
    override fun dialogVisible(visible: Boolean) {
        if (visible) {
            hideUiDelayedJob?.cancel()
            if (!uiState.value.uiMode.videoControllerUiVisible) {
                changeUiMode(uiState.value.uiMode.getControllerUiVisibleState(), null)
                hideUiDelayedJob?.cancel()
            }
        } else {
            startHideUiDelayedJob()
        }
    }

    override fun doneEnteringPip() {
        mutableUiState.update {
            it.copy(enteringPip = false)
        }
    }

    override fun onPictureInPictureModeChanged(isPictureInPictureMode: Boolean) {
        if (isPictureInPictureMode) {
            mutableUiState.update {
                it.copy(uiMode = UIModeState.PIP)
            }
        } else {
            changeUiMode(UIModeState.FULLSCREEN_VIDEO, null)
        }
    }

    override fun removePlaylistItem(uniqueId: Long) {
        newPlayer?.removePlaylistItem(uniqueId)
    }

    private fun getEmbeddedUiRatio() = newPlayer?.exoPlayer?.value?.let { player ->
        val videoRatio = VideoSize.fromMedia3VideoSize(player.videoSize).getRatio()
        return (if (videoRatio.isNaN()) currentContentRatio
        else videoRatio).coerceIn(minContentRatio, maxContentRatio)
    } ?: minContentRatio

    private fun <T> safeTryEmit(sharedFlow: MutableSharedFlow<T>, value: T) {
        if (!sharedFlow.tryEmit(value)) {
            viewModelScope.launch {
                sharedFlow.emit(value)
            }
        }
    }
}
