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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

@Composable
fun DropDownMenu() {
    var showMainMenu: Boolean by remember { mutableStateOf(false) }

    var pixel_density = LocalDensity.current

    var offsetY by remember {
        mutableStateOf(0.dp)
    }

    Box {
        IconButton(onClick = { showMainMenu = true }, modifier = Modifier.onPlaced {
            offsetY = with(pixel_density) {
                it.size.height.toDp()
            }

        }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.menu_item_more_settings)
            )
        }
        DropdownMenu(modifier = Modifier.align(Alignment.TopStart),
            offset = DpOffset(x = 0.dp, y = -offsetY),
            expanded = showMainMenu,
            onDismissRequest = { showMainMenu = false }) {
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_open_in_browser)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = stringResource(R.string.menu_item_open_in_browser)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_share_timestamp)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.menu_item_share_timestamp)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.mute)) }, leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = stringResource(R.string.mute)
                )
            }, onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_fit_screen)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.FitScreen,
                        contentDescription = stringResource(R.string.menu_item_fit_screen)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_sub_titles)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Subtitles,
                        contentDescription = stringResource(R.string.menu_item_sub_titles)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(text = { Text(stringResource(R.string.menu_item_language)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = stringResource(R.string.menu_item_language)
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })

        }
    }

}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@Preview(device = "spec:width=1080px,height=1080px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerDropDownPreview() {
    VideoPlayerTheme {
        Box(Modifier.fillMaxSize()){
            DropDownMenu()
        }
    }
}
