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
        availableStreams: List<Stream>, preferredLanguages: List<LanguageIdentifier>
    ): LanguageIdentifier? {
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
        availableStreams: List<Stream>, language: LanguageIdentifier
    ) = availableStreams.filter {
        it.languages.contains(
            language
        )
    }

    internal fun tryAndGetMedianVideoOnlyStream(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && it.hasVideoTracks && !it.hasAudioTracks }
            .ifEmpty { null }?.let {
                it[it.size / 2]
            }

    internal fun tryAndGetMedianCombinedVideoAndAudioStream(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && it.hasVideoTracks && it.hasVideoTracks }
            .ifEmpty { null }
            ?.let {
                it[it.size / 2]
            }

    internal fun tryAndGetMedianAudioOnlyStream(availableStreams: List<Stream>) =
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

    internal fun getNonDynamicVideoStreams(availableStreams: List<Stream>) =
        availableStreams.filter {
            !it.isDashOrHls && it.hasVideoTracks && !it.hasAudioTracks
        }

    internal fun getNonDynamicAudioStreams(availableStreams: List<Stream>) =
        availableStreams.filter { !it.isDashOrHls && !it.hasVideoTracks && it.hasAudioTracks }

    internal fun hasVideoStreams(availableStreams: List<Stream>): Boolean {
        availableStreams.forEach {
            if (it.hasVideoTracks)
                return true
        }
        return false
    }
}
