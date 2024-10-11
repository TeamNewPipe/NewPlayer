package net.newpipe.newplayer.testapp

import androidx.media3.common.MediaItem
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.utils.NoResponse
import net.newpipe.newplayer.utils.SingleSelection
import net.newpipe.newplayer.utils.StreamExceptionResponse
import net.newpipe.newplayer.utils.StreamSelectionResponse
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