package net.newpipe.newplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.newpipe.newplayer.ui.ContentScale

@Parcelize
data class VideoPlayerUIState(
    val playing: Boolean,
    var fullscreen: Boolean,
    val uiVissible: Boolean,
    var uiVisible: Boolean,
    val contentRatio: Float,
    val embeddedUiRatio: Float,
    val contentFitMode: ContentScale,
    val seekerPosition: Float,
    val isLoading: Boolean,
    val durationInMs: Long,
    val playbackPositionInMs: Long
) : Parcelable {
    companion object {
        val DEFAULT = VideoPlayerUIState(
            playing = false,
            fullscreen = false,
            uiVissible = false,
            uiVisible = false,
            contentRatio = 16 / 9F,
            embeddedUiRatio = 16F / 9F,
            contentFitMode = ContentScale.FIT_INSIDE,
            seekerPosition = 0F,
            isLoading = true,
            durationInMs = 0,
            playbackPositionInMs = 0
        )
    }
}