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
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.newpipe.newplayer.utils.VideoSize
import kotlinx.parcelize.Parcelize
import net.newpipe.newplayer.NewPlayer

val VIDEOPLAYER_UI_STATE = "video_player_ui_state"

@Parcelize
data class VideoPlayerUIState(
    val playing: Boolean,
    var fullscreen: Boolean,
    var uiVissible: Boolean,
    var contentRatio: Float
) : Parcelable {
    companion object {
        val DEFAULT = VideoPlayerUIState(
            playing = false, fullscreen = false, uiVissible = false, 0F
        )
    }
}

interface VideoPlayerViewModel {
    var newPlayer: NewPlayer?
    val player: Player?
    val uiState: StateFlow<VideoPlayerUIState>
    var listener: Listener?

    fun initUIState(instanceState: Bundle)
    fun play()
    fun pause()
    fun prevStream()
    fun nextStream()
    fun switchToFullscreen()
    fun switchToEmbeddedView()

    interface Listener {
        fun requestUpdateLayoutRatio(ratio: Float)
    }

    sealed class Events {
        object SwitchToFullscreen : Events()
        object SwitchToEmbeddedView : Events()
    }
}


@HiltViewModel
class VideoPlayerViewModelImpl @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    application: Application,
) : AndroidViewModel(application), VideoPlayerViewModel {

    // private
    private val mutableUiState = MutableStateFlow(VideoPlayerUIState.DEFAULT)
    private var current_video_size = VideoSize.DEFAULT

    //interface
    override var newPlayer: NewPlayer? = null
        set(value) {
            field = value
            installExoPlayer()
        }

    override val uiState = mutableUiState.asStateFlow()
    override var listener: VideoPlayerViewModel.Listener? = null

    override val player:Player?
        get() = newPlayer?.player

    private fun installExoPlayer() {
        player?.let { player ->
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    mutableUiState.update {
                        it.copy(playing = isPlaying)
                    }
                }

                // We need to updated the layout of our player view if the video ratio changes
                // However, this should be done differently depending on weather we are in
                // embedded or fullscreen view.
                // If we are in embedded view, we tell the mother layout (only ConstraintLayout supported!)
                // to change the ratio of the whole player view.
                // If we are in fullscreen we only want to change the ratio of the SurfaceView
                override fun onVideoSizeChanged(media3VideoSize: androidx.media3.common.VideoSize) {
                    super.onVideoSizeChanged(media3VideoSize)

                    val videoSize = VideoSize.fromMedia3VideoSize(media3VideoSize)

                    if (current_video_size != videoSize) {
                        val newRatio = videoSize.getRatio()
                        if (current_video_size.getRatio() != newRatio) {
                            mutableUiState.update {
                                it.copy(contentRatio = newRatio)
                            }
                            if (!mutableUiState.value.fullscreen) {
                                listener?.requestUpdateLayoutRatio(newRatio)
                            }
                        }
                        current_video_size = videoSize
                    }
                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("gurken viewmodel cleared")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initUIState(instanceState: Bundle) {

        val uiState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                instanceState.getParcelable(VIDEOPLAYER_UI_STATE, VideoPlayerUIState::class.java)
            else
                instanceState.getParcelable(VIDEOPLAYER_UI_STATE)

        uiState?.let { uiState ->
            mutableUiState.update {
                uiState
            }
        }
    }

    override fun play() {
        newPlayer?.play()
    }

    override fun pause() {
        newPlayer?.pause()
    }

    override fun prevStream() {
        println("imeplement prev stream")
    }

    override fun nextStream() {
        println("implement next stream")
    }

    override fun switchToEmbeddedView() {
        mutableUiState.update {
            it.copy(fullscreen = false)
        }
    }

    override fun switchToFullscreen() {
        mutableUiState.update {
            it.copy(fullscreen = true)
        }
    }

    companion object {
        val dummy = object : VideoPlayerViewModel {
            override var newPlayer: NewPlayer? = null
            override val player: Player? = null
            override val uiState = MutableStateFlow(VideoPlayerUIState.DEFAULT)
            override var listener: VideoPlayerViewModel.Listener? = null


            override fun initUIState(instanceState: Bundle) {
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