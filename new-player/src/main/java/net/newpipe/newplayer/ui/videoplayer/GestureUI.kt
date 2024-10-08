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

package net.newpipe.newplayer.ui.videoplayer

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.EmbeddedGestureUI
import net.newpipe.newplayer.ui.videoplayer.gesture_ui.FullscreenGestureUI

private const val TAG = "TouchUi"

val INDICATOR_BACKGROUND_COLOR = Color.Black.copy(alpha = 0.3f)

@OptIn(UnstableApi::class)
@Composable
internal fun GestureUI(
    modifier: Modifier,
    viewModel: NewPlayerViewModel,
    uiState: NewPlayerUIState,
    onVolumeIndicatorVisibilityChanged: (Boolean) -> Unit
) {
    if (uiState.uiMode.fullscreen) {
        FullscreenGestureUI(
            modifier = modifier,
            viewModel = viewModel,
            uiState = uiState,
            onVolumeIndicatorVisibilityChanged = onVolumeIndicatorVisibilityChanged
        )
    } else {
        EmbeddedGestureUI(
            modifier = modifier, viewModel = viewModel, uiState = uiState
        )
    }
}