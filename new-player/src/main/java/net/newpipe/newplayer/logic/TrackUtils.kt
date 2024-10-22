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

package net.newpipe.newplayer.logic

 import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.data.AudioStreamTrack
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamTrack
import net.newpipe.newplayer.data.VideoStreamTrack

private const val TAG = "TrackUtils"

/**
 * This provides several tools to handle [StreamTrack]s.
 */
object TrackUtils {

    internal fun getAllAvailableTracksNonDuplicated(streams: List<Stream>): List<StreamTrack> {
        val totalList = mutableListOf<StreamTrack>()
        streams.forEach {
            totalList.addAll(it.streamTracks)
        }
        totalList.sort()
        return totalList.distinct()
    }

    internal fun getNonDynamicTracksNonDuplicated(streams: List<Stream>) =
        getAllAvailableTracksNonDuplicated(streams.filter { !it.isDashOrHls })

    internal fun getAvailableLanguages(tracks: List<StreamTrack>) =
        tracks.filterIsInstance<AudioStreamTrack>().mapNotNull { it.language }

    internal fun getBestLanguageFit(
        availableStreams: List<Stream>, preferredLanguages: List<String>
    ): String? {
        for (preferredLanguage in preferredLanguages) {
            for (available in availableStreams) {
                if (available.languages.contains(
                        preferredLanguage
                    )
                ) {
                    return preferredLanguage
                }
            }
        }
        return null
    }

    internal fun filtersByLanguage(
        availableStreams: List<Stream>, language: String
    ) = availableStreams.filter {
        it.languages.contains(
            language
        )
    }

    internal fun tryAndGetMedianVideoOnlyTracks(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && it.hasVideoTracks && !it.hasAudioTracks }
            .ifEmpty { null }?.let {
                it[it.size / 2]
            }

    internal fun tryAndGetMedianCombinedVideoAndAudioTracks(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && it.hasVideoTracks && it.hasVideoTracks }
            .ifEmpty { null }
            ?.let {
                it[it.size / 2]
            }

    internal fun tryAndGetMedianAudioOnlyTracks(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && it.hasAudioTracks && !it.hasVideoTracks }
            .ifEmpty { null }?.let {
                it[it.size / 2]
            }


    internal fun getDemuxedStreams(
        availableStreams: List<Stream>
    ) = availableStreams.filter {
        !it.isDashOrHls && it.streamTracks.size == 1
    }


    internal fun getDynamicStreams(availableStreams: List<Stream>) =
        availableStreams.filter { it.isDashOrHls }

    internal fun getNonDynamicVideoTracks(availableStreams: List<Stream>) =
        availableStreams.filter {
            !it.isDashOrHls && it.hasVideoTracks && !it.hasAudioTracks
        }

    internal fun getNonDynamicAudioTracks(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && !it.hasVideoTracks && it.hasAudioTracks }

    internal fun hasVideoTracks(availableStreams: List<Stream>): Boolean {
        availableStreams.forEach {
            if (it.hasVideoTracks)
                return true
        }
        return false
    }

    internal fun hasAudioTracks(availableStreams: List<Stream>): Boolean {
        availableStreams.forEach {
            if (it.hasAudioTracks)
                return true
        }
        return false
    }

    internal fun hasDynamicStreams(availableStreams: List<Stream>): Boolean {
        availableStreams.forEach {
            if (it.isDashOrHls)
                return true
        }
        return false
    }

    /**
     * You can filter for [StreamTrack]s by simply testing if a track equals the one
     * you are looking for.
     * However maybe you just want to filter tracks based on only some attributes.
     * You can do this with this function.
     */
    internal fun getStreamMatchingAudioTrack(
        availableStreams: List<Stream>,
        track: AudioStreamTrack,
        matchLanguage: Boolean = true,
        matchBitrate: Boolean = true,
        matchFileFormat: Boolean = true,
    ) = availableStreams.filterIsInstance<AudioStreamTrack>()
        .filter{!matchLanguage || it.language == track.language}
        .filter{!matchBitrate || it.bitrate == track.bitrate}
        .filter{!matchFileFormat || it.fileFormat == track.fileFormat}

    /**
     * You can filter for [StreamTrack]s by simply testing if a track equals the one
     * you are looking for.
     * However maybe you just want to filter tracks based on only some attributes.
     * You can do this with this function.
     */
    internal fun getStreamsMatchingVideoTrack(
        availableStreams: List<Stream>,
        track: VideoStreamTrack,
        matchWidth: Boolean = true,
        matchHeight: Boolean = true,
        matchFrameRate: Boolean = true,
        matchFileFormat: Boolean = true
    ) = availableStreams.filterIsInstance<VideoStreamTrack>()
        .filter{!matchWidth || it.width == track.width}
        .filter{!matchHeight || it.height == track.height}
        .filter{!matchFrameRate || it.frameRate == track.frameRate}
        .filter{!matchFileFormat || it.fileFormat == track.fileFormat}

    @OptIn(UnstableApi::class)
    internal fun streamTracksFromMedia3Tracks(
        media3Tracks: Tracks,
        onlySelectedTracks: Boolean = false
    ): List<StreamTrack> {
        val tracks = mutableListOf<StreamTrack>()
        for (group in media3Tracks.groups) {
            for (i in 0 until group.length) {
                if (onlySelectedTracks && group.isTrackSelected(i))
                    continue

                val format = group.getTrackFormat(i)
                when (group.type) {
                    C.TRACK_TYPE_AUDIO ->
                        tracks.add(
                            AudioStreamTrack(
                                bitrate = format.bitrate,
                                fileFormat = "",
                                language = format.language
                            )
                        )

                    C.TRACK_TYPE_VIDEO ->
                        tracks.add(
                            VideoStreamTrack(
                                width = format.width,
                                height = format.height,
                                frameRate = format.frameRate.toInt(),
                                fileFormat = ""
                            )
                        )

                    C.TRACK_TYPE_TEXT -> Unit
                    else -> Log.w(
                        TAG,
                        "Unknown or unsupported media3 track type encountered during track conversion: $group.type"
                    )
                }
            }
        }
        tracks.sort()

        return tracks.distinct()
    }
}
