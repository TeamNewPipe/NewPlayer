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

import net.newpipe.newplayer.data.AudioStreamTrack
import net.newpipe.newplayer.data.LanguageIdentifier
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamTrack

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
}
