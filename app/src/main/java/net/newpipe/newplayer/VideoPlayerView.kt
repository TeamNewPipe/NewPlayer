package net.newpipe.newplayer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.internal.model.VideoPlayerViewModel
import net.newpipe.newplayer.internal.model.VideoPlayerViewModelImpl

@AndroidEntryPoint
class VideoPlayerView : FrameLayout {
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        val view = LayoutInflater.from(context).inflate(R.layout.video_player_view, this)
        val composeView = view.findViewById<ComposeView>(R.id.player_copose_view)
    }
}