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

package net.newpipe.newplayer.utils

import android.net.Uri

enum class StreamType {
    VIDEO,
    AUDIO,
    AUDIO_AND_VIDEO,
    DYNAMIC
}

data class Stream(
    val streamUri: Uri,
    val identifier: String,
    val streamType: StreamType,
    val languages: List<String>,
    val mimeType: String? = null,
) {
    override fun equals(other: Any?) =
        other is Stream
                && other.streamUri == streamUri
                && other.streamType == streamType
                && other.identifier == identifier
                && other.mimeType == mimeType
                && run {
            for (language in languages) {
                if (!other.languages.contains(language)) {
                    return@run false
                }
            }
            true
        }

}