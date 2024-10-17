package net.newpipe.newplayer.logic

import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.StreamSelection
import net.newpipe.newplayer.data.StreamTrack

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