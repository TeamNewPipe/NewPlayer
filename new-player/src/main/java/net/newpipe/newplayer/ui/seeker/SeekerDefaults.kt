/*
 * Copyright 2023 Vivek Singh
 *
 * @Author Vivek Singh
 * @Author Christian Schabesberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Original code was taken from: https://github.com/2307vivek/Seeker/
 *
 */
package net.newpipe.newplayer.ui.seeker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.ui.theme.VideoPlayerColorScheme


/** @hide */
internal object SeekerDefaults {

    /**
     * Creates a [SeekerColors] that represents the different colors used in parts of the
     * [Seeker] in different states.
     *
     * @param progressColor color of the progress indicator.
     * @param trackColor color of the track.
     * @param disabledProgressColor color of the progress indicator when the Slider is
     * disabled.
     * @param disabledTrackColor color of the track when theSlider is disabled.
     * @param thumbColor thumb color when enabled
     * @param disabledThumbColor thumb color when disabled.
     * @param readAheadColor color of the read ahead indicator.
     */
    @Composable
    fun seekerColors(
        progressColor: Color = MaterialTheme.colorScheme.primary,
        trackColor: Color = TrackColor,
        disabledProgressColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = DisabledProgressAlpha),
        disabledTrackColor: Color = disabledProgressColor
            .copy(alpha = DisabledTrackAlpha)
            .compositeOver(MaterialTheme.colorScheme.onSurface),
        thumbColor: Color = MaterialTheme.colorScheme.primary,
        disabledThumbColor: Color = MaterialTheme.colorScheme.onSurface
            .copy(alpha = 0.0F)
            .compositeOver(MaterialTheme.colorScheme.surface),
        readAheadColor: Color = ReadAheadColor
    ): SeekerColors = DefaultSeekerColor(
        progressColor = progressColor,
        trackColor = trackColor,
        disabledProgressColor = disabledProgressColor,
        disabledTrackColor = disabledTrackColor,
        thumbColor = thumbColor,
        disabledThumbColor = disabledThumbColor,
        readAheadColor = readAheadColor
    )

    /**
     * Creates a [SeekerDimensions] which represents dimension of different parts of [Seeker] in
     * different states.
     *
     * @param trackHeight height of the track.
     * @param progressHeight height of the progress indicator.
     * @param thumbRadius radius of the thumb slider.
     * @param gap gap between two segments in the track.
     * */
    @Composable
    fun seekerDimensions(
        trackHeight: Dp = TrackHeight,
        progressHeight: Dp = trackHeight,
        thumbRadius: Dp = ThumbRadius,
        gap: Dp = Gap
    ): SeekerDimensions = DefaultSeekerDimensions(
        trackHeight = trackHeight,
        progressHeight = progressHeight,
        thumbRadius = thumbRadius,
        gap = gap
    )

    private val TrackColor = Color(0xFFD9D9D9)
    private val ReadAheadColor = Color(0xFFBDBDBD)

    private const val TrackAlpha = 0.24f
    private const val ReadAheadAlpha = 0.44f
    private const val DisabledTrackAlpha = 0.22f
    private const val DisabledProgressAlpha = 0.32f

    
/** @hide */
internal val ThumbRadius = 10.dp
    private val TrackHeight = 4.dp
    private val Gap = 2.dp

    
/** @hide */
internal val MinSliderHeight = 48.dp
    
/** @hide */
internal val MinSliderWidth = ThumbRadius * 2

    
/** @hide */
internal val ThumbDefaultElevation = 1.dp
    
/** @hide */
internal val ThumbPressedElevation = 6.dp

    
/** @hide */
internal val ThumbRippleRadius = 24.dp
}

/**
 * Represents the colors used by different parts of [Seeker] in different states.
 *
 * See [SeekerDefaults.seekerColors] for default implementation.
 * */
@Stable

/** @hide */
internal interface SeekerColors {

    /**
     * Represents the color used for the seeker's track, depending on [enabled].
     *
     * @param enabled whether the [Seeker] is enabled or not
     */
    @Composable
    fun trackColor(enabled: Boolean): State<Color>

    /**
     * Represents the color used for the seeker's thumb, depending on [enabled].
     *
     * @param enabled whether the [Seeker] is enabled or not
     */
    @Composable
    fun thumbColor(enabled: Boolean): State<Color>

    /**
     * Represents the color used for the seeker's progress indicator, depending on [enabled].
     *
     * @param enabled whether the [Seeker] is enabled or not
     */
    @Composable
    fun progressColor(enabled: Boolean): State<Color>

    /**
     * Represents the color used for the seeker's read ahead indicator, depending on [enabled].
     *
     * @param enabled whether the [Seeker] is enabled or not
     */
    @Composable
    fun readAheadColor(enabled: Boolean): State<Color>
}

/**
 * Represents the dimensions used by different parts of [Seeker] in different states.
 *
 * See [SeekerDefaults.seekerDimensions] for default implementation.
 * */
@Stable

/** @hide */
internal interface SeekerDimensions {

    /**
     * Represents the height used for the seeker's track.
     */
    @Composable
    fun trackHeight(): State<Dp>

    /**
     * Represents the height used for the seeker's progress indicator.
     */
    @Composable
    fun progressHeight(): State<Dp>

    /**
     * Represents the gap used between two segments in seeker's track.
     */
    @Composable
    fun gap(): State<Dp>

    /**
     * Represents the radius used for seeker's thumb.
     */
    @Composable
    fun thumbRadius(): State<Dp>
}

@Immutable

/** @hide */
internal data class DefaultSeekerDimensions(
    val trackHeight: Dp,
    val progressHeight: Dp,
    val gap: Dp,
    val thumbRadius: Dp
) : SeekerDimensions {
    @Composable
    override fun trackHeight(): State<Dp> {
        return rememberUpdatedState(trackHeight)
    }

    @Composable
    override fun progressHeight(): State<Dp> {
        return rememberUpdatedState(progressHeight)
    }

    @Composable
    override fun gap(): State<Dp> {
        return rememberUpdatedState(gap)
    }

    @Composable
    override fun thumbRadius(): State<Dp> {
        return rememberUpdatedState(thumbRadius)
    }
}

@Immutable

/** @hide */
internal data class DefaultSeekerColor(
    val progressColor: Color,
    val trackColor: Color,
    val disabledTrackColor: Color,
    val disabledProgressColor: Color,
    val thumbColor: Color,
    val disabledThumbColor: Color,
    val readAheadColor: Color
) : SeekerColors {
    @Composable
    override fun trackColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) trackColor else disabledTrackColor
        )
    }

    @Composable
    override fun thumbColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) thumbColor else disabledThumbColor
        )
    }

    @Composable
    override fun progressColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) progressColor else disabledProgressColor
        )
    }

    @Composable
    override fun readAheadColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            readAheadColor
        )
    }
}