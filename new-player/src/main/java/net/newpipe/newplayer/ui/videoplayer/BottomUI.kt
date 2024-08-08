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

import android.app.LocaleConfig
import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.ConfigurationCompat
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.seeker.DefaultSeekerColor
import net.newpipe.newplayer.ui.seeker.Seeker
import net.newpipe.newplayer.ui.seeker.SeekerColors
import net.newpipe.newplayer.ui.seeker.SeekerDefaults
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import java.util.Locale
import kotlin.math.min

@Composable
fun BottomUI(
    modifier: Modifier,
    isFullscreen: Boolean,
    seekPosition: Float,
    durationInMs: Long,
    playbackPositionInMs: Long,
    bufferedPercentage: Float,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    seekPositionChanged: (Float) -> Unit,
    seekingFinished: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(getTimeStringFromMs(playbackPositionInMs, getLocale() ?: Locale.US))
        Seeker(
            Modifier.weight(1F),
            value = seekPosition,
            onValueChange = seekPositionChanged,
            onValueChangeFinished = seekingFinished,
            readAheadValue = bufferedPercentage,
            colors = customizedSeekerColors()
        )

        //Slider(value = 0.4F, onValueChange = {}, modifier = Modifier.weight(1F))

        Text(getTimeStringFromMs(durationInMs, getLocale() ?: Locale.US))

        IconButton(onClick = if (isFullscreen) switchToEmbeddedView else switchToFullscreen) {
            Icon(
                imageVector = if (isFullscreen) Icons.Filled.FullscreenExit
                else Icons.Filled.Fullscreen,
                contentDescription = stringResource(R.string.widget_description_toggle_fullscreen)
            )
        }
    }
}

@Composable
private fun customizedSeekerColors() : SeekerColors {
    val colors = DefaultSeekerColor(
        progressColor = MaterialTheme.colorScheme.primary,
        thumbColor = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        readAheadColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledProgressColor = MaterialTheme.colorScheme.primary,
        disabledThumbColor = MaterialTheme.colorScheme.primary,
        disabledTrackColor = MaterialTheme.colorScheme.primary
    )
    return colors
}

@Composable
@ReadOnlyComposable
fun getLocale(): Locale? {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
}


private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_SECOND = 1000

private const val MILLIS_PER_DAY =
    HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_MINUTE = SECONDS_PER_MINUTE * MILLIS_PER_SECOND

private fun getTimeStringFromMs(timeSpanInMs: Long, locale: Locale) : String {
    val days = timeSpanInMs / MILLIS_PER_DAY
    val millisThisDay = timeSpanInMs - days * MILLIS_PER_DAY
    val hours = millisThisDay / MILLIS_PER_HOUR
    val millisThisHour = millisThisDay - hours * MILLIS_PER_HOUR
    val minutes = millisThisHour / MILLIS_PER_MINUTE
    val milliesThisMinute = millisThisHour - minutes * MILLIS_PER_MINUTE
    val seconds = milliesThisMinute / MILLIS_PER_SECOND


    val time_string =
        if (0L < days) String.format(locale, "%d:%02d:%02d:%02d", days, hours, minutes, seconds)
        else if (0L < hours) String.format(locale, "%d:%02d:%02d", hours, minutes, seconds)
        else String.format(locale, "%d:%02d", minutes, seconds)

    return time_string
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerBottomUIPreview() {
    VideoPlayerTheme {
        Surface(color = Color.Black) {
            BottomUI(
                modifier = Modifier,
                isFullscreen = true,
                seekPosition = 0.4F,
                durationInMs = 90 * 60 * 1000,
                playbackPositionInMs = 3 * 60 * 1000,
                bufferedPercentage = 0.4f,
                switchToFullscreen = { },
                switchToEmbeddedView = { },
                seekPositionChanged = {},
                seekingFinished = {}
            )
        }
    }
}