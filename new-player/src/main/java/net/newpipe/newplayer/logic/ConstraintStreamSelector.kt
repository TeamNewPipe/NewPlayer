package net.newpipe.newplayer.logic

import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamSelection
import net.newpipe.newplayer.data.StreamTrack

/**
 * TODO
 * This selector should be used if the uses did pic a specific language and/or a specific
 * video resolution.
 */
internal object ConstraintStreamSelector {
    fun selectStream(
        availableStreams: List<Stream>,
        currentStreamSelection: StreamSelection,
        currentlyPlayingTracks: List<StreamTrack>,
        languageConstraint: String?,
        trackConstraint: StreamTrack?
    ): StreamSelection {

        val availableFilteredByLanguage = if (languageConstraint != null)
            TrackUtils.filtersByLanguage(availableStreams, languageConstraint)
        else
            availableStreams



        return currentStreamSelection
    }
}