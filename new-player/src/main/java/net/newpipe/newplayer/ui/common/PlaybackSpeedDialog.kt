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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.NewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy


/** @hide **/
@OptIn(UnstableApi::class)
@Composable
internal fun PlaybackSpeedDialog(
    uiState: NewPlayerUIState,
    viewModel: NewPlayerViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f),
            elevation = CardDefaults.elevatedCardElevation(5.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            ) {
                Box(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.playback_speed))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.decrease_playback_speed)
                        )
                    }
                    Slider(modifier = Modifier.weight(1f), value = 0.4f, onValueChange = {})
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.increase_playback_speed)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.playback_pitch))
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.decrease_playback_speed)
                        )
                    }
                    Slider(modifier = Modifier.weight(1f), value = 0.4f, onValueChange = {})
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.increase_playback_speed)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(true, onCheckedChange = { showNotYetImplementedToast(context) })
                    Text(text = stringResource(R.string.detach_pitch_with_description))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(true, onCheckedChange = { showNotYetImplementedToast(context) })
                    Text(text = stringResource(R.string.fast_forward_on_silence))
                }

                Row {
                    Box(modifier = Modifier.width(17.dp))
                    TextButton(onClick = { showNotYetImplementedToast(context) }) {
                        Text(stringResource(R.string.reset))
                    }

                    Box(modifier = Modifier.weight(1f))

                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.ok))
                    }
                    Box(modifier = Modifier.width(17.dp))
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=900px,dpi=440,orientation=landscape")
@Composable
private fun PlaybackSpeedDialogPreview() {
    var dialogVisible by remember {
        mutableStateOf(true)
    }

    VideoPlayerTheme {
        if (dialogVisible) {
            PlaybackSpeedDialog(
                viewModel = NewPlayerViewModelDummy(),
                uiState = NewPlayerUIState.DUMMY,
                onDismiss = {
                    dialogVisible = false
                })
        }
    }
}
