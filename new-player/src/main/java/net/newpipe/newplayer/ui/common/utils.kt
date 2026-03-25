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

package net.newpipe.newplayer.ui.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.view.Window
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.waterfall
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.core.os.ConfigurationCompat
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import net.newpipe.newplayer.R
import net.newpipe.newplayer.data.NewPlayerException
import net.newpipe.newplayer.uiModel.EmbeddedUiConfig
import java.util.Locale

private const val INCHES_PER_CENTIMETER = 0.3937F

/** Get the [Activity] from local context. Assumes the activity exists!
 * @return the activity
 * @throws NullPointerException if there is no Activity
 */
/** @hide */
@Composable
internal fun activity(): Activity = LocalContext.current.findActivity()!!

/** Call block with the [Activity] from current context, if there is an activity.
 *
 * @param default: the default value if there is no activity
 * @param block: the block to call with the activity
 */
/** @hide */
@Composable
internal fun <T> activity(default: T, block: @Composable Activity.() -> T): T =
    when (val a = LocalContext.current.findActivity()) {
        null -> default
        else -> block(a)
    }

/** @hide */
@Composable
internal fun window(): Window = activity().window

/** @hide */
internal fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/** @hide */
@Composable
internal fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    LaunchedEffect(orientation) {
        val activity = context.findActivity() ?: return@LaunchedEffect
        activity.requestedOrientation = orientation
    }
}

@SuppressLint("NewApi")
/** @return the default brightness of the screen, via window attributes */
/** @hide */
internal fun Activity.getDefaultBrightness(): Float {
    val layout = window.attributes as WindowManager.LayoutParams
    return if (layout.screenBrightness < 0) -1f else layout.screenBrightness
}

/** @hide */
@SuppressLint("NewApi")
internal fun setScreenBrightness(value: Float, activity: Activity) {
    val window = activity.window
    val layout = window.attributes as WindowManager.LayoutParams
    layout.screenBrightness = value
    window.attributes = layout
}


/** @hide */
@Composable
@ReadOnlyComposable
internal fun getLocale(): Locale {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
}

@Composable
/** @return A collection of current activity/window configurations */
/** @hide */
internal fun getEmbeddedUiConfig() = activity(EmbeddedUiConfig.DUMMY) { getEmbeddedUiConfig() }

@Composable
@ReadOnlyComposable
/** @return A collection of current activity/window configurations */
/** @hide */
internal fun Activity.getEmbeddedUiConfig(): EmbeddedUiConfig {
    val view = LocalView.current

    val isLightStatusBar = WindowCompat.getInsetsController(
        window,
        view
    ).isAppearanceLightStatusBars
    val screenOrientation = requestedOrientation
    val defaultBrightness = getDefaultBrightness()
    return EmbeddedUiConfig(
        systemBarInLightMode = isLightStatusBar,
        brightness = defaultBrightness,
        screenOrientation = screenOrientation
    )
}

/** @hide */
@Composable
internal fun getInsets() =
    WindowInsets.systemBars.union(WindowInsets.displayCutout).union(WindowInsets.waterfall)

private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_SECOND = 1000

private const val MILLIS_PER_DAY =
    HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_MINUTE = SECONDS_PER_MINUTE * MILLIS_PER_SECOND


/** @hide */
internal fun getTimeStringFromMs(
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

/** @hide */
@Composable
internal fun Thumbnail(
    modifier: Modifier = Modifier,
    thumbnail: Uri?,
    contentDescription: String,
    shape: androidx.compose.ui.graphics.Shape? = null
) {
    ThumbnailImpl(modifier = modifier, model = thumbnail, contentDescription = contentDescription, shape = shape)
}

/** @hide */
@Composable
internal fun Thumbnail(
    modifier: Modifier = Modifier,
    thumbnail: ByteArray?,
    contentDescription: String,
    shape: androidx.compose.ui.graphics.Shape? = null
) {
    ThumbnailImpl(modifier = modifier, model = thumbnail, contentDescription = contentDescription, shape = shape)
}

@Composable
private fun ThumbnailImpl(
    modifier: Modifier,
    model: Any?,
    contentDescription: String,
    shape: androidx.compose.ui.graphics.Shape?
) {
    val clippedModifier = if (shape == null) {
        modifier
    } else {
        modifier.clip(shape)
    }

    if (model != null) {
        AsyncImage(
            modifier = clippedModifier,
            model = model,
            contentDescription = contentDescription,
            placeholder = painterResource(id = R.drawable.tiny_placeholder),
            error = painterResource(id = R.drawable.tiny_placeholder)
        )
    } else {
        Image(
            modifier = clippedModifier,
            painter = painterResource(R.drawable.tiny_placeholder),
            contentDescription = contentDescription
        )
    }
}

/** @hide */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
internal fun isInPowerSaveMode() =
    (LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager)
        .isPowerSaveMode


/** @hide */
@OptIn(UnstableApi::class)
internal fun getPlaylistDurationInMS(playlist: List<MediaItem>): Long {
    var duration = 0L
    for (item in playlist) {
        val itemDuration = item.mediaMetadata.durationMs
            ?: throw NewPlayerException("Can not calculate duration of a playlist if an item does not have a duration: MediItem in question: ${item.mediaMetadata.title}")
        duration += itemDuration
    }
    return duration
}


/** @hide */
internal fun relaunchCurrentActivity(activity: Activity) {

    /*
    val intent = Intent(activity, activity.javaClass).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    activity.startActivity(intent)

     */
    activity.startActivity(activity.intent)

}

/** @hide */
@Composable
internal fun HiddenMeasure(
    content: @Composable () -> Unit,
    onMeasured: (Placeable) -> Unit
) {
    Layout(
        modifier = Modifier.size(DpSize.Zero),
        content = content
    ) { measurable, _ ->
        val placeable = measurable.first().measure(Constraints())
        onMeasured(placeable)
        layout(0, 0) {
            // Draw nothing
        }
    }
}

/** @hide */
@Composable
internal fun getPixelsPerCentimeter() =
    LocalResources.current.displayMetrics.xdpi * INCHES_PER_CENTIMETER
