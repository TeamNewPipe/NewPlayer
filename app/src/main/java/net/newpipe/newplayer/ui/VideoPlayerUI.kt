/* NewPlayer
 *
 * @author Christian Schabesberger
 *
 * Copyright (C) NewPipe e.V. 2024 <code(at)newpipe-ev.de>
 *
 * NewPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.newpipe.newplayer.ui

import android.content.Intent
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import net.newpipe.newplayer.VideoPlayerActivity
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelImpl
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.utils.findActivity

@Composable
fun VideoPlayerUI(
    viewModel: VideoPlayerViewModel,
    isFullscreen: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var fullscreen_requested by remember {
        mutableStateOf(false)
    }

    if(isFullscreen != uiState.fullscreen && !fullscreen_requested) {
        fullscreen_requested = true
        val current_acitivity = LocalContext.current.findActivity()
        if(uiState.fullscreen) {
            val fullscreen_acitivity_intent = Intent(current_acitivity, VideoPlayerActivity::class.java)
            current_acitivity!!.startActivity(fullscreen_acitivity_intent)
        } else {
            current_acitivity!!.finish()
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                SurfaceView(context).also {
                    //viewModel.player?.setVideoSurfaceView(it)
                }
            }, update = {
                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        println("gurken state on pause")
                        viewModel.pause()
                    }

                    else -> Unit
                }
            })

        val isPlaying = viewModel.player!!.isPlaying
        println("is Player playing: $isPlaying")
        VideoPlayerControllerUI(
            isPlaying = uiState.playing,
            fullscreen = uiState.fullscreen,
            play = viewModel::play,
            pause = viewModel::pause,
            prevStream = viewModel::prevStream,
            nextStream = viewModel::nextStream,
            switchToFullscreen = viewModel::switchToFullscreen,
            switchToEmbeddedView = viewModel::switchToEmbeddedView
        )
    }
}

@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
fun PlayerUIPreviewEmbeded() {
    VideoPlayerTheme {
        VideoPlayerUI(viewModel = VideoPlayerViewModelImpl.dummy, isFullscreen = false)
    }
}