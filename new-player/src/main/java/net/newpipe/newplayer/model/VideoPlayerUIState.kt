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

import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.RepeatMode
import net.newpipe.newplayer.playerInternals.PlaylistItem
import net.newpipe.newplayer.ui.ContentScale

data class VideoPlayerUIState(
    val uiMode: UIModeState,
    val playing: Boolean,
    val contentRatio: Float,
    val embeddedUiRatio: Float,
    val contentFitMode: ContentScale,
    val seekerPosition: Float,
    val bufferedPercentage: Float,
    val isLoading: Boolean,
    val durationInMs: Long,
    val playbackPositionInMs: Long,
    val playbackPositionInPlaylistS: Int,
    val fastSeekSeconds: Int,
    val soundVolume: Float,
    val brightness: Float?,     // when null use system value
    val embeddedUiConfig: EmbeddedUiConfig?,
    val playList: List<PlaylistItem>,
    val chapters: List<Chapter>,
    val shuffleEnabled: Boolean,
    val repeatMode: RepeatMode,
    val playListDurationInS: Int,
    val currentlyPlaying: PlaylistItem,
    val currentPlaylistItemIndex: Int
) {
    companion object {
        val DEFAULT = VideoPlayerUIState(
            uiMode = UIModeState.PLACEHOLDER,
            playing = false,
            contentRatio = 16 / 9f,
            embeddedUiRatio = 16f / 9f,
            contentFitMode = ContentScale.FIT_INSIDE,
            seekerPosition = 0f,
            bufferedPercentage = 0f,
            isLoading = true,
            durationInMs = 0,
            playbackPositionInPlaylistS = 0,
            playbackPositionInMs = 0,
            fastSeekSeconds = 0,
            soundVolume = 0f,
            brightness = null,
            embeddedUiConfig = null,
            playList = emptyList(),
            chapters = emptyList(),
            shuffleEnabled = false,
            repeatMode = RepeatMode.DONT_REPEAT,
            playListDurationInS = 0,
            currentlyPlaying = PlaylistItem.DEFAULT,
            currentPlaylistItemIndex = 0
        )

        val DUMMY = DEFAULT.copy(
            uiMode = UIModeState.EMBEDDED_VIDEO,
            playing = true,
            seekerPosition = 0.3f,
            bufferedPercentage = 0.5f,
            isLoading = false,
            durationInMs = 12000,
            playbackPositionInPlaylistS = 5039,
            playbackPositionInMs = 69,
            fastSeekSeconds = 10,
            soundVolume = 0.5f,
            brightness = 0.2f,
            shuffleEnabled = true,
            playListDurationInS = 5493,
            currentlyPlaying = PlaylistItem.DUMMY,
            currentPlaylistItemIndex = 1,
            chapters = arrayListOf(
                Chapter(
                    chapterStartInMs = 0,
                    chapterTitle = "Intro",
                    thumbnail = null
                ),
                Chapter(
                    chapterStartInMs = 5000,
                    chapterTitle = "First Chapter",
                    thumbnail = null
                ),
                Chapter(
                    chapterStartInMs = 10000,
                    chapterTitle = "Second Chapter",
                    thumbnail = null
                ),
            ),
            playList = arrayListOf(
                PlaylistItem(
                    id = "6502",
                    title = "Stream 1",
                    creator = "The Creator",
                    lengthInS = 6 * 60 + 5,
                    thumbnail = null,
                    uniqueId = 0
                ),
                PlaylistItem(
                    id = "6502",
                    title = "Stream 2",
                    creator = "The Creator 2",
                    lengthInS = 2 * 60 + 5,
                    thumbnail = null,
                    uniqueId = 1
                ),
                PlaylistItem(
                    id = "6502",
                    title = "Stream 3",
                    creator = "The Creator 3",
                    lengthInS = 29 * 60 + 5,
                    thumbnail = null,
                    uniqueId = 2
                )
            )
        )
    }
}