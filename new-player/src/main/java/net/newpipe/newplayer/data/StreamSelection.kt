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

import net.newpipe.newplayer.logic.TrackUtils

interface StreamSelection {
    val item: String

    val tracks : List<StreamTrack>
    val hasVideoTracks:Boolean
    val hasAudioTracks:Boolean
    val isDynamic:Boolean
}

data class SingleSelection(
    val stream: Stream
) : StreamSelection {
    override val item: String
        get() = stream.item

    override val tracks: List<StreamTrack>
        get() = stream.streamTracks

    override val hasVideoTracks: Boolean
        get() = stream.hasVideoTracks

    override val hasAudioTracks: Boolean
        get() = stream.hasVideoTracks

    override val isDynamic: Boolean
        get() = stream.isDashOrHls
}

data class MultiSelection(
    val streams: List<Stream>
) : StreamSelection {

    override val item: String
        get() = streams[0].item

    override val tracks: List<StreamTrack>
        get() {
            val allTracks = mutableListOf<StreamTrack>()
            streams.forEach { allTracks.addAll(it.streamTracks) }
            return allTracks
        }

    override val hasVideoTracks: Boolean
        get() = TrackUtils.hasVideoTracks(streams)

    override val hasAudioTracks: Boolean
        get() = TrackUtils.hasAudioTracks(streams)

    override val isDynamic: Boolean
        get() = TrackUtils.hasDynamicStreams(streams)
}