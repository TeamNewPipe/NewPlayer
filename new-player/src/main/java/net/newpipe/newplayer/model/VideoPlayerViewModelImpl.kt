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
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.newpipe.newplayer.utils.VideoSize
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.ui.ContentScale

val VIDEOPLAYER_UI_STATE = "video_player_ui_state"

private const val TAG = "VideoPlayerViewModel"

@HiltViewModel
class VideoPlayerViewModelImpl @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    application: Application,
) : AndroidViewModel(application), VideoPlayerViewModel {

    // private
    private val mutableUiState = MutableStateFlow(VideoPlayerUIState.DEFAULT)
    private var currentContentRatio = 1F

    private var uiVisibilityJob: Job? = null
    private var progressUpdaterJob: Job? = null

    var callbackListeners: MutableList<VideoPlayerViewModel.Listener?> = ArrayList()

    //interface
    override var newPlayer: NewPlayer? = null
        set(value) {
            field = value
            installExoPlayer()
        }

    override val uiState = mutableUiState.asStateFlow()

    override val internalPlayer: Player?
        get() = newPlayer?.internal_player

    override var minContentRatio: Float = 4F / 3F
        set(value) {
            if (value <= 0 || maxContentRatio < value)
                Log.e(
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
            if (value <= 0 || value < minContentRatio)
                Log.e(
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

    private fun installExoPlayer() {
        internalPlayer?.let { player ->
            Log.d(TAG, "Install player: ${player.videoSize.width}")

            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Log.d(TAG, "Playing state changed. Is Playing: $isPlaying")
                    mutableUiState.update {
                        it.copy(playing = isPlaying)
                    }
                }

                override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                    super.onVideoSizeChanged(videoSize)
                    updateContentRatio(VideoSize.fromMedia3VideoSize(videoSize))
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    mutableUiState.update {
                        it.copy(isLoading = isLoading)
                    }
                    Log.i(
                        TAG,
                        if (isLoading) "Player started loading" else "Player finished loading"
                    )
                }
            })
        }
    }

    fun updateContentRatio(videoSize: VideoSize) {
        val newRatio = videoSize.getRatio()
        val ratio = if (newRatio.isNaN()) currentContentRatio else newRatio
        currentContentRatio = ratio
        Log.d(TAG, "Update Content ratio: $ratio")
        mutableUiState.update {
            it.copy(
                contentRatio = currentContentRatio,
                embeddedUiRatio = getEmbeddedUiRatio()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        Log.d(TAG, "viewmodel cleared")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initUIState(instanceState: Bundle) {

        val uiState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) instanceState.getParcelable(
                VIDEOPLAYER_UI_STATE, VideoPlayerUIState::class.java
            )
            else instanceState.getParcelable(VIDEOPLAYER_UI_STATE)

        uiState?.let { uiState ->
            mutableUiState.update {
                uiState
            }
        }
    }

    override fun addCallbackListener(listener: VideoPlayerViewModel.Listener) {
        callbackListeners.add(listener)
    }

    override fun play() {
        hideUi()
        newPlayer?.play()
    }

    override fun pause() {
        uiVisibilityJob?.cancel()
        newPlayer?.pause()

    }

    override fun prevStream() {
        resetHideUiDelayedJob()
        Log.e(TAG, "imeplement prev stream")
    }

    override fun nextStream() {
        resetHideUiDelayedJob()
        Log.e(TAG, "implement next stream")
    }

    override fun showUi() {
        mutableUiState.update {
            it.copy(uiVissible = true)
        }
        resetHideUiDelayedJob()
        resetProgressUpdatePeriodicallyJob()
    }

    private fun resetHideUiDelayedJob() {
        uiVisibilityJob?.cancel()
        uiVisibilityJob = viewModelScope.launch {
            delay(4000)
            hideUi()
        }
    }

    private fun resetProgressUpdatePeriodicallyJob() {
        progressUpdaterJob?.cancel()
        progressUpdaterJob = viewModelScope.launch {
            while (true) {
                updateProgressOnce()
                delay(1000)
            }
        }

    }

    private fun updateProgressOnce() {
        val progress = internalPlayer?.currentPosition ?: 0
        val duration = internalPlayer?.duration ?: 1
        val bufferedPercentage = (internalPlayer?.bufferedPercentage?.toFloat() ?: 0f) / 100f
        val progressPercentage = progress.toFloat() / duration.toFloat()

        mutableUiState.update {
            it.copy(
                seekerPosition = progressPercentage,
                durationInMs = duration,
                playbackPositionInMs = progress,
                bufferedPercentage = bufferedPercentage
            )
        }
    }

    override fun hideUi() {
        progressUpdaterJob?.cancel()
        uiVisibilityJob?.cancel()
        mutableUiState.update {
            it.copy(uiVissible = false)
        }
    }

    override fun seekPositionChanged(newValue: Float) {
        uiVisibilityJob?.cancel()
        mutableUiState.update { it.copy(seekerPosition = newValue) }
    }

    override fun seekingFinished() {
        resetHideUiDelayedJob()
        val seekerPosition = mutableUiState.value.seekerPosition
        val seekPositionInMs = (internalPlayer?.duration?.toFloat() ?: 0F) * seekerPosition
        newPlayer?.currentPosition = seekPositionInMs.toLong()
        Log.i(TAG, "Seek to Ms: $seekPositionInMs")
    }

    override fun embeddedDraggedDown(offset: Float) {
        callbackListeners.forEach { it?.embeddedPlayerDraggedDown(offset) }
    }

    override fun fastSeek(count: Int) {
        mutableUiState.update {
            it.copy(
                fastseekSeconds = count * (newPlayer?.fastSeekAmountSec ?: 10)
            )
        }

        if (mutableUiState.value.uiVisible) {
            resetHideUiDelayedJob()
        }
    }

    override fun finishFastSeek() {
        val fastSeekAmount = mutableUiState.value.fastseekSeconds
        if (fastSeekAmount != 0) {
            Log.d(TAG, "$fastSeekAmount")

            newPlayer?.currentPosition = (newPlayer?.currentPosition ?: 0) + (fastSeekAmount * 1000)
            mutableUiState.update {
                it.copy(fastseekSeconds = 0)
            }
        }
    }

    override fun brightnesChange(changeRate: Float) {
        TODO("Not yet implemented")
    }

    override fun volumeChange(changeRate: Float) {
        TODO("Not yet implemented")
    }

    override fun switchToEmbeddedView() {
        callbackListeners.forEach { it?.onFullscreenToggle(false) }
        uiVisibilityJob?.cancel()
        finishFastSeek()
        mutableUiState.update {
            it.copy(fullscreen = false, uiVissible = false)
        }
    }

    override fun switchToFullscreen() {
        callbackListeners.forEach { it?.onFullscreenToggle(true) }
        uiVisibilityJob?.cancel()
        finishFastSeek()
        mutableUiState.update {
            it.copy(fullscreen = true, uiVissible = false)
        }
    }

    private fun getEmbeddedUiRatio() =
        internalPlayer?.let { player ->
            val videoRatio = VideoSize.fromMedia3VideoSize(player.videoSize).getRatio()
            return (if (videoRatio.isNaN())
                currentContentRatio
            else
                videoRatio).coerceIn(minContentRatio, maxContentRatio)


        } ?: minContentRatio


    companion object {
        val dummy = object : VideoPlayerViewModel {
            override var newPlayer: NewPlayer? = null
            override val internalPlayer: Player? = null
            override val uiState = MutableStateFlow(VideoPlayerUIState.DEFAULT)
            override var minContentRatio = 4F / 3F
            override var maxContentRatio = 16F / 9F
            override var contentFitMode = ContentScale.FIT_INSIDE

            override fun initUIState(instanceState: Bundle) {
                println("dummy impl")
            }

            override fun addCallbackListener(listener: VideoPlayerViewModel.Listener) {
                println("dummy impl")
            }

            override fun play() {
                println("dummy impl")
            }

            override fun switchToEmbeddedView() {
                println("dummy impl")
            }

            override fun switchToFullscreen() {
                println("dummy impl")
            }

            override fun showUi() {
                println("dummy impl")
            }

            override fun hideUi() {
                println("dummy impl")
            }

            override fun seekPositionChanged(newValue: Float) {
                println("dymmy seekPositionChanged: newValue: ${newValue}")
            }

            override fun seekingFinished() {
                println("dummy impl")
            }

            override fun embeddedDraggedDown(offset: Float) {
                println("dymmy embeddedDraggedDown: offset: ${offset}")
            }

            override fun fastSeek(steps: Int) {
                println("dummy impl")
            }

            override fun finishFastSeek() {
                println("dummy impl")
            }

            override fun brightnesChange(changeRate: Float) {
                println("dummy impl")
            }

            override fun volumeChange(changeRate: Float) {
                println("dummy impl")
            }

            override fun pause() {
                println("dummy pause")
            }

            override fun prevStream() {
                println("dummy impl")
            }

            override fun nextStream() {
                println("dummy impl")
            }
        }
    }
}