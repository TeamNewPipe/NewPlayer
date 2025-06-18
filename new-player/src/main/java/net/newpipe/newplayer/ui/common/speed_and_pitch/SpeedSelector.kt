package net.newpipe.newplayer.ui.common.speed_and_pitch

import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.common.floatToStringWithoutTrailingZerosTwoDigitsAccuracy
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

/** hide */
@OptIn(UnstableApi::class)
@Composable
internal fun SpeedSelector(
    @StringRes titleText: Int,
    minValue: Float,
    maxValue: Float,
    currentValue: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(fontWeight = FontWeight.Bold, text = stringResource(titleText))
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val (startButton, endButton, slider, startText, valueText, endText) = createRefs()


            Text(
                modifier = Modifier.constrainAs(startText) {
                    start.linkTo(startButton.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(slider.top)
                },
                text = "x" + floatToStringWithoutTrailingZerosTwoDigitsAccuracy(minValue)
            )
            Text(
                modifier = Modifier.constrainAs(valueText) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top)
                },
                text = "x" + floatToStringWithoutTrailingZerosTwoDigitsAccuracy(currentValue),
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.constrainAs(endText) {
                    top.linkTo(parent.top)
                    end.linkTo(endButton.start)
                },
                text = "x" + floatToStringWithoutTrailingZerosTwoDigitsAccuracy(maxValue)
            )




            IconButton(
                modifier = Modifier
                    .constrainAs(startButton) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .wrapContentWidth(), onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.decrease_playback_speed)
                )
            }

            Slider(modifier = Modifier.constrainAs(slider) {
                start.linkTo(startButton.end)
                end.linkTo(endButton.start)
                centerVerticallyTo(endButton)
                width = Dimension.fillToConstraints
            }, value = 0.5f, onValueChange = {})


            IconButton(
                modifier = Modifier
                    .constrainAs(endButton) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .wrapContentWidth(),
                onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.increase_playback_speed)
                )
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=1080px,dpi=440,orientation=landscape")
@Composable
private fun SpeedSelectorPreview() {

    VideoPlayerTheme {
        SpeedSelector(
            titleText = R.string.playback_speed,
            minValue = 0.1f,
            maxValue = 5.0f,
            currentValue = 0.90f
        )
    }
}


