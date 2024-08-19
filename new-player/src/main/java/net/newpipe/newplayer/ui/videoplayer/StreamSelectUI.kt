package net.newpipe.newplayer.ui.videoplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import net.newpipe.newplayer.model.VideoPlayerUIState
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelDummy
import net.newpipe.newplayer.ui.CONTROLLER_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun StreamSelectUI(
    isChapterSelect: Boolean = false,
    viewModel: VideoPlayerViewModel,
    uiState: VideoPlayerUIState
) {
    Surface(modifier = Modifier.fillMaxSize(), color = CONTROLLER_UI_BACKGROUND_COLOR) {
        Scaffold(
            topBar = {

            }
        ) {

        }
    }
}


private fun ChapterSelectTopBar() {

}

@Preview(device = "id:pixel_5")
@Composable
fun VideoPlayerStreamSelectUIPreview() {
    VideoPlayerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Green) {
            StreamSelectUI(
                viewModel = VideoPlayerViewModelDummy(),
                uiState = VideoPlayerUIState.DEFAULT
            )
        }
    }
}