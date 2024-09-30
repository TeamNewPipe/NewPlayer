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

import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.Stream

class StreamSelector(
    val preferredVideoIdentifier: List<String>,
    val preferredAudioIdentifier: List<String>,
    val preferredLanguage: List<String>,
) {

    interface StreamSelection {
        val item: String
    }

    data class SingleSelection(
        override val item: String,
        val stream: Stream
    ) : StreamSelection

    data class MultiSelection(
        override val item: String,
        val streams: List<Stream>
    ) : StreamSelection

    enum class DemuxedStreamBundeling {
        DO_NOT_BUNDLE, BUNDLE_STREAMS_WITH_SAME_ID, BUNDLE_AUDIOSTREAMS_WITH_SAME_ID
    }

    fun selectStream(
        item: String,
        availableStreams: List<Stream>,
        demuxedStreamBundeling: DemuxedStreamBundeling = DemuxedStreamBundeling.DO_NOT_BUNDLE
    ): StreamSelection {

        // filter for best fitting language stream variants

        val bestFittingLanguage = getBestLanguageFit(availableStreams, preferredLanguage)
        val availablesInPreferredLanguage = if (bestFittingLanguage != null) filtersByLanguage(
            availableStreams, bestFittingLanguage
        )
        else {
            emptyList()
        }


        // is it a video stream or a pure audio stream?
        if (hasVideoStreams(availableStreams)) {

            // first: try and get a dynamic stream variant
            getDynamicStream(availablesInPreferredLanguage) ?: getDynamicStream(
                availableStreams
            )?.let {
                return SingleSelection(item, it)
            }

            // second: try and get separate audio and video stream variants

            val bestVideoIdentifier = getBestFittingVideoIdentifier(
                availablesInPreferredLanguage, preferredVideoIdentifier
            ) ?: tryAndGetMedianVideoOnlyStream(availablesInPreferredLanguage)?.identifier
            ?: getBestFittingVideoIdentifier(
                availableStreams, preferredVideoIdentifier
            ) ?: tryAndGetMedianVideoOnlyStream(availableStreams)?.identifier ?: ""

            if (demuxedStreamBundeling == DemuxedStreamBundeling.BUNDLE_STREAMS_WITH_SAME_ID) {
                return MultiSelection(item, availableStreams.filter { it.streamType != StreamType.DYNAMIC })
            } else {

                val videoOnlyStream = getVideoOnlyWithMatchingIdentifier(
                    availablesInPreferredLanguage, bestVideoIdentifier
                ) ?: getVideoOnlyWithMatchingIdentifier(
                    availableStreams, bestVideoIdentifier
                )

                if (videoOnlyStream != null) {
                    if (demuxedStreamBundeling == DemuxedStreamBundeling.BUNDLE_AUDIOSTREAMS_WITH_SAME_ID) {
                        val bestFittingAudioStreams = getBestFittingAudioStreams(availableStreams, preferredAudioIdentifier)
                            ?: listOf(availableStreams.filter { it.streamType == StreamType.AUDIO }[0])

                        val streams = mutableListOf(videoOnlyStream)
                        streams.addAll(bestFittingAudioStreams)
                        return MultiSelection(item, streams)
                    }
                    val audioStream = getBestFittingAudio(
                        availablesInPreferredLanguage, preferredAudioIdentifier
                    ) ?: getBestFittingAudio(availableStreams, preferredAudioIdentifier)
                    ?: availableStreams.filter { it.streamType == StreamType.AUDIO }[0]

                    return MultiSelection(item, listOf(videoOnlyStream, audioStream))
                } /* if (vdeioOnlyStream != null) */
            } /* else (demuxedStreamBundeling == DemuxedStreamBundeling.BUNDLE_STREAMS_WITH_SAME_ID) */

            // fourth: try to get a video and audio stream variant with the best fitting identifier

            getFirstMatchingIdentifier(
                availablesInPreferredLanguage, bestVideoIdentifier
            ) ?: getFirstMatchingIdentifier(
                availableStreams, bestVideoIdentifier
            )?.let {
                return SingleSelection(item, it)
            }

            // fifth: try and get the median video and audio stream variant

            return SingleSelection(item, run {
                val videos = getNonDynamicVideos(availablesInPreferredLanguage).ifEmpty {
                    getNonDynamicVideos(availableStreams)
                }

                if (videos.isNotEmpty()) {
                    return@run videos[videos.size / 2]
                } else {
                    throw NewPlayerException("No fitting video stream could be found for stream item item: ${item}")
                }
            })

        } else { /* if(hasVideoStreams(availableStreams)) */

            // first: try to get an audio stream variant with the best fitting identifier

            getBestFittingAudio(
                availablesInPreferredLanguage, preferredAudioIdentifier
            ) ?: getBestFittingAudio(
                availableStreams, preferredAudioIdentifier
            )?.let {
                return SingleSelection(item, it)
            }

            // second: try and get the median audio stream variant

            return SingleSelection(item, run {
                val audios = getNonDynamicAudios(availablesInPreferredLanguage).let {
                    if (it.isNotEmpty()) {
                        it
                    } else {
                        getNonDynamicAudios(availableStreams)
                    }
                }
                if (audios.isNotEmpty()) {
                    return@run audios[audios.size / 2]
                } else {
                    throw NewPlayerException("No fitting audio stream could be found for stream item item: ${item}")
                }
            })
        }
    }

   companion object {

       private fun getBestLanguageFit(
           availableStreams: List<Stream>, preferredLanguages: List<String>
       ): String? {
           for (preferredLanguage in preferredLanguages) {
               for (available in availableStreams) {
                   if (available.language == preferredLanguage) {
                       return preferredLanguage
                   }
               }
           }
           return null
       }

       private fun filtersByLanguage(
           availableStreams: List<Stream>, language: String
       ) = availableStreams.filter { it.language == language }

       private fun getBestFittingVideoIdentifier(
           availableStreams: List<Stream>, preferredVideoIdentifier: List<String>
       ): String? {
           for (preferredStream in preferredVideoIdentifier) {
               for (available in availableStreams) {
                   if ((available.streamType == StreamType.AUDIO_AND_VIDEO || available.streamType == StreamType.VIDEO) && preferredStream == available.identifier) {
                       return preferredStream
                   }
               }
           }
           return null
       }

       private fun tryAndGetMedianVideoOnlyStream(availableStreams: List<Stream>) =
           availableStreams.filter { it.streamType == StreamType.VIDEO }.ifEmpty { null }?.let {
               it[it.size / 2]
           }

       private fun tryAndGetMedianAudioOnlyStream(availableStreams: List<Stream>) =
           availableStreams.filter { it.streamType == StreamType.AUDIO }.ifEmpty { null }?.let {
               it[it.size / 2]
           }

       private fun getFirstMatchingIdentifier(
           availableStreams: List<Stream>, identifier: String
       ): Stream? {
           for (variant in availableStreams) {
               if (variant.identifier == identifier) return variant
           }
           return null
       }

       private fun getBestFittingAudio(
           availableStreams: List<Stream>, preferredAudioIdentifier: List<String>
       ): Stream? {
           for (preferredStream in preferredAudioIdentifier) {
               for (availableStream in availableStreams) {
                   if (availableStream.streamType == StreamType.AUDIO && preferredStream == availableStream.identifier) {
                       return availableStream
                   }
               }
           }
           return null
       }

       private fun getBestFittingAudioStreams(
           availableStreams: List<Stream>, preferredAudioIdentifier: List<String>
       ): List<Stream>? {
           val bundles =
               bundledStreamsWithSameIdentifier(availableStreams.filter { it.streamType == StreamType.AUDIO })
           for (preferred in preferredAudioIdentifier) {
               val streams = bundles[preferred]
               if (streams != null) {
                   return streams
               }
           }
           return null
       }

       private fun getDemuxedStreams(
           availableStreams: List<Stream>
       ) = availableStreams.filter {
           it.streamType == StreamType.AUDIO || it.streamType == StreamType.VIDEO
       }

       /**
        * This is needed to bundle streams with the same quality but different languages
        */
       private fun bundledStreamsWithSameIdentifier(
           availableStreams: List<Stream>,
       ): HashMap<String, ArrayList<Stream>> {
           val streamsBundledByIdentifier = HashMap<String, ArrayList<Stream>>()
           for (stream in availableStreams) {
               streamsBundledByIdentifier[stream.identifier] ?: run {
                   val array = ArrayList<Stream>()
                   streamsBundledByIdentifier[stream.identifier] = array
                   array
               }.add(stream)
           }
           return streamsBundledByIdentifier
       }

       private fun getVideoOnlyWithMatchingIdentifier(
           availableStreams: List<Stream>, identifier: String
       ): Stream? {
           for (variant in availableStreams) {
               if (variant.streamType == StreamType.VIDEO && variant.identifier == identifier) return variant
           }
           return null
       }

       private fun getDynamicStream(availableStreams: List<Stream>): Stream? {
           for (variant in availableStreams) {
               if (variant.streamType == StreamType.DYNAMIC) {
                   return variant
               }
           }
           return null
       }

       private fun getNonDynamicVideos(availableStreams: List<Stream>) = availableStreams.filter {
           it.streamType == StreamType.VIDEO || it.streamType == StreamType.AUDIO_AND_VIDEO
       }

       private fun getNonDynamicAudios(availableStreams: List<Stream>) =
           availableStreams.filter { it.streamType == StreamType.AUDIO }

       private fun hasVideoStreams(availableStreams: List<Stream>): Boolean {
           for (variant in availableStreams) {
               if (variant.streamType == StreamType.AUDIO_AND_VIDEO || variant.streamType == StreamType.VIDEO || variant.streamType == StreamType.DYNAMIC) return true
           }
           return false
       }

   }
}