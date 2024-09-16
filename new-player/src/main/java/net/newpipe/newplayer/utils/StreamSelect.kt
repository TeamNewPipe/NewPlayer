package net.newpipe.newplayer.utils


import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.PlayMode
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.Stream

object StreamSelect {

    interface StreamSelection

    data class SingleSelection(
        val stream: Stream
    ) : StreamSelection

    data class MultiSelection(
        val videoStream: Stream,
        val audioStream: Stream
    ) : StreamSelection


    private fun getBestLanguageFit(
        availableStreams: List<Stream>,
        preferredLanguages: List<String>
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
        availableStreams: List<Stream>,
        language: String
    ) =
        availableStreams.filter { it.language == language }

    private fun getBestFittingVideoIdentifier(
        availableStreams: List<Stream>,
        preferredVideoIdentifier: List<String>
    ): String? {
        for (preferredStream in preferredVideoIdentifier) {
            for (available in availableStreams) {
                if ((available.streamType == StreamType.AUDIO_AND_VIDEO ||
                            available.streamType == StreamType.VIDEO)
                    && preferredStream == available.identifier
                ) {
                    return preferredStream
                }
            }
        }
        return null
    }

    private fun getFirstMatchingIdentifier(
        availableStreams: List<Stream>,
        identifier: String
    ): Stream? {
        for (variant in availableStreams) {
            if (variant.identifier == identifier)
                return variant
        }
        return null
    }

    private fun getBestFittingAudio(
        availableStreams: List<Stream>,
        preferredAudioIdentifier: List<String>
    ): Stream? {
        for (preferredStream in preferredAudioIdentifier) {
            for (availableStream in availableStreams) {
                if (availableStream.streamType == StreamType.AUDIO
                    && preferredStream == availableStream.identifier
                ) {
                    return availableStream
                }
            }
        }
        return null
    }

    private fun getVideoOnlyWithMatchingIdentifier(
        availableStreams: List<Stream>,
        identifier: String
    ): Stream? {
        for (variant in availableStreams) {
            if (variant.streamType == StreamType.VIDEO
                && variant.identifier == identifier
            )
                return variant
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

    private fun getNonDynamicVideos(availableStreams: List<Stream>) =
        availableStreams.filter {
            it.streamType == StreamType.VIDEO || it.streamType == StreamType.AUDIO_AND_VIDEO
        }

    private fun getNonDynamicAudios(availableStreams: List<Stream>) =
        availableStreams.filter { it.streamType == StreamType.AUDIO }

    private fun hasVideoStreams(availableStreams: List<Stream>): Boolean {
        for (variant in availableStreams) {
            if (variant.streamType == StreamType.AUDIO_AND_VIDEO || variant.streamType == StreamType.VIDEO || variant.streamType == StreamType.DYNAMIC)
                return true
        }
        return false
    }

    fun selectStream(
        item: String,
        playMode: PlayMode,
        availableStreams: List<Stream>,
        preferredVideoIdentifier: List<String>,
        preferredAudioIdentifier: List<String>,
        preferredLanguage: List<String>
    ): StreamSelection {

        // filter for best fitting language stream variants

        val bestFittingLanguage = getBestLanguageFit(availableStreams, preferredLanguage)
        val availablesInPreferredLanguage =
            if (bestFittingLanguage != null) filtersByLanguage(
                availableStreams,
                bestFittingLanguage
            )
            else {
                emptyList()
            }


        // is it a video stream or a pure audio stream?
        if (hasVideoStreams(availableStreams)) {

            // first: try and get a dynamic stream variant
            getDynamicStream(availablesInPreferredLanguage)
                ?: getDynamicStream(
                    availableStreams
                )?.let {
                    return SingleSelection(it)
                }

            // second: try and get seperate audio and video stream variants

            val bestVideoIdentifier =
                getBestFittingVideoIdentifier(
                    availablesInPreferredLanguage,
                    preferredVideoIdentifier
                )?.let {
                    val videos =
                        getNonDynamicVideos(availablesInPreferredLanguage)
                    videos[videos.size / 2].identifier
                } ?: getBestFittingVideoIdentifier(
                    availableStreams,
                    preferredVideoIdentifier
                )
                ?: run {
                    val videos = getNonDynamicVideos(availableStreams)
                    videos[videos.size / 2].identifier
                }

            val videoOnlyStream =
                getVideoOnlyWithMatchingIdentifier(
                    availablesInPreferredLanguage,
                    bestVideoIdentifier
                ) ?: getVideoOnlyWithMatchingIdentifier(
                    availableStreams,
                    bestVideoIdentifier
                )

            if (videoOnlyStream != null) {
                getBestFittingAudio(
                    availablesInPreferredLanguage,
                    preferredAudioIdentifier
                ) ?: getBestFittingAudio(availableStreams, preferredAudioIdentifier)
                    ?.let {
                        return MultiSelection(videoOnlyStream, it)
                    }
            }

            // fourth: try to get a video and audio stream variant with the best fitting identifier

            getFirstMatchingIdentifier(
                availablesInPreferredLanguage,
                bestVideoIdentifier
            )
                ?: getFirstMatchingIdentifier(
                    availableStreams,
                    bestVideoIdentifier
                )?.let {
                    return SingleSelection(it)
                }

            // fifth: try and get the median video and audio stream variant

            return SingleSelection(run {
                val videos =
                    getNonDynamicVideos(availablesInPreferredLanguage).ifEmpty {
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
                availablesInPreferredLanguage,
                preferredAudioIdentifier
            )
                ?: getBestFittingAudio(
                    availableStreams,
                    preferredAudioIdentifier
                )?.let {
                    return SingleSelection(it)
                }

            // second: try and get the median audio stream variant

            return SingleSelection(run {
                val audios =
                    getNonDynamicAudios(availablesInPreferredLanguage).let {
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
}