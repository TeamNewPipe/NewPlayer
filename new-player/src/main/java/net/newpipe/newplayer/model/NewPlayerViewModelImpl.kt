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

package net.newpipe.newplayer.model

import android.app.Application
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat.getSystemService
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.newpipe.newplayer.utils.VideoSize
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.PlayMode
import net.newpipe.newplayer.RepeatMode
import net.newpipe.newplayer.ui.ContentScale
import java.util.LinkedList
import kotlin.math.abs

val VIDEOPLAYER_UI_STATE = "video_player_ui_state"

private const val TAG = "VideoPlayerViewModel"


@UnstableApi
@HiltViewModel
class NewPlayerViewModelImpl @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    application: Application,
) : AndroidViewModel(application), NewPlayerViewModel {

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
        val soundVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            .toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableUiState.update {
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
                newPlayer.availableStreamVariants.collect { availableVariants ->
                    if (availableVariants != null) {
                        mutableUiState.update {
                            it.copy(
                                availableStreamVariants = availableVariants.identifiers,
                                availableLanguages = availableVariants.languages
                            )
                        }
                    } else {
                        mutableUiState.update {
                            it.copy(
                                availableLanguages = emptyList(),
                                availableStreamVariants = emptyList()
                            )
                        }
                    }
                }
            }

            mutableUiState.update {
                it.copy(
                    playing = newPlayer.exoPlayer.value?.isPlaying ?: false,
                    isLoading = !(newPlayer.exoPlayer.value?.isPlaying
                        ?: false) && newPlayer.exoPlayer.value?.isLoading ?: false
                )
            }
        }
    }

    fun updateContentRatio(videoSize: VideoSize) {
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

        val recoveredUiState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) instanceState.getParcelable(
                VIDEOPLAYER_UI_STATE, NewPlayerUIState::class.java
            )
            else instanceState.getParcelable(VIDEOPLAYER_UI_STATE)

        if (recoveredUiState != null) {
            mutableUiState.update {
                recoveredUiState
            }
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

    override fun changeUiMode(newUiModeState: UIModeState, embeddedUiConfig: EmbeddedUiConfig?) {
        if (newUiModeState == uiState.value.uiMode) {
            return;
        }

        if (!uiState.value.uiMode.fullscreen && newUiModeState.fullscreen && embeddedUiConfig != null) {
            this.embeddedUiConfig = embeddedUiConfig
        }

        if (!(newUiModeState == UIModeState.EMBEDDED_VIDEO_CONTROLLER_UI ||
                    newUiModeState == UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI)
        ) {
            hideUiDelayedJob?.cancel()
        } else {
            startHideUiDelayedJob()
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
        if (newUiModeState == UIModeState.PIP) {
            if (uiState.value.uiMode.inAudioMode) {
                mutableUiState.update {
                    it.copy(uiMode = uiState.value.uiMode.getVideoEquivalent())
                }
            } else if (uiState.value.uiMode.videoControllerUiVisible) {
                mutableUiState.update {
                    it.copy(uiMode = uiState.value.uiMode.getUiHiddenState())
                }
            }
            mutableUiState.update {
                it.copy(enteringPip = true)
            }

        } else {
            if (uiState.value.uiMode.fullscreen && !newUiModeState.fullscreen) {
                mutableUiState.update {
                    it.copy(uiMode = newUiModeState, embeddedUiConfig = this.embeddedUiConfig)
                }
            } else {
                mutableUiState.update {
                    it.copy(uiMode = newUiModeState)
                }
            }
        }

        // update play mode in NewPlayer if that value was not updated through the newPlayer object
        val newPlayMode = newUiModeState.toPlayMode()
        // take the next value from the player because changeUiMode is called when the playBackMode
        // of the player changes. If this value was taken from the viewModel instead
        // this would lead to an endless loop. of changeMode state calling it self over and over again
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
        progressUpdaterJob?.cancel()
        progressUpdaterJob = viewModelScope.launch {
            while (true) {
                updateProgressOnce()
                delay(if (deviceInPowerSaveMode) 1000 else 1000 / 30/*fps*/)
            }
        }
    }

    private fun updateProgressOnce() {
        val progress = newPlayer?.currentPosition ?: 0
        val duration = newPlayer?.duration ?: 1
        val bufferedPercentage = (newPlayer?.bufferedPercentage?.toFloat() ?: 0f) / 100f
        val progressPercentage = progress.toFloat() / duration.toFloat()

        mutableUiState.update {
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
        var progress = 0L
        val currentlyPlaying = uiState.value.currentlyPlaying?.mediaId?.toLong() ?: 0L
        for (item in uiState.value.playList) {
            if (item.mediaId.toLong() == currentlyPlaying) break;
            progress += item.mediaMetadata.durationMs
                ?: throw NewPlayerException("Media Item not containing duration. Media Item in question: ${item.mediaMetadata.title}")
        }
        progress += (newPlayer?.currentPosition ?: 0)
        mutableUiState.update {
            it.copy(
                playbackPositionInPlaylistMs = progress
            )
        }
    }

    override fun seekPositionChanged(newValue: Float) {
        if (uiState.value.uiMode.videoControllerUiVisible) {
            changeUiMode(uiState.value.uiMode.getControllerUiVisibleState(), null)
        }
        hideUiDelayedJob?.cancel()
        progressUpdaterJob?.cancel()
        val seekerPosition = mutableUiState.value.seekerPosition
        val seekPositionInMs = (newPlayer?.duration?.toFloat() ?: 0F) * seekerPosition
        newPlayer?.currentPosition = seekPositionInMs.toLong()
        Log.i(TAG, "Seek to Ms: $seekPositionInMs")

        mutableUiState.update {
            it.copy(
                seekerPosition = newValue,
                playbackPositionInMs = seekPositionInMs.toLong()
            )
        }
    }

    private fun updateSeekPreviewThumbnail(seekPositionInMs: Long) {
        updatePreviewThumbnailJob?.cancel()
        mutableUiState.update {
            it.copy(currentSeekPreviewThumbnail = null)
        }
        updatePreviewThumbnailJob = viewModelScope.launch {
            val item = newPlayer?.currentlyPlaying?.value?.let {
                newPlayer?.getItemFromMediaItem(it)
            }
            item?.let {
                val bitmap = newPlayer?.repository?.getPreviewThumbnail(item, seekPositionInMs)
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
        if (mutableUiState.value.uiMode.fullscreen) {
            val currentBrightness = mutableUiState.value.brightness
                ?: if (systemBrightness < 0f) 0.5f else systemBrightness
            Log.d(
                TAG,
                "currentBrightnes: $currentBrightness, sytemBrightness: $systemBrightness, changeRate: $changeRate"
            )

            val newBrightness = (currentBrightness + changeRate * 1.3f).coerceIn(0f, 1f)
            mutableUiState.update {
                it.copy(brightness = newBrightness)
            }
        }
    }

    override fun volumeChange(changeRate: Float) {
        val currentVolume = mutableUiState.value.soundVolume
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
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

    override fun onStorePlaylist() {
        TODO("Not yet implemented")
    }

    override fun movePlaylistItem(from: Int, to: Int) {
        if (playlistItemToBeMoved == null) {
            playlistItemToBeMoved = from
        }
        playlistItemNewPosition = to
        val tempList = LinkedList(uiState.value.playList)
        val item = uiState.value.playList[from]
        tempList.removeAt(from)
        tempList.add(to, item)
        mutableUiState.update {
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