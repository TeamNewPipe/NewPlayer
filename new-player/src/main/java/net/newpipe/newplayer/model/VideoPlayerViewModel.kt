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

    fun addCallbackListener(listener: Listener)
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
    fun embeddedDraggedDown(offset: Float)
    fun fastSeek(count: Int)
    fun fastSeekFinished()

    interface Listener {
        fun onFullscreenToggle(isFullscreen: Boolean) {}

        fun embeddedPlayerDraggedDown(offset: Float) {}
    }
}