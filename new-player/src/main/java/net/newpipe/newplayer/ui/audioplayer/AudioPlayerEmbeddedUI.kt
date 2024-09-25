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

package net.newpipe.newplayer.ui.audioplayer

import android.app.Activity
import androidx.annotation.OptIn
import androidx.collection.emptyLongSet
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.ui.selection_ui.ITEM_CORNER_SHAPE
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.videoplayer.CONTROLLER_UI_BACKGROUND_COLOR
import net.newpipe.newplayer.ui.videoplayer.PreviewBackgroundSurface
import net.newpipe.newplayer.utils.Thumbnail
import net.newpipe.newplayer.utils.getEmbeddedUiConfig
import net.newpipe.newplayer.utils.getLocale
import net.newpipe.newplayer.utils.getTimeStringFromMs

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AudioPlayerEmbeddedUI(viewModel: NewPlayerViewModel, uiState: NewPlayerUIState) {
    val locale = getLocale()!!

    val embeddedUIConfig = if (LocalContext.current is Activity)
        getEmbeddedUiConfig(activity = LocalContext.current as Activity)
    else
        EmbeddedUiConfig.DUMMY

    Box(modifier = Modifier.wrapContentSize()) {
        Thumbnail(
            modifier = Modifier.fillMaxWidth(),
            thumbnail = uiState.currentlyPlaying?.mediaMetadata?.artworkUri,
            contentDescription = stringResource(
                id = R.string.stream_thumbnail
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 10.dp, bottom = 14.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 0.5.dp,
                        bottom = 0.5.dp
                    ),
                    text = getTimeStringFromMs(
                        uiState.playbackPositionInMs,
                        locale,
                        leadingZerosForMinutes = false
                    ),
                    fontSize = 14.sp,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Surface(
                color = CONTROLLER_UI_BACKGROUND_COLOR,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 14.dp, end = 10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 0.5.dp,
                        bottom = 0.5.dp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    text = getTimeStringFromMs(
                        uiState.durationInMs,
                        locale,
                        leadingZerosForMinutes = false
                    ),
                    fontSize = 14.sp,
                )
            }
        }
        LinearProgressIndicator(modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth(),
            progress = {
                val duration = if (uiState.durationInMs == 0L) {
                    0.000000001f
                } else {
                    uiState.durationInMs.toFloat()
                }

                (uiState.playbackPositionInMs.toFloat() / duration)
            })


        Surface(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    viewModel.changeUiMode(
                        UIModeState.FULLSCREEN_AUDIO,
                        embeddedUiConfig = embeddedUIConfig
                    )
                }, color = Color.Transparent
        ) {

        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=1080px,dpi=440,orientation=landscape")
@Composable
fun AuidioPlayerEmbeddedPreview() {
    VideoPlayerTheme {
        PreviewBackgroundSurface {
            AudioPlayerEmbeddedUI(
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY,
            )
        }
    }
}