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

data class VideoPlayerUIState(
    val playing: Boolean,
    var fullscreen: Boolean,
    var uiVissible: Boolean
){
    companion object {
        val DEFAULT = VideoPlayerUIState(
            playing = true,
            fullscreen = false,
            uiVissible = false
        )
    }
}

interface VideoPlayerViewModel {
    val player: Player?
    val uiState: StateFlow<VideoPlayerUIState>
    fun play()
    fun pause()
    fun uiResume()
    fun prevStream()
    fun nextStream()
    fun switchToFullscreen()
    fun switchToEmbeddedView()
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

    init {
        player.prepare()
        player.setMediaItem(MediaItem.fromUri(app.getString(R.string.ccc_6502_video)))
    }

    override fun play() {
        player.play()
        mutableUiState.update {
            it.copy(playing = player.isPlaying)
        }
    }

    override fun pause() {
        player.pause()

        mutableUiState.update {
            it.copy(playing = player.isPlaying)
        }
    }

    override fun uiResume() {
        play()
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

    override fun onCleared() {
        player.release()
    }

    companion object {
        val dummy = object : VideoPlayerViewModel {
            override val player = null
            override val uiState = MutableStateFlow(VideoPlayerUIState.DEFAULT)
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

            override fun uiResume() {
                println("dummy ui resume")
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