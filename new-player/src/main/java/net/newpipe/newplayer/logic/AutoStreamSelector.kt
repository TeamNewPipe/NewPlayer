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

import net.newpipe.newplayer.data.MultiSelection
import net.newpipe.newplayer.data.NewPlayerException
import net.newpipe.newplayer.data.SingleSelection
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamSelection
import net.newpipe.newplayer.logic.TrackUtils.getDynamicStreams
import net.newpipe.newplayer.logic.TrackUtils.hasVideoTracks
import net.newpipe.newplayer.logic.TrackUtils.tryAndGetMedianAudioOnlyTracks
import net.newpipe.newplayer.logic.TrackUtils.tryAndGetMedianCombinedVideoAndAudioTracks
import net.newpipe.newplayer.logic.TrackUtils.tryAndGetMedianVideoOnlyTracks

/**
 * TODO: This whole class is just a concept. this should really be finished at some point.
 *
 * @hide
 */
/** @hide */
internal class AutoStreamSelector(
    /**
     * Must be in IETF-BCP-47 format
     * This is a soft constraint. If no language can be found in the available tracks
     * that fits on of the preferred languages, a default language is picked.
     */
    val preferredLanguages: List<String>,

    /**
     * This is a hard constraint. If not null a stream with this language must be selected,
     * otherwise the automatic stream selection fails.
     * Setting this will override the [preferredLanguages].
     */
    val streamLanguageConstraint: String?
) {

    
/** @hide */
internal fun selectStreamAutomatically(
        availableStreams: List<Stream>,
    ): StreamSelection {


        // filter for best fitting language stream variants

        val bestFittingLanguage =
            TrackUtils.getBestLanguageFit(availableStreams, preferredLanguages)

        val availableInPreferredLanguage =
            if (bestFittingLanguage != null) TrackUtils.filtersByLanguage(
                availableStreams, bestFittingLanguage
            )
            else {
                emptyList()
            }


        // is it a video stream or a pure audio stream?
        if (hasVideoTracks(availableStreams)) {

            // first: try and get a dynamic stream variant
            val dynamicStreams = getDynamicStreams(availableInPreferredLanguage)
            if (dynamicStreams.isNotEmpty()) {
                return SingleSelection(dynamicStreams[0])
            }

            // second: try and get separate audio and video stream variants

            val videoOnlyStream = tryAndGetMedianVideoOnlyTracks(availableStreams)


            if (videoOnlyStream != null) {

                val audioStream = tryAndGetMedianAudioOnlyTracks(availableStreams)

                if (videoOnlyStream != null && audioStream != null) {
                    return MultiSelection(listOf(videoOnlyStream, audioStream))
                }
            } /* if (vdeioOnlyStream != null) */

            // fourth: try to get a video and audio stream variant with the best fitting identifier

            tryAndGetMedianCombinedVideoAndAudioTracks(availableStreams)?.let {
                return SingleSelection(it)
            }

        } else { /* if(!hasVideoStreams(availableStreams)) */

            // first: try to get an audio stream variant with the best fitting identifier

            tryAndGetMedianAudioOnlyTracks(availableStreams)?.let {
                return SingleSelection(it)
            }
        }

        throw NewPlayerException("StreamSelector: No suitable Stream found that.")
    }
}