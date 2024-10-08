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

package net.newpipe.newplayer.ui.videoplayer.controller

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.EmbeddedUiConfig
import net.newpipe.newplayer.model.UIModeState
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.common.NewPlayerSeeker
import net.newpipe.newplayer.ui.common.ThumbPreview
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.ui.common.getEmbeddedUiConfig
import net.newpipe.newplayer.ui.common.getLocale
import net.newpipe.newplayer.ui.common.getTimeStringFromMs
import net.newpipe.newplayer.ui.seeker.SeekerDefaults


private const val TAG = "BottomUI"

private const val SEEKER_PADDING = 4

@OptIn(UnstableApi::class)
@Composable
internal fun BottomUI(
    modifier: Modifier, viewModel: NewPlayerViewModel, uiState: NewPlayerUIState
) {

    var previewPaddingStart by remember {
        mutableFloatStateOf(0f)
    }

    var previewPaddingEnd by remember {
        mutableFloatStateOf(0f)
    }

    val seekerPaddingPx = with(LocalDensity.current) { SEEKER_PADDING.dp.toPx() }

    Column(modifier = modifier) {

        val previewModifier = if (uiState.uiMode.fullscreen)
            Modifier.offset(y = (-20).dp) /* make some space so your thumb is not in the way*/
        else
            Modifier.offset(y = (-10).dp)

        ThumbPreview(
            modifier = previewModifier,
            uiState = uiState,
            thumbSize = SeekerDefaults.ThumbRadius * 2,
            additionalStartPaddingPxls = previewPaddingStart.toInt(),
            additionalEndPaddingPxls = previewPaddingEnd.toInt(),
            previewHeight = if (uiState.uiMode.fullscreen) 120.dp else 60.dp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val locale = getLocale()!!
            Text(text =
            getTimeStringFromMs(uiState.playbackPositionInMs, getLocale() ?: locale),
                modifier = Modifier.onGloballyPositioned {
                    previewPaddingStart = it.size.width + seekerPaddingPx
                })

            NewPlayerSeeker(
                modifier = Modifier
                    .weight(1F)
                    .padding(start = SEEKER_PADDING.dp, end = SEEKER_PADDING.dp),
                viewModel = viewModel,
                uiState = uiState
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.onGloballyPositioned {
                    previewPaddingEnd = it.size.width + seekerPaddingPx
                }
            ) {
                Text(getTimeStringFromMs(uiState.durationInMs, getLocale() ?: locale))

                val embeddedUiConfig = when (LocalContext.current) {
                    is Activity -> getEmbeddedUiConfig(LocalContext.current as Activity)
                    else -> EmbeddedUiConfig.DUMMY
                }

                IconButton(
                    onClick = if (uiState.uiMode.fullscreen) {
                        {
                            viewModel.changeUiMode(UIModeState.EMBEDDED_VIDEO, embeddedUiConfig)
                        }
                    } else {
                        {
                            viewModel.changeUiMode(UIModeState.FULLSCREEN_VIDEO, embeddedUiConfig)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.uiMode.fullscreen) Icons.Filled.FullscreenExit
                        else Icons.Filled.Fullscreen,
                        contentDescription = stringResource(R.string.widget_description_toggle_fullscreen)
                    )
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////
// Preview
///////////////////////////////////////////////////////////////////

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=600px,dpi=440,orientation=landscape")
@Composable
private fun VideoPlayerControllerBottomUIPreview() {
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    VideoPlayerTheme {
        Surface(color = Color.Black) {
            BottomUI(
                modifier = Modifier,
                viewModel = object : NewPlayerViewModelDummy() {
                    override fun seekPositionChanged(newValue: Float) {
                        super.seekPositionChanged(newValue)
                        sliderPosition = newValue
                    }
                },
                uiState = NewPlayerUIState.DUMMY.copy(
                    uiMode = UIModeState.FULLSCREEN_VIDEO_CONTROLLER_UI,
                    seekerPosition = sliderPosition,
                    playbackPositionInMs = 3 * 60 * 1000,
                    bufferedPercentage = 0.4f
                ),
            )
        }
    }
}