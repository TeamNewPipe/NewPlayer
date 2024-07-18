package net.newpipe.newplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.internal.model.VideoPlayerViewModel
import net.newpipe.newplayer.internal.model.VideoPlayerViewModelImpl
import net.newpipe.newplayer.internal.ui.VideoPlayerUI
import net.newpipe.newplayer.internal.ui.theme.VideoPlayerTheme

@AndroidEntryPoint
class VideoPlayerActivity : ComponentActivity() {

    private val viewModel: VideoPlayerViewModel by viewModels<VideoPlayerViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initUIState(intent.extras!!)

        enableEdgeToEdge()
        setContent {
            VideoPlayerTheme {
                VideoPlayerUI(viewModel = viewModel)
            }
        }
    }
}