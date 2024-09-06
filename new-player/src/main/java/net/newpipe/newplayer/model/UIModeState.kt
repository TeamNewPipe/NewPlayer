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

import net.newpipe.newplayer.PlayMode

enum class UIModeState {
    PLACEHOLDER,

    EMBEDDED_VIDEO,
    EMBEDDED_VIDEO_CONTROLLER_UI,
    EMBEDDED_VIDEO_CHAPTER_SELECT,
    EMBEDDED_VIDEO_STREAM_SELECT,

    FULLSCREEN_VIDEO,
    FULLSCREEN_VIDEO_CONTROLLER_UI,
    FULLSCREEN_VIDEO_CHAPTER_SELECT,
    FULLSCREEN_VIDEO_STREAM_SELECT;

    val fullscreen: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CHAPTER_SELECT -> true
                EMBEDDED_VIDEO_STREAM_SELECT -> true
                FULLSCREEN_VIDEO -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> true
                else -> false
            }

    val controllerUiVisible: Boolean
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
                else -> false
            }

    val isChapterSelect: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                else -> false
            }

    val systemInsetsVisible: Boolean
        get() =
            when (this) {
                FULLSCREEN_VIDEO -> false
                else -> true
            }

    val fitScreenRotation: Boolean
        get() =
            when (this) {
                FULLSCREEN_VIDEO -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> true
                else -> false
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
    }

    fun getNextModeWhenBackPressed() = when (this) {
        EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO
        EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO
        FULLSCREEN_VIDEO -> EMBEDDED_VIDEO
        FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO
        FULLSCREEN_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO
        else -> null
    }

    companion object {
        fun fromPlayMode(playMode: PlayMode) =
            when (playMode) {
                PlayMode.IDLE -> PLACEHOLDER
                PlayMode.EMBEDDED_VIDEO -> EMBEDDED_VIDEO
                PlayMode.FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO
                PlayMode.PIP -> TODO()
                PlayMode.BACKGROUND -> TODO()
                PlayMode.AUDIO_FOREGROUND -> TODO()
            }
    }
}