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

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.newpipe.newplayer.ui.ContentScale

@Parcelize
data class VideoPlayerUIState(
    val playing: Boolean,
    var fullscreen: Boolean,
    val uiVissible: Boolean,
    var uiVisible: Boolean,
    val contentRatio: Float,
    val embeddedUiRatio: Float,
    val contentFitMode: ContentScale,
    val seekerPosition: Float,
    val bufferedPercentage: Float,
    val isLoading: Boolean,
    val durationInMs: Long,
    val playbackPositionInMs: Long,
    val fastSeekSeconds: Int,
    val soundVolume: Float,

    // when null use system value
    val brightness: Float?
) : Parcelable {
    companion object {
        val DEFAULT = VideoPlayerUIState(
            playing = false,
            fullscreen = false,
            uiVissible = false,
            uiVisible = false,
            contentRatio = 16 / 9f,
            embeddedUiRatio = 16f / 9f,
            contentFitMode = ContentScale.FIT_INSIDE,
            seekerPosition = 0f,
            bufferedPercentage = 0f,
            isLoading = true,
            durationInMs = 0,
            playbackPositionInMs = 0,
            fastSeekSeconds = 0,
            soundVolume = 0f,
            brightness = null
        )
    }
}