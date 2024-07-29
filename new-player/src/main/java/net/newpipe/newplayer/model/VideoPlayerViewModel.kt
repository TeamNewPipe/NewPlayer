package net.newpipe.newplayer.model

import android.os.Bundle
import androidx.media3.common.Player
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.ui.ContentScale


interface VideoPlayerViewModel {
    var newPlayer: NewPlayer?
    val player: Player?
    val uiState: StateFlow<VideoPlayerUIState>
    var minContentRatio: Float
    var maxContentRatio: Float
    var contentFitMode: ContentScale
    var callbackListener: Listener?

    fun initUIState(instanceState: Bundle)
    fun play()
    fun pause()
    fun prevStream()
    fun nextStream()
    fun switchToFullscreen()
    fun switchToEmbeddedView()
    fun showUi()
    fun hideUi()
    fun seekPositionChanged(newValue: Float)
    fun seekingFinished()

    interface Listener {
        fun onFullscreenToggle(isFullscreen: Boolean)
        fun onUiVissibleToggle(isVissible: Boolean)
    }
}