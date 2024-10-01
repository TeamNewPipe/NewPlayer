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

import android.app.PictureInPictureUiState
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.ui.ContentScale

@OptIn(UnstableApi::class)
interface NewPlayerViewModel {
    var newPlayer: NewPlayer?

    val uiState: StateFlow<NewPlayerUIState>
    var minContentRatio: Float
    var maxContentRatio: Float
    var contentFitMode: ContentScale
    val embeddedPlayerDraggedDownBy: SharedFlow<Float>
    val onBackPressed: SharedFlow<Unit>
    var deviceInPowerSaveMode: Boolean

    fun initUIState(instanceState: Bundle)
    fun play()
    fun pause()
    fun prevStream()
    fun nextStream()
    fun changeUiMode(newUiModeState: UIModeState, embeddedUiConfig: EmbeddedUiConfig?)
    fun onBackPressed()
    fun seekPositionChanged(newValue: Float)
    fun seekingFinished()
    fun embeddedDraggedDown(offset: Float)
    fun fastSeek(count: Int)
    fun finishFastSeek()
    fun brightnessChange(changeRate: Float, systemBrightness: Float)
    fun volumeChange(changeRate: Float)
    fun chapterSelected(chapterId: Int)
    fun streamSelected(streamId: Int)
    fun cycleRepeatMode()
    fun toggleShuffle()
    fun onStorePlaylist()
    fun movePlaylistItem(from: Int, to: Int)
    fun removePlaylistItem(uniqueId: Long)
    fun onStreamItemDragFinished()
    fun dialogVisible(visible: Boolean)
    fun doneEnteringPip()
    fun onPictureInPictureModeChanged(isPictureInPictureMode: Boolean)
}