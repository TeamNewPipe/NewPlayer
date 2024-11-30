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

package net.newpipe.newplayer.data

import android.net.Uri

/**
 * A stream represents one actual video stream that is associated with a video.
 * Each stream has its own URI (and therefore correspond to one individual video container).
 * Each stream can contain multiple audio/video tracks.
 *
 * @param item The item the stream belongs to.
 * @param streamUri the URI of the stream.
 * @param streamTracks the tracks that the stream contains
 * @param mimeType The mime type of the stream. This may only be set if ExoPlayer might not be
 * able to infer the type of of the stream from the Uri itself.
 * @param isDashOrHls depicts wather its a dynamic stream or not.
 */
data class Stream(
    val item: String,
    val streamUri: Uri,
    val streamTracks: List<StreamTrack>,
    val mimeType: String? = null,
    val isDashOrHls: Boolean = false
) {

    /**
     * The list of audio languages provided by the stream.
     */
    val languages: List<String>
        get() = streamTracks.filterIsInstance<AudioStreamTrack>().mapNotNull { it.language }


    val hasAudioTracks: Boolean
        get() {
            streamTracks.forEach { if (it is AudioStreamTrack) return true }
            return false
        }

    val hasVideoTracks: Boolean
        get() {
            streamTracks.forEach { if (it is VideoStreamTrack) return true }
            return false
        }


    val videoStreamTracks: List<VideoStreamTrack>
        get() = streamTracks.filterIsInstance<VideoStreamTrack>()

    val audioStreamTrack: List<AudioStreamTrack>
        get() = streamTracks.filterIsInstance<AudioStreamTrack>()

}