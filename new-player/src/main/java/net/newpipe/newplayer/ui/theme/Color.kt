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

package net.newpipe.newplayer.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.ui.PreviewBackgroundSurface
import net.newpipe.newplayer.ui.VideoPlayerControllerUI

val video_player_primary = Color(0xFFE53935)

// The color of buttons, and the LoadingCircle
val video_player_onPrimary = Color(0xFFF8F8F8)

val video_player_primaryContainer = Color(0xFF633F00)
val video_player_onPrimaryContainer = Color(0xFFFFDDB3)
val video_player_secondary = Color(0xFFDDC2A1)
val video_player_onSecondary = Color(0xFF3E2D16)
val video_player_secondaryContainer = Color(0xFF56442A)
val video_player_onSecondaryContainer = Color(0xFFFBDEBC)
val video_player_tertiary = Color(0xFFB8CEA1)
val video_player_onTertiary = Color(0xFF243515)
val video_player_tertiaryContainer = Color(0xFF3A4C2A)
val video_player_onTertiaryContainer = Color(0xFFD4EABB)
val video_player_error = Color(0xFFFFB4AB)
val video_player_errorContainer = Color(0xFF93000A)
val video_player_onError = Color(0xFF690005)
val video_player_onErrorContainer = Color(0xFFFFDAD6)
val video_player_background = Color(0xFF1F1B16)
val video_player_onBackground = Color(0xFFEAE1D9)
val video_player_surface = Color(0xFF000000)

// The color of the Text and icons
val video_player_onSurface = Color(0xFFF8F8F8)

// The color of the menu Icons
val video_player_onSurfaceVariant = Color(0xFFF8F8F8)

// The background color of the seekbar
val video_player_surfaceVariant = Color(0xFF4F4539)

val video_player_outline = Color(0xFF9C8F80)
val video_player_inverseOnSurface = Color(0xFF1F1B16)
val video_player_inverseSurface = Color(0xFFEAE1D9)
val video_player_inversePrimary = Color(0xFF825500)
val video_player_shadow = Color(0xFF000000)
val video_player_surfaceTint = Color(0xFFFFB951)
val video_player_outlineVariant = Color(0xFF4F4539)
val video_player_scrim = Color(0xFF000000)

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerUIPreviewEmbeddedColorPreview() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(isPlaying = false,
                fullscreen = false,
                uiVissible = true,
                seekPosition = 0.3F,
                isLoading = false,
                durationInMs = 9*60*1000,
                playbackPositionInMs = 6*60*1000,
                bufferedPercentage = 0.4f,
                fastSeekSeconds = 10,
                play = {},
                pause = {},
                prevStream = {},
                nextStream = {},
                switchToFullscreen = {},
                switchToEmbeddedView = {},
                showUi = {},
                hideUi = {},
                seekPositionChanged = {},
                seekingFinished = {},
                embeddedDraggedDownBy = {},
                fastSeekForward = {},
                fastSeekBackward = {})
        }
    }
}

