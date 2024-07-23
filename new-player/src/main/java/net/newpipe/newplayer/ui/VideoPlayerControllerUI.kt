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

package net.newpipe.newplayer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.theme.video_player_onSurface

@Composable
fun VideoPlayerControllerUI(
    isPlaying: Boolean,
    fullscreen: Boolean,
    uiVissible: Boolean,
    play: () -> Unit,
    pause: () -> Unit,
    prevStream: () -> Unit,
    nextStream: () -> Unit,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit,
    showUi: () -> Unit,
    hideUi: () -> Unit
) {
    TouchControll(modifier = Modifier, hideUi = hideUi, showUi = showUi, uiVissible = uiVissible) {
        if (uiVissible) {
            Surface(
                modifier = Modifier.fillMaxSize(), color = Color(0x75000000)
            ) {
                Box(
                    modifier = if (fullscreen) {
                        Modifier
                            .background(Color.Transparent)
                            .windowInsetsPadding(WindowInsets.systemBars)
                    } else {
                        Modifier
                            .background(Color.Transparent)
                    }
                ) {
                    TopUI(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 45.dp)
                            .padding(top = 4.dp, start = 16.dp, end = 16.dp)
                    )
                    CenterUI(
                        modifier = Modifier.align(Alignment.Center),
                        isPlaying,
                        play = play,
                        pause = pause,
                        prevStream = prevStream,
                        nextStream = nextStream
                    )
                    BottomUI(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, end = 16.dp)
                            .defaultMinSize(minHeight = 40.dp)
                            .fillMaxWidth(),
                        isFullscreen = fullscreen,
                        switchToFullscreen,
                        switchToEmbeddedView
                    )
                }
            }
        }
    }
    if (fullscreen) {
        BackHandler {
            switchToEmbeddedView()
        }
    }
}

///////////////////////////////////////////////////////////////////
// TopUI
///////////////////////////////////////////////////////////////////

@Composable
private fun TopUI(modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1F)) {
            Text("The Title", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(
                "The Channel",
                fontSize = 12.sp,

                )
        }
        Button(
            onClick = { /*TODO*/ },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = video_player_onSurface
            ),
        ) {
            Text(
                "1080p", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp)
            )
        }
        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Text(
                "1x", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp)
            )
        }
        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = stringResource(R.string.widget_description_chapter_selection)
            )
        }
        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.widget_descriptoin_playlist_item_selection)
            )
        }
        MainMenu()
    }
}

@Composable
private fun TouchControll(
    modifier: Modifier,
    hideUi: () -> Unit,
    showUi: () -> kotlin.Unit,
    uiVissible: Boolean,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            if (uiVissible)
                hideUi()
            else
                showUi()
        }) {
        content()
    }
}

@Composable
private fun MainMenu() {
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
// CenterUI
///////////////////////////////////////////////////////////////////

@Composable
private fun CenterUI(
    modifier: Modifier,
    isPlaying: Boolean,
    play: () -> Unit,
    pause: () -> Unit,
    nextStream: () -> Unit,
    prevStream: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        CenterControllButton(
            buttonModifier = Modifier.size(80.dp),
            iconModifier = Modifier.size(40.dp),
            icon = Icons.Filled.SkipPrevious,
            contentDescriptoion = stringResource(R.string.widget_description_previous_stream),
            onClick = prevStream
        )

        CenterControllButton(
            buttonModifier = Modifier.size(80.dp),
            iconModifier = Modifier.size(60.dp),
            icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescriptoion = stringResource(
                if (isPlaying) R.string.widget_description_pause
                else R.string.widget_description_play
            ),
            onClick = if (isPlaying) pause else play
        )

        CenterControllButton(
            buttonModifier = Modifier.size(80.dp),
            iconModifier = Modifier.size(40.dp),
            icon = Icons.Filled.SkipNext,
            contentDescriptoion = stringResource(R.string.widget_description_next_stream),
            onClick = nextStream
        )
    }
}

@Composable
private fun CenterControllButton(
    buttonModifier: Modifier,
    iconModifier: Modifier,
    icon: ImageVector,
    contentDescriptoion: String?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = video_player_onSurface
        ),
        modifier = buttonModifier
    ) {
        Icon(
            imageVector = icon, modifier = iconModifier, contentDescription = contentDescriptoion
        )
    }
}

///////////////////////////////////////////////////////////////////
// BottomUI
///////////////////////////////////////////////////////////////////

@Composable
private fun BottomUI(
    modifier: Modifier,
    isFullscreen: Boolean,
    switchToFullscreen: () -> Unit,
    switchToEmbeddedView: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text("00:06:45")
        Slider(value = 0.4F, onValueChange = {}, modifier = Modifier.weight(1F))
        Text("00:09:40")
        IconButton(onClick = if (isFullscreen) switchToEmbeddedView else switchToFullscreen) {
            Icon(
                imageVector = if (isFullscreen) Icons.Filled.FullscreenExit
                else Icons.Filled.Fullscreen,
                contentDescription = stringResource(R.string.widget_description_toggle_fullscreen)
            )
        }
    }
}

///////////////////////////////////////////////////////////////////
// Utils
///////////////////////////////////////////////////////////////////

@Composable
private fun ViewInFullScreen() {
    //LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}

@Composable
fun PreviewBackgroundSurface(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.Black
    ) {
        content()
    }
}

@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerUIPreviewEmbeded() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(isPlaying = false,
                fullscreen = false,
                uiVissible = true,
                play = {},
                pause = {},
                prevStream = {},
                nextStream = {},
                switchToFullscreen = {},
                switchToEmbeddedView = {},
                showUi = {},
                hideUi = {})
        }
    }
}

@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=landscape")
@Composable
fun VideoPlayerControllerUIPreviewLandscape() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(isPlaying = true,
                fullscreen = true,
                uiVissible = true,
                play = {},
                pause = {},
                prevStream = {},
                nextStream = {},
                switchToEmbeddedView = {},
                switchToFullscreen = {},
                showUi = {},
                hideUi = {})
        }
    }
}

@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=portrait")
@Composable
fun VideoPlayerControllerUIPreviewPortrait() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            VideoPlayerControllerUI(
                isPlaying = false,
                fullscreen = true,
                uiVissible = true,
                play = {},
                pause = {},
                prevStream = {},
                nextStream = {},
                switchToEmbeddedView = {},
                switchToFullscreen = {},
                showUi = {},
                hideUi = {})
        }
    }
}