package net.newpipe.newplayer.ui.audioplayer

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.R
import net.newpipe.newplayer.ui.common.CoverThumbnail
import net.newpipe.newplayer.uiModel.NewPlayerUIState

/**hide*/
@OptIn(UnstableApi::class)
@Composable
internal fun CoverArtUI(modifier: Modifier = Modifier, uiState: NewPlayerUIState) {
    Box(modifier = modifier) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            CoverThumbnail(
                modifier = Modifier.fillMaxWidth(),
                metadata = uiState.currentlyPlaying?.mediaMetadata,
                contentDescription = stringResource(id = R.string.stream_thumbnail),
            )
        }
    }
}