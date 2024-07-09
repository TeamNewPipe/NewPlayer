package net.newpipe.newplayer.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import net.newpipe.newplayer.R
import javax.inject.Inject


interface VideoPlayerViewModel {
    val player: Player?
}

@HiltViewModel
class VideoPlayerViewModelImpl @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    override val player: Player,
    application: Application
) : AndroidViewModel(application), VideoPlayerViewModel {

    val app = getApplication<Application>()

    init {
        player.prepare()
        player.setMediaItem(MediaItem.fromUri(app.getString(R.string.ccc_6502_video)))
    }

    override fun onCleared() {
        player.release()
    }

    companion object {
        val dummy = object : VideoPlayerViewModel {
            override val player = null
        }
    }
}