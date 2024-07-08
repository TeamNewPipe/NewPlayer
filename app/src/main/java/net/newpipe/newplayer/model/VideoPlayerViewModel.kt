package net.newpipe.newplayer.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player
): ViewModel() {

    init {
        player.prepare()
    }

    override fun onCleared() {
        player.release()
    }
}