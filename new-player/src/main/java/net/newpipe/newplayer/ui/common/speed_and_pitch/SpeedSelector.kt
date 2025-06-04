package net.newpipe.newplayer.ui.common.speed_and_pitch

import androidx.annotation.OptIn
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R

/** hide */
@OptIn(UnstableApi::class)
@Composable
internal fun SpeedSelector() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(fontWeight = FontWeight.Bold, text = stringResource(R.string.playback_speed))
        }

        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
            val (startButton, endButton, slider, legendBox) = createRefs()


            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(legendBox) {
                        start.linkTo(slider.start)
                        end.linkTo(slider.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(slider.top)
                        width = Dimension.fillToConstraints
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Text("asdf")

                Text("bsdf")

                Text("csdf")
            }



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

