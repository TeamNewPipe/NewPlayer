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

package net.newpipe.newplayer.testapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.VideoPlayerView
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelImpl
import net.newpipe.newplayer.ui.ContentScale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val videoPlayerViewModel: VideoPlayerViewModel by viewModels<VideoPlayerViewModelImpl>()

    @Inject
    lateinit var newPlayer: NewPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val video_view = findViewById<VideoPlayerView>(R.id.new_player_video_view)
        val start_stream_button = findViewById<Button>(R.id.start_stream_button)
        val buttons_layout = findViewById<View>(R.id.buttons_layout)

        start_stream_button.setOnClickListener {
            newPlayer.playWhenReady = true
            newPlayer.setStream(getString(R.string.ccc_chromebooks_video))
        }

        video_view.viewModel = videoPlayerViewModel
        videoPlayerViewModel.newPlayer = newPlayer

        videoPlayerViewModel.maxContentRatio = 4F/3F
        videoPlayerViewModel.contentFitMode = ContentScale.FIT_INSIDE


        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        if (videoPlayerViewModel.uiState.value.fullscreen) {
            //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            //    v.setPadding(0, 0, 0, 0)
            //    insets
            //}
            buttons_layout.visibility = View.GONE
            //windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            buttons_layout.visibility = View.VISIBLE

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )
                insets
            }

            //windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }

        videoPlayerViewModel.callbackListener = object : VideoPlayerViewModel.Listener {
            override fun onFullscreenToggle(isFullscreen: Boolean) {}

            override fun onUiVissibleToggle(isVissible: Boolean) {
                if (isVissible) {
                   // windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                } else {
                   // windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                }
            }
        }

    }
}