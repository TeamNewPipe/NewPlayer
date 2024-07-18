package net.newpipe.newplayer.internal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayerActivity : ComponentActivity() {

    private val viewModel: net.newpipe.newplayer.internal.model.VideoPlayerViewModel by viewModels<net.newpipe.newplayer.internal.model.VideoPlayerViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initUIState(intent.extras!!)

        enableEdgeToEdge()
        setContent {
            net.newpipe.newplayer.internal.ui.theme.VideoPlayerTheme {
                net.newpipe.newplayer.internal.ui.VideoPlayerUI(viewModel = viewModel)
            }
        }
    }
}