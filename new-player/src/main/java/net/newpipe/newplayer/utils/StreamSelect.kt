package net.newpipe.newplayer.utils


import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.PlayMode
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.StreamVariant

object StreamSelect {

    interface StreamSelection

    data class SingleSelection(
        val streamVariant: StreamVariant
    ) : StreamSelection

    data class MultiSelection(
        val videoStream: StreamVariant,
        val audioStream: StreamVariant
    ) : StreamSelection


    private fun getBestLanguageFit(
        availableStreamVariants: List<StreamVariant>,
        preferredLanguages: List<String>
    ): String? {
        for (preferredLanguage in preferredLanguages) {
            for (availableVariant in availableStreamVariants) {
                if (availableVariant.language == preferredLanguage) {
                    return preferredLanguage
                }
            }
        }
        return null
    }

    private fun filterVariantsByLanguage(
        availableStreamVariants: List<StreamVariant>,
        language: String
    ) =
        availableStreamVariants.filter { it.language == language }

    private fun getBestFittingVideoIdentifier(
        availableStreamVariants: List<StreamVariant>,
        preferredVideoIdentifier: List<String>
    ): String? {
        for (preferredStream in preferredVideoIdentifier) {
            for (availableVariant in availableStreamVariants) {
                if ((availableVariant.streamType == StreamType.AUDIO_AND_VIDEO ||
                            availableVariant.streamType == StreamType.VIDEO)
                    && preferredStream == availableVariant.streamVariantIdentifier
                ) {
                    return preferredStream
                }
            }
        }
        return null
    }

    private fun getFirstVariantMatchingIdentifier(
        availableStreamVariants: List<StreamVariant>,
        identifier: String
    ): StreamVariant? {
        for (variant in availableStreamVariants) {
            if (variant.streamVariantIdentifier == identifier)
                return variant
        }
        return null
    }

    private fun getBestFittingAudioVariant(
        availableStreamVariants: List<StreamVariant>,
        preferredAudioIdentifier: List<String>
    ): StreamVariant? {
        for (preferredStream in preferredAudioIdentifier) {
            for (availableStream in availableStreamVariants) {
                if (availableStream.streamType == StreamType.AUDIO
                    && preferredStream == availableStream.streamVariantIdentifier
                ) {
                    return availableStream
                }
            }
        }
        return null
    }

    private fun getVideoOnlyVariantWithMatchingIdentifier(
        availableStreamVariants: List<StreamVariant>,
        identifier: String
    ): StreamVariant? {
        for (variant in availableStreamVariants) {
            if (variant.streamType == StreamType.VIDEO
                && variant.streamVariantIdentifier == identifier
            )
                return variant
        }
        return null
    }

    private fun getDynamicStream(availableStreamVariants: List<StreamVariant>): StreamVariant? {
        for (variant in availableStreamVariants) {
            if (variant.streamType == StreamType.DYNAMIC) {
                return variant
            }
        }
        return null
    }

    private fun getNonDynamicVideoVariants(availableStreamVariants: List<StreamVariant>) =
        availableStreamVariants.filter {
            it.streamType == StreamType.VIDEO || it.streamType == StreamType.AUDIO_AND_VIDEO
        }

    private fun getNonDynamicAudioVariants(availableStreamVariants: List<StreamVariant>) =
        availableStreamVariants.filter { it.streamType == StreamType.AUDIO }

    private fun hasVideoStreamVariants(availableStreamVariants: List<StreamVariant>): Boolean {
        for (variant in availableStreamVariants) {
            if (variant.streamType == StreamType.AUDIO_AND_VIDEO || variant.streamType == StreamType.VIDEO || variant.streamType == StreamType.DYNAMIC)
                return true
        }
        return false
    }

    fun selectStream(
        item: String,
        playMode: PlayMode,
        availableStreamVariants: List<StreamVariant>,
        preferredVideoIdentifier: List<String>,
        preferredAudioIdentifier: List<String>,
        preferredLanguage: List<String>
    ): StreamSelection {

        // filter for best fitting language stream variants

        val bestFittingLanguage = getBestLanguageFit(availableStreamVariants, preferredLanguage)
        val availableVariantsInPreferredLanguage =
            if (bestFittingLanguage != null) filterVariantsByLanguage(
                availableStreamVariants,
                bestFittingLanguage
            )
            else {
                emptyList()
            }


        // is it a video stream or a pure audio stream?
        if (hasVideoStreamVariants(availableStreamVariants)) {

            // first: try and get a dynamic stream variant
            getDynamicStream(availableVariantsInPreferredLanguage)
                ?: getDynamicStream(
                    availableStreamVariants
                )?.let {
                    return SingleSelection(it)
                }

            // second: try and get seperate audio and video stream variants

            val bestVideoIdentifier =
                getBestFittingVideoIdentifier(
                    availableVariantsInPreferredLanguage,
                    preferredVideoIdentifier
                )?.let {
                    val videoVariants =
                        getNonDynamicVideoVariants(availableVariantsInPreferredLanguage)
                    videoVariants[videoVariants.size / 2].streamVariantIdentifier
                } ?: getBestFittingVideoIdentifier(
                    availableStreamVariants,
                    preferredVideoIdentifier
                )
                ?: run {
                    val videoVariants = getNonDynamicVideoVariants(availableStreamVariants)
                    videoVariants[videoVariants.size / 2].streamVariantIdentifier
                }

            val videoOnlyStream =
                getVideoOnlyVariantWithMatchingIdentifier(
                    availableVariantsInPreferredLanguage,
                    bestVideoIdentifier
                ) ?: getVideoOnlyVariantWithMatchingIdentifier(
                    availableStreamVariants,
                    bestVideoIdentifier
                )

            if (videoOnlyStream != null) {
                getBestFittingAudioVariant(
                    availableVariantsInPreferredLanguage,
                    preferredAudioIdentifier
                ) ?: getBestFittingAudioVariant(availableStreamVariants, preferredAudioIdentifier)
                    ?.let {
                        return MultiSelection(videoOnlyStream, it)
                    }
            }

            // fourth: try to get a video and audio stream variant with the best fitting identifier

            getFirstVariantMatchingIdentifier(
                availableVariantsInPreferredLanguage,
                bestVideoIdentifier
            )
                ?: getFirstVariantMatchingIdentifier(
                    availableStreamVariants,
                    bestVideoIdentifier
                )?.let {
                    return SingleSelection(it)
                }

            // fifth: try and get the median video and audio stream variant

            return SingleSelection(run {
                val videoVariants =
                    getNonDynamicVideoVariants(availableVariantsInPreferredLanguage).ifEmpty {
                        getNonDynamicVideoVariants(availableStreamVariants)
                    }

                if (videoVariants.isNotEmpty()) {
                    return@run videoVariants[videoVariants.size / 2]
                } else {
                    throw NewPlayerException("No fitting video stream could be found for stream item item: ${item}")
                }
            })

        } else { /* if(hasVideoStreamVariants(availableStreamVariants)) */

            // first: try to get an audio stream variant with the best fitting identifier

            getBestFittingAudioVariant(
                availableVariantsInPreferredLanguage,
                preferredAudioIdentifier
            )
                ?: getBestFittingAudioVariant(
                    availableStreamVariants,
                    preferredAudioIdentifier
                )?.let {
                    return SingleSelection(it)
                }

            // second: try and get the median audio stream variant

            return SingleSelection(run {
                val audioVariants =
                    getNonDynamicAudioVariants(availableVariantsInPreferredLanguage).let {
                        if (it.isNotEmpty()) {
                            it
                        } else {
                            getNonDynamicAudioVariants(availableStreamVariants)
                        }
                    }
                if (audioVariants.isNotEmpty()) {
                    return@run audioVariants[audioVariants.size / 2]
                } else {
                    throw NewPlayerException("No fitting audio stream could be found for stream item item: ${item}")
                }
            })
        }
    }
}