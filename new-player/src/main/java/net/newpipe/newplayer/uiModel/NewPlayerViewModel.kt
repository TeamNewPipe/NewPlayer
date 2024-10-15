package net.newpipe.newplayer.uiModel

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.ui.ContentScale

@OptIn(UnstableApi::class)
interface NewPlayerViewModel {
    var newPlayer: NewPlayer?
    var contentFitMode: ContentScale

    val uiState: StateFlow<NewPlayerUIState>

    val embeddedPlayerDraggedDownBy: SharedFlow<Float>
    val onBackPressed: SharedFlow<Unit>

    fun onPictureInPictureModeChanged(isPictureInPictureMode: Boolean)
}