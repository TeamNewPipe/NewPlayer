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

internal class TrackSelector(
    val preferredLanguages: List<LanguageIdentifier>,
) {
    internal fun selectStreamAutomatically(
        item: String,
        availableStreams: List<Stream>,
    ): StreamSelection {


        // filter for best fitting language stream variants

        val bestFittingLanguage = getBestLanguageFit(availableStreams, preferredLanguages)
        val availablesInPreferredLanguage = if (bestFittingLanguage != null) filtersByLanguage(
            availableStreams, bestFittingLanguage
        )
        else {
            emptyList()
        }


        // is it a video stream or a pure audio stream?
        if (hasVideoStreams(availableStreams)) {

            // first: try and get a dynamic stream variant
            val dynamicStreams = getDynamicStreams(availablesInPreferredLanguage)
            if (dynamicStreams.isNotEmpty()) {
                return SingleSelection(dynamicStreams[0])
            }

            // second: try and get separate audio and video stream variants

            val videoOnlyStream = tryAndGetMedianVideoOnlyStream(availableStreams)


            if (videoOnlyStream != null) {

                val audioStream = tryAndGetMedianAudioOnlyStream(availableStreams)

                if (videoOnlyStream != null && audioStream != null) {
                    return MultiSelection(listOf(videoOnlyStream, audioStream))
                }
            } /* if (vdeioOnlyStream != null) */

            // fourth: try to get a video and audio stream variant with the best fitting identifier

            tryAndGetMedianCombinedVideoAndAudioStream(availableStreams)?.let {
                return SingleSelection(it)
            }

        } else { /* if(!hasVideoStreams(availableStreams)) */

            // first: try to get an audio stream variant with the best fitting identifier

            tryAndGetMedianAudioOnlyStream(availableStreams)?.let {
                return SingleSelection(it)
            }
        }

        throw NewPlayerException("StreamSelector: No suitable Stream found that.")
    }

    companion object {

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
            tracks.filterIsInstance<AudioStreamTrack>().map { it.language }.filterNotNull()

        private fun getBestLanguageFit(
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

        private fun filtersByLanguage(
            availableStreams: List<Stream>, language: LanguageIdentifier
        ) = availableStreams.filter {
            it.languages.contains(
                language
            )
        }

        private fun tryAndGetMedianVideoOnlyStream(availableStreams: List<Stream>) =
            availableStreams.filter { !it.isDashOrHls && it.hasVideoTracks && !it.hasAudioTracks }
                .ifEmpty { null }?.let {
                    it[it.size / 2]
                }

        private fun tryAndGetMedianCombinedVideoAndAudioStream(availableStreams: List<Stream>) =
            availableStreams.filter { !it.isDashOrHls && it.hasVideoTracks && it.hasVideoTracks }
                .ifEmpty { null }
                ?.let {
                    it[it.size / 2]
                }

        private fun tryAndGetMedianAudioOnlyStream(availableStreams: List<Stream>) =
            availableStreams.filter { !it.isDashOrHls && it.hasAudioTracks && !it.hasVideoTracks }
                .ifEmpty { null }?.let {
                    it[it.size / 2]
                }


        private fun getDemuxedStreams(
            availableStreams: List<Stream>
        ) = availableStreams.filter {
            !it.isDashOrHls && it.streamTracks.size == 1
        }


        private fun getDynamicStreams(availableStreams: List<Stream>) =
            availableStreams.filter { it.isDashOrHls }

        private fun getNonDynamicVideoStreams(availableStreams: List<Stream>) =
            availableStreams.filter {
                !it.isDashOrHls && it.hasVideoTracks && !it.hasAudioTracks
            }

        private fun getNonDynamicAudioStreams(availableStreams: List<Stream>) =
            availableStreams.filter { !it.isDashOrHls && !it.hasVideoTracks && it.hasAudioTracks }

        private fun hasVideoStreams(availableStreams: List<Stream>): Boolean {
            availableStreams.forEach {
                if (it.hasVideoTracks)
                    return true
            }
            return false
        }

    }
}