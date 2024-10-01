package net.newpipe.newplayer.ui.common

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

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.model.NewPlayerUIState
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelDummy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

private const val BOX_PADDING = 4

@OptIn(UnstableApi::class)
@Composable
fun ThumbPreview(
    viewModel: NewPlayerViewModel,
    uiState: NewPlayerUIState,
    additionalStartPadding: Int = 0,
    additionalEndPadding: Int = 0,
    thumbsize: Dp
) {
    var sliderBoxWidth by remember {
        mutableIntStateOf(-10)
    }

    var previewBoxWidth by remember {
        mutableIntStateOf(-1)
    }

    val boxPaddingPxls = with(LocalDensity.current) { (BOX_PADDING).dp.toPx() }
    val thumbSizePxls = with(LocalDensity.current) {(thumbsize.toPx())}

    val previewPosition = additionalStartPadding - boxPaddingPxls + thumbSizePxls/2 +
            ((sliderBoxWidth - additionalEndPadding - additionalStartPadding - (3*boxPaddingPxls))
                    * uiState.seekerPosition)

    val edgeCorrectedPreviewPosition =
        if (previewPosition < (previewBoxWidth / 2))
            0
        else if ((sliderBoxWidth - previewBoxWidth / 2) < previewPosition)
            sliderBoxWidth - previewBoxWidth
        else
            previewPosition - (previewBoxWidth / 2)

    Box(
        Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .height((60 + (2 * BOX_PADDING)).dp)
            .padding(BOX_PADDING.dp)
            .onGloballyPositioned { rect ->
                sliderBoxWidth = rect.size.width
            }) {
        Card(modifier = Modifier
            .onGloballyPositioned { rect ->
                previewBoxWidth = rect.size.width
            }
            .offset { IntOffset(edgeCorrectedPreviewPosition.toInt(), 0) },
            elevation = CardDefaults.cardElevation(BOX_PADDING.dp)
        ) {
            Thumbnail(
                thumbnail = null,
                contentDescription = stringResource(id = R.string.seek_thumb_preview)
            )
        }

        /*
        Surface(
            modifier = Modifier
                .size(10.dp, 10.dp)
                .offset { IntOffset(previewPosition.toInt(), 200) }, color = Color.Blue
        ) {
        }
         */
    }
}


@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=600px,dpi=440")
@Composable
fun ThumbPreviewPreview() {
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    var startOffset by remember { mutableIntStateOf(0) }
    var endOffset by remember { mutableIntStateOf(0) }

    VideoPlayerTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)
        ) {
            ThumbPreview(
                viewModel = NewPlayerViewModelDummy(), uiState = NewPlayerUIState.DUMMY.copy(
                    seekerPosition = sliderPosition
                ), additionalStartPadding = startOffset, additionalEndPadding = endOffset,
                20.dp // see handle width
            )

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Left", modifier = Modifier.onGloballyPositioned {
                    startOffset = it.size.width
                })
                Slider(modifier = Modifier.weight(1f), value = sliderPosition, onValueChange = {
                    sliderPosition = it
                })
                Text(text = "R", modifier = Modifier.onGloballyPositioned {
                    endOffset = it.size.width
                })
            }
        }
    }
}