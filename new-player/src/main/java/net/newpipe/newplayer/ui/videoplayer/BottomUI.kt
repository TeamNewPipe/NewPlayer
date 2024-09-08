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

import android.app.Activity
import android.app.LocaleConfig
import android.icu.text.DecimalFormat
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.viewModelScope
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.seeker.ChapterSegment
import net.newpipe.newplayer.ui.seeker.DefaultSeekerColor
import net.newpipe.newplayer.ui.seeker.Seeker
import net.newpipe.newplayer.ui.seeker.SeekerColors
import net.newpipe.newplayer.ui.seeker.Segment
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.getEmbeddedUiConfig
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs


private const val TAG = "BottomUI"

@Composable
fun BottomUI(
    modifier: Modifier, viewModel: VideoPlayerViewModel, uiState: VideoPlayerUIState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        val locale = getLocale()!!
        Text(getTimeStringFromMs(uiState.playbackPositionInMs, getLocale() ?: locale))
        Seeker(
            Modifier.weight(1F),
            value = uiState.seekerPosition,
            onValueChange = viewModel::seekPositionChanged,
            onValueChangeFinished = viewModel::seekingFinished,
            readAheadValue = uiState.bufferedPercentage,
            colors = customizedSeekerColors(),
            chapterSegments = getSeekerSegmentsFromChapters(uiState.chapters, uiState.durationInMs)
        )

        Text(getTimeStringFromMs(uiState.durationInMs, getLocale() ?: locale))

        val embeddedUiConfig = when (LocalContext.current) {
            is Activity -> getEmbeddedUiConfig(LocalContext.current as Activity)
            else -> EmbeddedUiConfig.DUMMY
        }

        IconButton(
            onClick = if (uiState.uiMode.fullscreen) viewModel::switchToEmbeddedView
            else {
                { // <- head of lambda ... yea kotlin is weird
                    viewModel.switchToFullscreen(embeddedUiConfig)
                }
            }
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

private fun getSeekerSegmentsFromChapters(chapters: List<Chapter>, duration: Long) =
    chapters
        .filter { chapter ->
            if (chapter.chapterStartInMs in 1..<duration) {
                true
            } else {
                Log.e(
                    TAG,
                    "Chapter mark outside of stream duration range: chapter: ${chapter.chapterTitle}, mark in ms: ${chapter.chapterStartInMs}, video duration in ms: ${duration}"
                )
                false
            }
        }
        .map { chapter ->
            val markPosition = chapter.chapterStartInMs.toFloat() / duration.toFloat()
            ChapterSegment(name = chapter.chapterTitle ?: "", start = markPosition)
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
                uiState = VideoPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI,
                    seekerPosition = 0.2f,
                    playbackPositionInMs = 3 * 60 * 1000,
                    bufferedPercentage = 0.4f
                ),
            )
        }
    }
}