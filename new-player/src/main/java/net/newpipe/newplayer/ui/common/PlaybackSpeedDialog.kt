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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.uiModel.NewPlayerUIState
import net.newpipe.newplayer.uiModel.NewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModelDummy


/** @hide **/
@OptIn(UnstableApi::class)
@Composable
internal fun PlaybackSpeedDialog(
    uiMode: NewPlayerUIState,
    viewModel: NewPlayerViewModel,
    onDismiss: () -> Unit
) {
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

        }
    }
}

@OptIn(UnstableApi::class)
@Preview(device = "spec:width=1080px,height=700px,dpi=440,orientation=landscape")
@Composable
private fun PlaybackSpeedDialogPreview() {
    var dialogVisible by remember {
        mutableStateOf(true)
    }

    VideoPlayerTheme {
        if(dialogVisible) {
            PlaybackSpeedDialog(
                viewModel = NewPlayerViewModelDummy(),
                uiMode = NewPlayerUIState.DUMMY,
                onDismiss = {
                    dialogVisible = false
                })
        }
    }
}
