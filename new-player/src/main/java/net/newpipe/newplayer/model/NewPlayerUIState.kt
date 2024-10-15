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

import androidx.compose.ui.graphics.ImageBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.utils.Chapter
import net.newpipe.newplayer.RepeatMode
import net.newpipe.newplayer.ui.ContentScale
import net.newpipe.newplayer.utils.AudioStreamTrack
import net.newpipe.newplayer.utils.LanguageIdentifier
import net.newpipe.newplayer.utils.StreamTrack
import net.newpipe.newplayer.utils.VideoStreamTrack


@UnstableApi
data class NewPlayerUIState(
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
    val playbackPositionInPlaylistMs: Long,
    val fastSeekSeconds: Int,
    val soundVolume: Float,
    val brightness: Float?,     // when null use system value
    val embeddedUiConfig: EmbeddedUiConfig?,
    val playList: List<MediaItem>,
    val chapters: List<Chapter>,
    val shuffleEnabled: Boolean,
    val repeatMode: RepeatMode,
    val playListDurationInS: Int,
    val currentlyPlaying: MediaItem?,
    val currentPlaylistItemIndex: Int,
    val currentlyAvailableTracks: List<StreamTrack>,
    val enteringPip: Boolean,
    val currentSeekPreviewThumbnail: ImageBitmap?,
    val seekPreviewVisible: Boolean,
) {
    companion object {
        val DEFAULT = NewPlayerUIState(
            uiMode = UIModeState.PLACEHOLDER,
            playing = false,
            contentRatio = 16 / 9f,
            embeddedUiRatio = 16f / 9f,
            contentFitMode = ContentScale.FIT_INSIDE,
            seekerPosition = 0f,
            bufferedPercentage = 0f,
            isLoading = true,
            durationInMs = 0,
            playbackPositionInPlaylistMs = 0,
            playbackPositionInMs = 0,
            fastSeekSeconds = 0,
            soundVolume = 0f,
            brightness = null,
            embeddedUiConfig = null,
            playList = emptyList(),
            chapters = emptyList(),
            shuffleEnabled = false,
            repeatMode = RepeatMode.DO_NOT_REPEAT,
            playListDurationInS = 0,
            currentlyPlaying = null,
            currentPlaylistItemIndex = 0,
            currentlyAvailableTracks = emptyList(),
            enteringPip = false,
            currentSeekPreviewThumbnail = null,
            seekPreviewVisible = false,
        )

        val DUMMY = DEFAULT.copy(
            currentlyAvailableTracks = listOf(
                VideoStreamTrack(width= 1920, height = 1080, frameRate = 30, fileFormat = "MPEG4"),
                VideoStreamTrack(width= 1280, height = 720, frameRate = 30, fileFormat = "MPEG4"),
                VideoStreamTrack(width= 853, height = 480, frameRate = 30, fileFormat = "MPEG4"),
                AudioStreamTrack(bitrate = 49000, language = LanguageIdentifier.EN, fileFormat = "MP4A"),
                AudioStreamTrack(bitrate = 49000, language = LanguageIdentifier.ES, fileFormat = "MP4A")
            ),
            uiMode = UIModeState.EMBEDDED_VIDEO,
            playing = true,
            seekerPosition = 0.3f,
            bufferedPercentage = 0.5f,
            isLoading = false,
            durationInMs = 12000,
            playbackPositionInPlaylistMs = 5039,
            playbackPositionInMs = 400,
            fastSeekSeconds = 10,
            soundVolume = 0.5f,
            brightness = 0.2f,
            shuffleEnabled = true,
            playListDurationInS = 5493,
            currentlyPlaying = MediaItem.Builder()
                .setUri("https://ftp.fau.de/cdn.media.ccc.de/congress/2010/mp4-h264-HQ/27c3-4159-en-reverse_engineering_mos_6502.mp4")
                .setMediaId("0")
                .setMediaMetadata(MediaMetadata.Builder()
                    .setTitle("Superawesome Video")
                    .setArtist("Yours truely")
                    .setArtworkUri(null)
                    .setDurationMs(4201000L)
                    .build())
                .build(),
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
                MediaItem.Builder()
                    .setUri("https://ftp.fau.de/cdn.media.ccc.de/congress/2010/mp4-h264-HQ/27c3-4159-en-reverse_engineering_mos_6502.mp4")
                    .setMediaId("0")
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setTitle("Stream 1")
                        .setArtist("Yours truely")
                        .setArtworkUri(null)
                        .setDurationMs(4201000L)
                        .build())
                    .build(),
                MediaItem.Builder()
                    .setUri("https://ftp.fau.de/cdn.media.ccc.de/congress/2010/mp4-h264-HQ/27c3-4159-en-reverse_engineering_mos_6502.mp4")
                    .setMediaId("1")
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setTitle("Stream 2")
                        .setArtist("Yours truely")
                        .setArtworkUri(null)
                        .setDurationMs(3201000L)
                        .build())
                    .build(),
                MediaItem.Builder()
                    .setUri("https://ftp.fau.de/cdn.media.ccc.de/congress/2010/mp4-h264-HQ/27c3-4159-en-reverse_engineering_mos_6502.mp4")
                    .setMediaId("2")
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setTitle("Stream 3")
                        .setArtist("Yours truely")
                        .setArtworkUri(null)
                        .setDurationMs(2201000L)
                        .build())
                    .build(),
            )
        )
    }
}