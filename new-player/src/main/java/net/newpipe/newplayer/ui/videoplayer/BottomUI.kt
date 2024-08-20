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

import android.app.LocaleConfig
import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.ConfigurationCompat
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.seeker.DefaultSeekerColor
import net.newpipe.newplayer.ui.seeker.Seeker
import net.newpipe.newplayer.ui.seeker.SeekerColors
import net.newpipe.newplayer.ui.seeker.SeekerDefaults
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs
import java.util.Locale
import kotlin.math.min

@Composable
fun BottomUI(
    modifier: Modifier, viewModel: VideoPlayerViewModel, uiState: VideoPlayerUIState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(getTimeStringFromMs(uiState.playbackPositionInMs, getLocale() ?: Locale.US))
        Seeker(
            Modifier.weight(1F),
            value = uiState.seekerPosition,
            onValueChange = viewModel::seekPositionChanged,
            onValueChangeFinished = viewModel::seekingFinished,
            readAheadValue = uiState.bufferedPercentage,
            colors = customizedSeekerColors()
        )

        //Slider(value = 0.4F, onValueChange = {}, modifier = Modifier.weight(1F))

        Text(getTimeStringFromMs(uiState.durationInMs, getLocale() ?: Locale.US))

        IconButton(
            onClick = if (uiState.uiMode.fullscreen) viewModel::switchToEmbeddedView
            else viewModel::switchToFullscreen
        ) {
            Icon(
                imageVector = if (uiState.uiMode.fullscreen) Icons.Filled.FullscreenExit
                else Icons.Filled.Fullscreen,
                contentDescription = stringResource(R.string.widget_description_toggle_fullscreen)
            )
        }
    }
}

@Composable
private fun customizedSeekerColors(): SeekerColors {
    val colors = DefaultSeekerColor(
        progressColor = MaterialTheme.colorScheme.primary,
        thumbColor = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        readAheadColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledProgressColor = MaterialTheme.colorScheme.primary,
        disabledThumbColor = MaterialTheme.colorScheme.primary,
        disabledTrackColor = MaterialTheme.colorScheme.primary
    )
    return colors
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerBottomUIPreview() {
    VideoPlayerTheme {
        Surface(color = Color.Black) {
            BottomUI(
                modifier = Modifier,
                viewModel = VideoPlayerViewModelDummy(),
                uiState = VideoPlayerUIState.DEFAULT.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI,
                    seekerPosition = 0.4f,
                    durationInMs = 90 * 60 * 1000,
                    playbackPositionInMs = 3 * 60 * 1000,
                    bufferedPercentage = 0.4f
                ),
            )
        }
    }
}