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

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.logic.TrackUtils
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.UIModeState
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable

/** @hide */
internal fun LanguageMenu(uiState: NewPlayerUIState, viewModel: InternalNewPlayerViewModel, isVisible: Boolean, makeInvisible: () -> Unit) {
    val availableLanguages = TrackUtils.getAvailableLanguages(uiState.currentlyAvailableTracks)

    DropdownMenu(expanded = isVisible, onDismissRequest = {
        makeInvisible()
        viewModel.dialogVisible(false)
    }) {
        for (language in availableLanguages) {
            val locale = Locale(language)
            val context = LocalContext.current

            DropdownMenuItem(
                text = {
                    Text(locale.displayLanguage)
                },
                onClick = { /*TODO*/
                    showNotYetImplementedToast(context)
                    makeInvisible()
                    viewModel.dialogVisible(false)
                })
        }
    }
}


@OptIn(UnstableApi::class)
@Composable

/** @hide */
internal fun LanguageMenuItem(uiState: NewPlayerUIState, onClick: () -> Unit) {
    val availableLanguages = TrackUtils.getAvailableLanguages(uiState.currentlyAvailableTracks)

    if (2 <= availableLanguages.size) {
        val language = TrackUtils.getAvailableLanguages(uiState.currentlyAvailableTracks)[0]
        val locale = Locale(language)
        DropdownMenuItem(text = { Text(locale.displayLanguage) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = String.format(
                        stringResource(R.string.menu_selected_language_item),
                        locale.displayLanguage
                    )
                )
            }, onClick = onClick)
    }
}