package net.newpipe.newplayer.testapp

import androidx.media3.common.MediaItem
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.logic.NoResponse
import net.newpipe.newplayer.data.SingleSelection
import net.newpipe.newplayer.logic.StreamExceptionResponse
import net.newpipe.newplayer.logic.StreamSelectionResponse
import java.lang.Exception

suspend fun streamErrorHandler(
    item: String?,
    mediaItem: MediaItem?,
    exception: Exception,
    repository: MediaRepository
): StreamExceptionResponse {
    return if (item == "faulty") {
        StreamSelectionResponse(SingleSelection(repository.getStreams("6502")[0]))
    } else {
        NoResponse()
    }
}