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

data class Stream(
    val item: String,
    val streamUri: Uri,
    val streamTracks: List<StreamTrack>,
    val mimeType: String? = null,
    val isDashOrHls: Boolean = false
) {

    val languages: List<LanguageIdentifier>
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

    override fun equals(other: Any?) =
        other is Stream
                && other.hashCode() == this.hashCode()

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + streamUri.hashCode()
        result = 31 * result + streamTracks.hashCode()
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        return result
    }

}