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

package net.newpipe.newplayer.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.shapes.Shape
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.waterfall
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.core.os.ConfigurationCompat
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import java.util.Locale

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    LaunchedEffect(orientation) {
        val activity = context.findActivity() ?: return@LaunchedEffect
        activity.requestedOrientation = orientation
    }
}

@SuppressLint("NewApi")
fun getDefaultBrightness(activity: Activity): Float {
    val window = activity.window
    val layout = window.attributes as WindowManager.LayoutParams
    return if (layout.screenBrightness < 0) 0.5f else layout.screenBrightness
}

@SuppressLint("NewApi")
fun setScreenBrightness(value: Float, activity: Activity) {
    val window = activity.window
    val layout = window.attributes as WindowManager.LayoutParams
    layout.screenBrightness = value
    window.attributes = layout
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


@Composable
@ReadOnlyComposable
fun getLocale(): Locale? {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
}

@Composable
fun getInsets() =
    WindowInsets.systemBars.union(WindowInsets.displayCutout).union(WindowInsets.waterfall)

private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_SECOND = 1000

private const val MILLIS_PER_DAY =
    HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_MINUTE = SECONDS_PER_MINUTE * MILLIS_PER_SECOND

fun getTimeStringFromMs(
    timeSpanInMs: Long,
    locale: Locale,
    leadingZerosForMinutes: Boolean = true
): String {
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
        else String.format(
            locale,
            if (leadingZerosForMinutes) "%02d:%02d" else "%d:%02d",
            minutes,
            seconds
        )

    return time_string
}

@Composable
fun Thumbnail(
    modifier: Modifier = Modifier,
    thumbnail: Thumbnail?,
    contentDescription: String,
    shape: androidx.compose.ui.graphics.Shape? = null
) {
    val modifier = if (shape == null) {
        modifier
    } else {
        modifier
            .clip(shape)
    }

    when (thumbnail) {
        is OnlineThumbnail -> AsyncImage(
            modifier = modifier,
            model = thumbnail.url,
            contentDescription = contentDescription,
        )

        is BitmapThumbnail -> Image(
            modifier = modifier,
            bitmap = thumbnail.img,
            contentDescription = contentDescription
        )

        is VectorThumbnail -> Image(
            modifier = modifier,
            imageVector = thumbnail.vec,
            contentDescription = contentDescription
        )

        null -> Image(
            modifier = modifier,
            painter = painterResource(R.drawable.tiny_placeholder),
            contentDescription = contentDescription
        )
    }
}


