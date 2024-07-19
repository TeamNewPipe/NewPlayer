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

package net.newpipe.newplayer.internal.ui

import android.app.Activity
import android.content.Intent
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.flow.collectLatest
import net.newpipe.newplayer.internal.VideoPlayerActivity
import net.newpipe.newplayer.internal.model.VIDEOPLAYER_UI_STATE
import net.newpipe.newplayer.internal.model.VideoPlayerViewModel
import net.newpipe.newplayer.internal.model.VideoPlayerViewModelImpl
import net.newpipe.newplayer.internal.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.internal.utils.findActivity

@Composable
fun VideoPlayerUI(
    viewModel: VideoPlayerViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val activity = LocalContext.current.findActivity()

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

    BackHandler {
        closeFullscreen(viewModel, activity!!)
    }

    val fullscreenLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            println("gurken returned for result")
            viewModel.initUIState(result.data?.extras!!)
        }

    LaunchedEffect(key1 = Unit) {
        viewModel.events?.collectLatest { event ->
            when (event) {
                VideoPlayerViewModel.Events.SwitchToEmbeddedView -> {
                    closeFullscreen(viewModel, activity!!)
                }

                VideoPlayerViewModel.Events.SwitchToFullscreen -> {
                    openFullscreen(viewModel, activity!!, fullscreenLauncher)
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                SurfaceView(context).also { view ->
                    println("gurken attach player: ${viewModel.player}")
                    viewModel.player?.setVideoSurfaceView(view)
                }
            }, update = { view ->
                when (lifecycle) {
                    Lifecycle.Event.ON_RESUME -> {
                        println("gurken reattach player: ${viewModel.player}")
                        viewModel.player?.setVideoSurfaceView(view)
                    }

                    else -> Unit
                }
            })

        val isPlaying = viewModel.player?.isPlaying ?: false

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

fun closeFullscreen(viewModel: VideoPlayerViewModel, activity: Activity) {
    val return_fullscreen_intent = Intent()
    var uiState = viewModel.uiState.value
    uiState.fullscreen = false
    return_fullscreen_intent.putExtra(VIDEOPLAYER_UI_STATE, uiState)
    activity.setResult(0, return_fullscreen_intent)
    activity.finish()
}

fun openFullscreen(
    viewModel: VideoPlayerViewModel,
    activity: Activity,
    fullscreenLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val fullscreen_activity_intent =
        Intent(activity!!.findActivity(), VideoPlayerActivity::class.java)
    var uiState = viewModel.uiState.value
    uiState.fullscreen = true
    fullscreen_activity_intent.putExtra(VIDEOPLAYER_UI_STATE, uiState)
    fullscreenLauncher.launch(fullscreen_activity_intent)
}

@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
fun PlayerUIPreviewEmbeded() {
    VideoPlayerTheme {
        VideoPlayerUI(viewModel = VideoPlayerViewModelImpl.dummy)
    }
}