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

package net.newpipe.newplayer.ui.videoplayer

import android.app.Activity
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.ui.ContentScale
import net.newpipe.newplayer.uiModel.NewPlayerUIState

/** @hide */
@OptIn(UnstableApi::class)
@Composable
internal fun PlaySurface(
    modifier: Modifier,
    player: Player?,
    uiState: NewPlayerUIState
) {

    // Take care of content ratio

    // Preparation

    val activity = LocalContext.current as Activity

    val displayMetrics = activity.resources.displayMetrics

    val screenRatio =
        displayMetrics.widthPixels.toFloat() / displayMetrics.heightPixels.toFloat()

    val fitMode = uiState.contentFitMode
    val uiRatio = if (uiState.uiMode.fullscreen) screenRatio
    else uiState.embeddedUiRatio
    val contentRatio = uiState.contentRatio


    // actual calculation of the aspect ratio

    if (uiState.uiMode.fullscreen && fitMode == ContentScale.STRETCHED)
        ActualView(modifier.fillMaxSize(), player)
    else {
        val modifier = modifier.aspectRatio(contentRatio)
        if (fitMode == ContentScale.FIT_INSIDE) {
            ActualView(modifier.fillMaxSize(), player)
        } else { /* if(fitMode == ContentScale.CROP) */
            if (uiRatio <= contentRatio) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentWidth(unbounded = true, align = Alignment.CenterHorizontally)
                ) {
                    ActualView(modifier.fillMaxHeight(), player)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(unbounded = true, align = Alignment.CenterVertically)
                ) {
                    ActualView(modifier.fillMaxWidth(), player)
                }
            }
        }
    }

}

@Composable
private fun ActualView(modifier: Modifier, player: Player?) {
    // init lifecycle foo for android view

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    // Prepare stuff for the SurfaceView to which the video will be rendered
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }



    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceView(context).also { view ->
                player?.setVideoSurfaceView(view)
            }
        }, update = { view ->
            when (lifecycle) {
                Lifecycle.Event.ON_RESUME -> {
                    player?.setVideoSurfaceView(view)
                }

                else -> Unit
            }
        })
}

