package net.newpipe.newplayer.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.theme.video_player_onSurface

@Composable
fun VideoPlayerUI() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box() {
            TopUI(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 45.dp)
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp)
            )
            CenterUI(modifier = Modifier.align(Alignment.Center))
            BottomUI(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp)
                    .defaultMinSize(minHeight = 40.dp)
                    .fillMaxWidth()
            )
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
                containerColor = Color.Transparent,
                contentColor = video_player_onSurface
            ),
        ) {
            Text(
                "1080p",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(0.dp)
            )
        }
        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Text(
                "1x",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(0.dp)
            )
        }
        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Chapter Selection"
            )
        }
        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Playlist Selection"
            )
        }
        MainMenu()
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
        IconButton(onClick = {showMainMenu = true},
            modifier = Modifier.onPlaced {
                offsetY = with(pixel_density) {
                    it.size.height.toDp()
                }

            }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More settings"
            )
        }
        DropdownMenu(
            modifier = Modifier.align(Alignment.TopStart),
            offset = DpOffset(x = 0.dp, y = -offsetY),
            expanded = showMainMenu,
            onDismissRequest = {showMainMenu = false}) {
            DropdownMenuItem(
                text = { Text("Open in browser") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = "Open In Browser"
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false})
            DropdownMenuItem(
                text = { Text("Share Timestamp") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share Time Stamp"
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false})
            DropdownMenuItem(
                text = { Text("Mute") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute"
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false})
            DropdownMenuItem(
                text = { Text("Fit Screen") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.FitScreen,
                        contentDescription = "Fit Screen"
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false})
            DropdownMenuItem(
                text = { Text("Sub Titles") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Subtitles,
                        contentDescription = "Sub Titles"
                    )
                },
                onClick = { /*TODO*/ showMainMenu = false })
            DropdownMenuItem(
                text = { Text("Language") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = "Language"
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
private fun CenterUI(modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        CenterControllButton(
            buttonModifier = Modifier.size(80.dp),
            iconModifier = Modifier.size(40.dp),
            icon = Icons.Filled.SkipPrevious,
            contentDescriptoion = "Previous Stream",
            onClick = {}
        )

        CenterControllButton(
            buttonModifier = Modifier.size(80.dp),
            iconModifier = Modifier.size(60.dp),
            icon = Icons.Filled.PlayArrow,
            contentDescriptoion = "Previous Stream",
            onClick = {}
        )
        CenterControllButton(
            buttonModifier = Modifier.size(80.dp),
            iconModifier = Modifier.size(40.dp),
            icon = Icons.Filled.SkipNext,
            contentDescriptoion = "Next Stream",
            onClick = {}
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
            containerColor = Color.Transparent,
            contentColor = video_player_onSurface
        ),
        modifier = buttonModifier
    ) {
        Icon(
            imageVector = icon,
            modifier = iconModifier,
            contentDescription = contentDescriptoion
        )
    }
}

///////////////////////////////////////////////////////////////////
// BottomUI
///////////////////////////////////////////////////////////////////

@Composable
private fun BottomUI(modifier: Modifier) {
    var isFullscreen: Boolean by rememberSaveable { mutableStateOf(false) }

    if (isFullscreen) {
        LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text("06:45")
        Slider(value = 0.4F, onValueChange = {}, modifier = Modifier.weight(1F))
        Text("09:40")
        IconButton(onClick = { isFullscreen = !isFullscreen }) {
            Icon(
                imageVector = Icons.Filled.Fullscreen,
                contentDescription = "Fullscreen"
            )
        }
    }
}

///////////////////////////////////////////////////////////////////
// Utils
///////////////////////////////////////////////////////////////////

@Composable
private fun ViewInFullScreen() {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}

@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
fun PlayerUIPreviewEmbeded() {
    VideoPlayerTheme {
        VideoPlayerUI()
    }
}

@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=landscape")
@Composable
fun PlayerUIPreviewLandscape() {
    VideoPlayerTheme {
        VideoPlayerUI()
    }
}

@Preview(device = "spec:width=2340px,height=1080px,dpi=440,orientation=portrait")
@Composable
fun PlayerUIPreviewPortrait() {
    VideoPlayerTheme {
        VideoPlayerUI()
    }
}