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
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.VideoPlayerView
import net.newpipe.newplayer.model.VideoPlayerViewModel
import net.newpipe.newplayer.model.VideoPlayerViewModelImpl
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
        video_view.viewModel = videoPlayerViewModel
        videoPlayerViewModel.newPlayer = newPlayer

        videoPlayerViewModel.maxContentRatio = 4F/3F

        /*
        video_view.fullScreenToggleListener = object : VideoPlayerView.FullScreenToggleListener {
            override fun fullscreenToggle(turnOn: Boolean) {
                if (turnOn) {
                    println("gurken blub")
                    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                        v.setPadding(
                            0, 0, 0, 0
                        )
                        insets
                    }

                } else {
                    println("gurken blab")
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

                }
            }


        }
        */

        newPlayer.playWhenReady = true
        newPlayer.setStream(getString(R.string.ccc_chromebooks_video))

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

    }
}