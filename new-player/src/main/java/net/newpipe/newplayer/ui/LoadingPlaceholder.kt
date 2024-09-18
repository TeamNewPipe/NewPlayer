package net.newpipe.newplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun VideoPlayerLoadingPlaceholder(aspectRatio: Float = 3F / 1F) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerLoaidingPlaceholderPreview() {
    VideoPlayerTheme {
        VideoPlayerLoadingPlaceholder()
    }
}