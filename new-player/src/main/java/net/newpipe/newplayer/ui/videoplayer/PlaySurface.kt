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

import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import net.newpipe.newplayer.ui.ContentScale

@Composable
internal fun PlaySurface(
    modifier: Modifier,
    player: Player?,
    lifecycle: Lifecycle.Event,
    fitMode: ContentScale,
    uiRatio: Float,
    contentRatio: Float
) {

    var internalModifier = modifier.aspectRatio(contentRatio)

    if(uiRatio <= contentRatio) {
        internalModifier.fillMaxWidth()
    } else {
        internalModifier.fillMaxHeight()
    }

    /*
    when (fitMode) {
        ContentScale.FILL -> Modifier.fillMaxSize()
        ContentScale.FIT_INSIDE -> Modifier
            .aspectRatio(contentRatio)
            .then(
                if (contentRatio < uiRatio) Modifier
                    .fillMaxWidth() else Modifier.fillMaxHeight()
            )

        ContentScale.CROP -> Modifier
            .aspectRatio(contentRatio)
            .wrapContentWidth(unbounded = true)
            .fillMaxSize()
    }
     */



    Box(
        modifier = internalModifier.background(color = Color.Green)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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
}