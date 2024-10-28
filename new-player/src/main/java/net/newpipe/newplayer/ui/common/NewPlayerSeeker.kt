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



package net.newpipe.newplayer.ui.common

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.ui.seeker.ChapterSegment
import net.newpipe.newplayer.ui.seeker.DefaultSeekerColor
import net.newpipe.newplayer.ui.seeker.Seeker
import net.newpipe.newplayer.ui.seeker.SeekerColors

private const val TAG = "NewPlayerSeeker"

@OptIn(UnstableApi::class)
@Composable

/** @hide */
internal fun NewPlayerSeeker(
    modifier: Modifier = Modifier,
    viewModel: InternalNewPlayerViewModel,
    uiState: NewPlayerUIState
) {
    Seeker(
        modifier = modifier,
        value = uiState.seekerPosition,
        onValueChange = viewModel::seekPositionChanged,
        onValueChangeFinished = viewModel::seekingFinished,
        readAheadValue = uiState.bufferedPercentage,
        colors = customizedSeekerColors(),
        chapterSegments = getSeekerSegmentsFromChapters(uiState.chapters, uiState.durationInMs)
    )
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

