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

package net.newpipe.newplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.runtime.Composable

val VideoPlayerColorScheme = darkColorScheme(
    primary = video_player_primary,
    onPrimary = video_player_onPrimary,
    primaryContainer = video_player_primaryContainer,
    onPrimaryContainer = video_player_onPrimaryContainer,
    secondary = video_player_secondary,
    onSecondary = video_player_onSecondary,
    secondaryContainer = video_player_secondaryContainer,
    onSecondaryContainer = video_player_onSecondaryContainer,
    tertiary = video_player_tertiary,
    onTertiary = video_player_onTertiary,
    tertiaryContainer = video_player_tertiaryContainer,
    onTertiaryContainer = video_player_onTertiaryContainer,
    error = video_player_error,
    errorContainer = video_player_errorContainer,
    onError = video_player_onError,
    onErrorContainer = video_player_onErrorContainer,
    background = video_player_background,
    onBackground = video_player_onBackground,
    surface = video_player_surface,
    onSurface = video_player_onSurface,
    surfaceVariant = video_player_surfaceVariant,
    onSurfaceVariant = video_player_onSurfaceVariant,
    outline = video_player_outline,
    inverseOnSurface = video_player_inverseOnSurface,
    inverseSurface = video_player_inverseSurface,
    inversePrimary = video_player_inversePrimary,
    surfaceTint = video_player_surfaceTint,
    outlineVariant = video_player_outlineVariant,
    scrim = video_player_scrim,
)

@Composable
/** @hide */
internal fun VideoPlayerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VideoPlayerColorScheme,
        typography = Typography,
        content = content
    )
}