package net.newpipe.newplayer.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.newpipe.newplayer.ui.theme.AppTheme
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun PlayerUI(activity: Activity) {
    var isFullscreen = rememberSaveable { mutableStateOf(false) }

    if (isFullscreen.value)  {
        LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }
    
    VideoPlayerTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Text(
                "hello gurken",
                color = Color.White
            )
            Button(onClick = { isFullscreen.value = !isFullscreen.value }) {
                Text("Switch to fullscreen")
            }
        }

    }
}

@Composable
private fun ViewInFullScreen() {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}
