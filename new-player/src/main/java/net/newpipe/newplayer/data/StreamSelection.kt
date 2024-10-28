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

import androidx.media3.exoplayer.source.MergingMediaSource
import net.newpipe.newplayer.logic.TrackUtils

/**
 * A selection of streams that should be used for playback.
 * This is used by the stream selection algorithm to depict which streams should be used
 * to build a MediaSource from and thus forward to the actual ExoPlayer.
 */
interface StreamSelection {
    val item: String

    val tracks : List<StreamTrack>
    val hasVideoTracks:Boolean
    val hasAudioTracks:Boolean
    val isDynamic:Boolean
}

/**
 * This is used if only one single stream should be forwarded to ExoPlayer.
 * This can be either a DASH/HLS stream or a progressive stream that has all the required
 * tracks already muxed together.
 */
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

/**
 * This can be used if tracks from multiple streams should be forwarded to ExoPlayer, so
 * ExoPlayer can mux them together.
 * This StreamSelection will be made into a [MergingMediaSource].
 * This stream selection will not depict which of the tracks contained in the StreamSelection
 * should be muxed together by ExoPlayer. This MultiSelection only depicts that at least all the
 * tracks that should be played are contained.
 *
 * The information to pick the actual tracks out of the available tracks within this selection
 * bust be given to ExoPlayer through another mechanism. (You see this is still TODO).
 */
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