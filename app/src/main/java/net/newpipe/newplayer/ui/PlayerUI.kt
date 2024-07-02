package net.newpipe.newplayer.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun PlayerUI() {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box() {
            CenterUI(modifier = Modifier.align(Alignment.Center))
            BottomUI(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp)
                    .defaultMinSize(minHeight = 40.dp)
                    .fillMaxWidth()
            )
        }
        Text("hello gurken")
    }
}

@Composable
private fun CenterUI(modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        IconButton(
            onClick = { /*TODO*/ }, modifier = Modifier
                .size(40.dp)
                .padding(end = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Previous Stream"
            )
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(60.dp)) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Pause"
            )
        }
        IconButton(
            onClick = { /*TODO*/ }, modifier = Modifier
                .size(40.dp)
                .padding(start = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Next Stream"
            )
        }
    }
}

@Composable
private fun BottomUI(modifier: Modifier) {
    var isFullscreen: Boolean by rememberSaveable { mutableStateOf(false) }

    if (isFullscreen) {
        LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text("06:45")
        Slider(value = 0.4F, onValueChange = {}, modifier = Modifier.weight(1F))
        Text("09:40")
        IconButton(onClick = { isFullscreen = !isFullscreen }) {
            Icon(
                imageVector = Icons.Filled.Fullscreen,
                contentDescription = "Fullscreen"
            )
        }
    }
}

@Composable
private fun ViewInFullScreen() {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}

@Preview
@Composable
fun PlayerUIPreview() {
    VideoPlayerTheme {
        PlayerUI()
    }
}