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

import androidx.compose.ui.graphics.ImageBitmap
import androidx.media3.common.Player
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.ui.ContentScale
import net.newpipe.newplayer.data.AudioStreamTrack
import net.newpipe.newplayer.data.RepeatMode
import net.newpipe.newplayer.data.StreamTrack
import net.newpipe.newplayer.data.VideoStreamTrack
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.NewPlayer

/**
 * Shows the state the UI is rendering. In the manner of MVVM, [NewPlayerViewModel] sends this
 * state over to [NewPlayerUI], to tell it what should be rendered.
 */
@UnstableApi
data class NewPlayerUIState(
    /**
     * Depicts which screen of NewPlayer's UI should be visible.
     */
    val uiMode: UIModeState,

    /**
     * Is the stream currently playing or not.
     */
    val playing: Boolean,

    /**
     * The content ratio of the currently playing video.
     */
    val contentRatio: Float,

    /**
     * The ratio of the embedded UI. This might differ from the content ratio.
     */
    val embeddedUiRatio: Float,

    /**
     * Depicts weather the video should fit inside, be cropped or stretched.
     */
    val contentFitMode: ContentScale,

    /**
     * The position of the seeker thumb. Whereas seekerPosition ∈ [0; 1].
     */
    val seekerPosition: Float,

    /**
     * The amount of buffered video/audio material, Whereas bufferedPercentage ∈ [0; 1].
     */
    val bufferedPercentage: Float,

    /**
     * The same as ExoPlayers isLoading. See [Player.isLoading]
     */
    val isLoading: Boolean,

    /**
     * The duration of the currently playing stream in milliseconds.
     */
    val durationInMs: Long,

    /**
     * The playback position of the currently playing stream in milliseconds.
     * This only updates if the progress updater job runs within [NewPlayerViewModel].
     */
    val playbackPositionInMs: Long,

    /**
     * The playback position within the whole currently playing playlist.
     */
    val playbackPositionInPlaylistMs: Long,

    /**
     * Amount of seconds on step of fast seek can take.
     */
    val fastSeekSeconds: Int,

    /**
     * The current sound volume.
     */
    val soundVolume: Float,

    /**
     * The brightness volume. Might be null if the system is in control of the brightness.
     */
    val brightness: Float?,

    /**
     * This is used to restore several values when switching back from a fullscreen view to an
     * embedded screen view. Such values include the color scheme of the system info bar, the
     * screen brightness and the default screen orientation.
     *
     * At least within the test app restoring these values does not always correctly work.
     * One reason this might be is because the main activity layout of the test app actually hosts
     * two NewPlayerViews. Because the viewModel now switches between these two views weird things
     * happen. For the sake of better animations and this EmbeddedUiConfig stuff to work it was
     * better to only have one NewPlayerView in the whole layout. Or ... well at best directly
     * Compose.
     */
    val embeddedUiConfig: EmbeddedUiConfig?,

    /**
     * The currently playing playlist as provided by ExoPlayer. However, it might sometimes
     * drift from the playlist provided by exoplayer. This can happen when the user reorders
     * items. The playlist will then immediately reflect the new state of the list, even though
     * that state was not yet confirmed by exoplayer. If this list could not drift you would expect
     * the UI to be jittery upon item reordering.
     */
    val playList: List<MediaItem>,

    /**
     * The chapters provided by the currently playing item. Might be empty if the item does not have
     * chapters or when the chapter didn't load up yet.
     */
    val chapters: List<Chapter>,

    /**
     * Same as [NewPlayer.shuffle].
     */
    val shuffleEnabled: Boolean,

    /**
     * Same as [NewPlayer.repeatMode]
     */
    val repeatMode: RepeatMode,

    /**
     * Duration of the whole currently playing playlist
     */
    val playListDurationInS: Int,

    /**
     * The media item that is currently playing.
     */
    val currentlyPlaying: MediaItem?,

    /**
     * The index into the [playList], that depicts the currently playing item.
     */
    val currentPlaylistItemIndex: Int,

    /**
     * The tracks that are available for the currently playing item.
     */
    val currentlyAvailableTracks: List<StreamTrack>,

    /**
     * The tracks that are currently playing.
     * in other words these tracks are actively playing back and are getting muxed together by
     * ExoPlayer.
     */
    val currentlyPlayingTracks: List<StreamTrack>,

    /**
     * Is the Ui currently transitioning to PiP mode.
     */
    val enteringPip: Boolean,

    /**
     * The seeker preview thumbnail that should be visible. This updates if the user uses
     * the seeker thumb to seek through a stream. If null no thumbnail is available.
     */
    val currentSeekPreviewThumbnail: ImageBitmap?,

    /**
     * Depicts weather the seeker preview thumbnail should be visible or not.
     */
    val seekPreviewVisible: Boolean,
) {
    companion object {

        /**
         * Default object to initialize the UIState.
         */
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
            currentlyPlayingTracks = emptyList(),
            enteringPip = false,
            currentSeekPreviewThumbnail = null,
            seekPreviewVisible = false,
        )

        /**
         * Dummy object that is mainly used for Compose previews.
         */
        val DUMMY = DEFAULT.copy(
            currentlyAvailableTracks = listOf(
                VideoStreamTrack(width= 1920, height = 1080, frameRate = 30, fileFormat = "MPEG4"),
                VideoStreamTrack(width= 1280, height = 720, frameRate = 30, fileFormat = "MPEG4"),
                VideoStreamTrack(width= 853, height = 480, frameRate = 30, fileFormat = "MPEG4"),
                AudioStreamTrack(bitrate = 49000, language = "en", fileFormat = "MP4A"),
                AudioStreamTrack(bitrate = 49000, language = "es", fileFormat = "MP4A")
            ),
            currentlyPlayingTracks = listOf(
                VideoStreamTrack(width= 1920, height = 1080, frameRate = 30, fileFormat = "MPEG4"),
                AudioStreamTrack(bitrate = 49000, language = "es", fileFormat = "MP4A")
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