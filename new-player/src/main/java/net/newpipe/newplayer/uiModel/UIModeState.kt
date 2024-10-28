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

import net.newpipe.newplayer.data.PlayMode


/**
 * This depicts the UI mode. In layman's terms you could say it depicts which
 * of NewPlayer's many UI screens should be visible.
 * The UIModeState can be derived from the [PlayMode].
 * However, while [PlayMode] depicts in which mode to play a certain item in,
 * UIModeState is more detailed and depicts the actually visible UI.
 * Because of this multiple UIModeStates can be mapped to the same [PlayMode].
 *
 * UIModeState is mainly thought to be used and modified 
/** @hide */
internally. However,
 * since a UIModeState is part of the [NewPlayerUIState], it is public.
 */
enum class UIModeState {
    /**
     * Same as [PlayMode.IDLE]
     */
    PLACEHOLDER,

    EMBEDDED_VIDEO,
    EMBEDDED_VIDEO_CONTROLLER_UI,
    EMBEDDED_VIDEO_CHAPTER_SELECT,
    EMBEDDED_VIDEO_STREAM_SELECT,

    FULLSCREEN_VIDEO,
    FULLSCREEN_VIDEO_CONTROLLER_UI,
    FULLSCREEN_VIDEO_CHAPTER_SELECT,
    FULLSCREEN_VIDEO_STREAM_SELECT,
    PIP,

    EMBEDDED_AUDIO,
    FULLSCREEN_AUDIO,
    AUDIO_CHAPTER_SELECT,
    AUDIO_STREAM_SELECT;

    /**
     * Depicts if the current mode requires the UI to be in fullscreen mode.
     */
    val fullscreen: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CHAPTER_SELECT -> true
                EMBEDDED_VIDEO_STREAM_SELECT -> true
                FULLSCREEN_VIDEO -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> true
                FULLSCREEN_AUDIO -> true
                AUDIO_CHAPTER_SELECT -> true
                AUDIO_STREAM_SELECT -> true
                PIP -> true
                PLACEHOLDER -> false
                EMBEDDED_VIDEO -> false
                EMBEDDED_VIDEO_CONTROLLER_UI -> false
                EMBEDDED_AUDIO -> false
            }

    val videoControllerUiVisible: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                else -> false
            }

    val isStreamSelect: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_STREAM_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> true
                AUDIO_STREAM_SELECT -> true
                else -> false
            }

    val isChapterSelect: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                AUDIO_CHAPTER_SELECT -> true
                else -> false
            }

    val systemInsetsVisible: Boolean
        get() =
            when (this) {
                FULLSCREEN_VIDEO -> false
                PIP -> false
                else -> true
            }

    /**
     * Depicts if the current mode requires the screen to be rotated to best fit the video dimensions.
     */
    val fitScreenRotation: Boolean
        get() =
            when (this) {
                FULLSCREEN_VIDEO -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> true
                PIP -> true
                else -> false
            }

    /**
     * Depicts if the current state requires the progress update job of the viewModel to run.
     */
    val requiresProgressUpdate: Boolean
        get() =
            when (this) {
                PLACEHOLDER -> false
                EMBEDDED_VIDEO -> false
                EMBEDDED_VIDEO_CONTROLLER_UI -> true
                EMBEDDED_VIDEO_CHAPTER_SELECT -> true
                EMBEDDED_VIDEO_STREAM_SELECT -> false
                FULLSCREEN_VIDEO -> false
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> false
                EMBEDDED_AUDIO -> true
                FULLSCREEN_AUDIO -> true
                AUDIO_CHAPTER_SELECT -> true
                AUDIO_STREAM_SELECT -> false
                PIP -> false
            }

    val inAudioMode: Boolean
        get() = when(this) {
            PLACEHOLDER -> false
            EMBEDDED_VIDEO -> false
            EMBEDDED_VIDEO_CONTROLLER_UI -> false
            EMBEDDED_VIDEO_CHAPTER_SELECT ->false
            EMBEDDED_VIDEO_STREAM_SELECT -> false
            FULLSCREEN_VIDEO -> false
            FULLSCREEN_VIDEO_CONTROLLER_UI -> false
            FULLSCREEN_VIDEO_CHAPTER_SELECT -> false
            FULLSCREEN_VIDEO_STREAM_SELECT -> false
            PIP -> false
            EMBEDDED_AUDIO -> true
            FULLSCREEN_AUDIO -> true
            AUDIO_CHAPTER_SELECT -> true
            AUDIO_STREAM_SELECT -> true
        }

    // STATE TRANSITIONS

    fun getControllerUiVisibleState() =
        when (this) {
            EMBEDDED_VIDEO -> EMBEDDED_VIDEO_CONTROLLER_UI
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO_CONTROLLER_UI

            else -> this
        }

    fun getUiHiddenState() =
        when (this) {
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO
            FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO
            FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO
            FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO

            EMBEDDED_VIDEO -> EMBEDDED_VIDEO
            EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO
            EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO
            EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO

            else -> this
        }


    fun getStreamSelectUiState() =
        when (this) {
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO_STREAM_SELECT
            FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO_STREAM_SELECT
            FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO_STREAM_SELECT


            EMBEDDED_VIDEO -> EMBEDDED_VIDEO_STREAM_SELECT
            EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO_STREAM_SELECT
            EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO_STREAM_SELECT

            FULLSCREEN_AUDIO -> AUDIO_STREAM_SELECT
            EMBEDDED_AUDIO -> AUDIO_STREAM_SELECT
            AUDIO_CHAPTER_SELECT -> AUDIO_STREAM_SELECT

            PIP -> EMBEDDED_VIDEO_STREAM_SELECT

            else -> this
        }

    fun getChapterSelectUiState() =
        when (this) {
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO_CHAPTER_SELECT
            FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO_CHAPTER_SELECT
            FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO_CHAPTER_SELECT

            EMBEDDED_VIDEO -> EMBEDDED_VIDEO_CHAPTER_SELECT
            EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO_CHAPTER_SELECT
            EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO_CHAPTER_SELECT

            FULLSCREEN_AUDIO -> AUDIO_CHAPTER_SELECT
            EMBEDDED_AUDIO -> AUDIO_CHAPTER_SELECT
            AUDIO_STREAM_SELECT -> AUDIO_CHAPTER_SELECT

            PIP -> EMBEDDED_VIDEO_CHAPTER_SELECT

            else -> this
        }

    fun toPlayMode() = when (this) {
        PLACEHOLDER -> PlayMode.IDLE
        EMBEDDED_VIDEO -> PlayMode.EMBEDDED_VIDEO
        EMBEDDED_VIDEO_CONTROLLER_UI -> PlayMode.EMBEDDED_VIDEO
        EMBEDDED_VIDEO_CHAPTER_SELECT -> PlayMode.EMBEDDED_VIDEO
        EMBEDDED_VIDEO_STREAM_SELECT -> PlayMode.EMBEDDED_VIDEO
        FULLSCREEN_VIDEO -> PlayMode.FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CONTROLLER_UI -> PlayMode.FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CHAPTER_SELECT -> PlayMode.FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_STREAM_SELECT -> PlayMode.FULLSCREEN_VIDEO
        EMBEDDED_AUDIO -> PlayMode.EMBEDDED_AUDIO
        FULLSCREEN_AUDIO -> PlayMode.FULLSCREEN_AUDIO
        AUDIO_CHAPTER_SELECT -> PlayMode.FULLSCREEN_AUDIO
        AUDIO_STREAM_SELECT -> PlayMode.FULLSCREEN_AUDIO
        PIP -> PlayMode.PIP
    }

    fun getNextModeWhenBackPressed() = when (this) {
        EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO
        EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO
        FULLSCREEN_VIDEO -> EMBEDDED_VIDEO
        FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO
        PLACEHOLDER -> null
        EMBEDDED_VIDEO -> null
        EMBEDDED_VIDEO_CONTROLLER_UI -> null
        EMBEDDED_AUDIO -> null
        FULLSCREEN_AUDIO -> EMBEDDED_AUDIO
        AUDIO_CHAPTER_SELECT -> FULLSCREEN_AUDIO
        AUDIO_STREAM_SELECT -> FULLSCREEN_AUDIO
        PIP -> null
    }

    fun getAudioEquivalent() = when (this) {
        PLACEHOLDER -> PLACEHOLDER
        EMBEDDED_VIDEO -> EMBEDDED_AUDIO
        EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_AUDIO
        EMBEDDED_VIDEO_CHAPTER_SELECT -> AUDIO_CHAPTER_SELECT
        EMBEDDED_VIDEO_STREAM_SELECT -> AUDIO_STREAM_SELECT
        FULLSCREEN_VIDEO -> FULLSCREEN_AUDIO
        FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_AUDIO
        FULLSCREEN_VIDEO_CHAPTER_SELECT -> AUDIO_CHAPTER_SELECT
        FULLSCREEN_VIDEO_STREAM_SELECT -> AUDIO_STREAM_SELECT
        PIP -> FULLSCREEN_AUDIO
        EMBEDDED_AUDIO -> EMBEDDED_AUDIO
        FULLSCREEN_AUDIO -> FULLSCREEN_AUDIO
        AUDIO_CHAPTER_SELECT -> AUDIO_CHAPTER_SELECT
        AUDIO_STREAM_SELECT -> AUDIO_STREAM_SELECT
    }

    fun getVideoEquivalent() = when (this) {
        PLACEHOLDER -> PLACEHOLDER
        EMBEDDED_VIDEO -> EMBEDDED_VIDEO
        EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO_CONTROLLER_UI
        EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO_CHAPTER_SELECT
        EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO_STREAM_SELECT
        FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO_CONTROLLER_UI
        FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO_CHAPTER_SELECT
        FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO_STREAM_SELECT
        PIP -> PIP
        EMBEDDED_AUDIO -> EMBEDDED_VIDEO
        FULLSCREEN_AUDIO -> FULLSCREEN_VIDEO
        AUDIO_CHAPTER_SELECT -> FULLSCREEN_VIDEO_CHAPTER_SELECT
        AUDIO_STREAM_SELECT -> FULLSCREEN_VIDEO_STREAM_SELECT
    }

    companion object {
        fun fromPlayMode(playMode: PlayMode) =
            when (playMode) {
                PlayMode.IDLE -> PLACEHOLDER
                PlayMode.EMBEDDED_VIDEO -> EMBEDDED_VIDEO
                PlayMode.FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO
                PlayMode.PIP -> PIP
                PlayMode.BACKGROUND_VIDEO -> TODO()
                PlayMode.BACKGROUND_AUDIO -> TODO()
                PlayMode.FULLSCREEN_AUDIO -> FULLSCREEN_AUDIO
                PlayMode.EMBEDDED_AUDIO -> EMBEDDED_AUDIO
            }
    }
}