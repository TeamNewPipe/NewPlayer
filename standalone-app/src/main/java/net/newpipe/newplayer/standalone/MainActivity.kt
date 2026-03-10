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

package net.newpipe.newplayer.standalone

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.data.PlayMode
import net.newpipe.newplayer.standalone.ui.StandaloneAppUI
import net.newpipe.newplayer.ui.ContentScale
import net.newpipe.newplayer.uiModel.NewPlayerViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var newPlayer: NewPlayer

    @OptIn(UnstableApi::class)
    private val viewModel: NewPlayerViewModelImpl by viewModels()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.newPlayer = newPlayer
        viewModel.contentFitMode = ContentScale.FIT_INSIDE

        setContent {
            StandaloneAppUI(viewModel = viewModel, newPlayer = newPlayer)
        }

        ////////////////////////////////////////////////////////////////////////////////////
        //// This call has to be inserted into the Activity holding the NewPlayerUI.
        //// Without this, transitions into or out of PiP mode don't work.
        ////////////////////////////////////////////////////////////////////////////////////
        addOnPictureInPictureModeChangedListener { mode ->
            viewModel.onPictureInPictureModeChanged(mode.isInPictureInPictureMode)
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent ?: return
        if (intent.action != Intent.ACTION_SEND) return

        val item = when {
            intent.type?.startsWith("video/") == true -> {
                val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                } ?: intent.clipData?.getItemAt(0)?.uri

                uri?.let {
                    try {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                        // Temporary URI grant only — proceed anyway
                    }
                    it.toString()
                }
            }

            intent.type == "text/plain" -> intent.getStringExtra(Intent.EXTRA_TEXT)

            else -> null
        } ?: return

        if (newPlayer.playBackMode.value != PlayMode.IDLE) {
            newPlayer.addToPlaylist(item)
        } else {
            newPlayer.playWhenReady = true
            newPlayer.playStream(item, PlayMode.EMBEDDED_VIDEO)
        }
    }
}
