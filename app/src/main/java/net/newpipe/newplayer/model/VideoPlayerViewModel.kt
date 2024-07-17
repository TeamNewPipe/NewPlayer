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
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.newpipe.newplayer.R
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import net.newpipe.newplayer.VideoPlayerActivity
import net.newpipe.newplayer.utils.VideoSize

data class VideoPlayerUIState(
    val playing: Boolean, var fullscreen: Boolean, var uiVissible: Boolean, var contentRatio: Float
) {
    companion object {
        val DEFAULT = VideoPlayerUIState(
            playing = false, fullscreen = false, uiVissible = false, 0F
        )
    }
}

interface VideoPlayerViewModel {
    val player: Player?
    val uiState: StateFlow<VideoPlayerUIState>
    var listener: Listener?
    fun play()
    fun pause()
    fun prevStream()
    fun nextStream()
    fun switchToFullscreen()
    fun switchToEmbeddedView()

    interface Listener {
        fun requestUpdateLayoutRatio(ratio: Float)
        fun switchToFullscreen()
    }
}


@HiltViewModel
class VideoPlayerViewModelImpl @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    override val player: Player,
    application: Application
) : AndroidViewModel(application), VideoPlayerViewModel {

    val app = getApplication<Application>()

    private val mutableUiState = MutableStateFlow(
        VideoPlayerUIState.DEFAULT
    )

    override val uiState = mutableUiState.asStateFlow()

    override var listener: VideoPlayerViewModel.Listener? = null

    var current_video_size = VideoSize.DEFAULT

    init {

        println("gurken $this")

        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                println("gurken playerstate: $isPlaying")
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

        player.setMediaItem(MediaItem.fromUri(app.getString(R.string.ccc_6502_video)))
        //player.playWhenReady = true
    }

    override fun play() {
        //player.play()
    }

    override fun pause() {
        //player.pause()
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
        //listener?.switchToFullscreen()
    }

    override fun onCleared() {
        player.release()
    }

    companion object {
        val dummy = object : VideoPlayerViewModel {
            override val player = null
            override val uiState = MutableStateFlow(VideoPlayerUIState.DEFAULT)
            override var listener: VideoPlayerViewModel.Listener? = null
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