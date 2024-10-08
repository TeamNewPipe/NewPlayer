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

package net.newpipe.newplayer.ui.videoplayer.gesture_ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.INDICATOR_BACKGROUND_COLOR

private const val TAG = "VolumeCircle"

private const val LINE_STROKE_WIDTH =6
private const val CIRCLE_SIZE = 130

@Composable
internal fun VolumeCircle(
    modifier: Modifier = Modifier,
    volumeFraction: Float,
    isBrightness: Boolean = false
) {
    assert(volumeFraction in 0f..1f) {
         Log.e(TAG, "Volume fraction must be in ragne [0;1]. It was $volumeFraction")
    }

    Box(modifier) {
        Canvas(Modifier.size(CIRCLE_SIZE.dp)) {
            val arcSize = (CIRCLE_SIZE - LINE_STROKE_WIDTH).dp.toPx();
            drawCircle(color = INDICATOR_BACKGROUND_COLOR, radius = (CIRCLE_SIZE / 2).dp.toPx())
            drawArc(
                topLeft = Offset(
                    (LINE_STROKE_WIDTH / 2).dp.toPx(), (LINE_STROKE_WIDTH / 2).dp.toPx()
                ),
                size = Size(arcSize, arcSize),
                startAngle = -90f,
                sweepAngle = 360f * volumeFraction,
                useCenter = false,
                color = Color.White,
                style = Stroke(width = LINE_STROKE_WIDTH.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp),
            imageVector = (if (isBrightness) getBrightnessIcon(volumeFraction = volumeFraction)
            else getVolumeIcon(volumeFraction = volumeFraction)),
            contentDescription = stringResource(
                id = if (isBrightness) R.string.brightness_indicator
                else R.string.volume_indicator
            )
        )
    }
}

@Composable
private fun getVolumeIcon(volumeFraction: Float) =
    if (volumeFraction == 0f) Icons.AutoMirrored.Filled.VolumeOff
    else if (volumeFraction < 0.3) Icons.AutoMirrored.Filled.VolumeMute
    else if (volumeFraction < 0.6) Icons.AutoMirrored.Filled.VolumeDown
    else Icons.AutoMirrored.Filled.VolumeUp

@Composable
private fun getBrightnessIcon(volumeFraction: Float) =
    if (volumeFraction < 0.3) Icons.Filled.BrightnessLow
    else if (volumeFraction < 0.6) Icons.Filled.BrightnessMedium
    else Icons.Filled.BrightnessHigh

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
private fun VolumeCirclePreview() {
    VideoPlayerTheme {
        Surface(color = Color.White) {
            VolumeCircle(volumeFraction = 0.3f, isBrightness = false)
        }
    }
}