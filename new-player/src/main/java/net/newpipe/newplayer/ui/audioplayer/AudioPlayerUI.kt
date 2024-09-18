package net.newpipe.newplayer.ui.audioplayer

import androidx.annotation.OptIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@OptIn(UnstableApi::class)
@Composable
fun AudioPlayerUI(viewModel:NewPlayerViewModel, uiState: NewPlayerUIState) {

}

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
fun AudioPlayerUIPreviewEmbedded() {
    VideoPlayerTheme {
        AudioPlayerUI(viewModel = NewPlayerViewModelDummy(), uiState = NewPlayerUIState.DUMMY)
    }
}