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

import android.content.pm.ActivityInfo
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.newpipe.newplayer.ui.NewPlayerUI

/**
 * This helps to restore properties of the UI/SystemUI when returning from fullscreen mode.
 * [NewPlayerViewModelImpl] uses this to store the properties when switching to a fullscreen mode.
 * When returning from a fullscreen mode [NewPlayerViewModelImpl] will store the current
 * EmbeddedUiConfig in the [NewPlayerUIState]. When returning from fullscreen mode
 * [NewPlayerUI] will then restore that configuration.
 *
 * TODO: At least in theory. Brightnes and the systembar theme don't work (correctly) right now.
 *
 */
@Parcelize
data class EmbeddedUiConfig(
    val systemBarInLightMode: Boolean,
    val brightness: Float,
    val screenOrientation: Int
) : Parcelable {
    companion object {
        val DUMMY = EmbeddedUiConfig(
            systemBarInLightMode = true,
            brightness = -1f,
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        )
    }
}