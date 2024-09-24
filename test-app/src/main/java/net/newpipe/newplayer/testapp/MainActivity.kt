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
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import net.newpipe.newplayer.ActivityBrainSlug
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.PlayMode
import net.newpipe.newplayer.model.NewPlayerViewModel
import net.newpipe.newplayer.model.NewPlayerViewModelImpl
import net.newpipe.newplayer.testapp.databinding.ActivityMainBinding
import net.newpipe.newplayer.ui.ContentScale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val newPlayerViewModel: NewPlayerViewModel by viewModels<NewPlayerViewModelImpl>()

    @Inject
    lateinit var newPlayer: NewPlayer

    var activityBrainSlug: ActivityBrainSlug? = null

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttons.start6502StreamButton.setOnClickListener {
            newPlayer.playWhenReady = true
            newPlayer.playStream("6502", PlayMode.EMBEDDED_VIDEO)
        }

        binding.buttons.startImuStreamButton.setOnClickListener {
            newPlayer.playWhenReady = true
            newPlayer.playStream("imu", PlayMode.EMBEDDED_VIDEO)
        }

        binding.buttons.startPortraitStreamButton.setOnClickListener {
            newPlayer.playWhenReady = true
            newPlayer.playStream("portrait", PlayMode.EMBEDDED_VIDEO)
        }

        binding.buttons.startYtTestVideoButton.setOnClickListener {
            newPlayer.playWhenReady = true
            newPlayer.playStream("yt_test", PlayMode.EMBEDDED_VIDEO)
        }

        binding.buttons.add6502StreamButton.setOnClickListener {
            newPlayer.addToPlaylist("6502")
        }

        binding.buttons.addImuStreamButton.setOnClickListener {
            newPlayer.addToPlaylist("imu")
        }

        binding.buttons.addPortraitStreamButton.setOnClickListener {
            newPlayer.addToPlaylist("portrait")
        }

        binding.buttons.addYtTestVideoButton.setOnClickListener {
            newPlayer.addToPlaylist("yt_test")
        }

        binding.buttons.listenModeButton.setOnClickListener {
            newPlayer.playBackMode.update {
                PlayMode.FULLSCREEN_AUDIO
            }
        }

        binding.buttons.pipModeButton.setOnClickListener {
            newPlayer.playBackMode.update {
                PlayMode.PIP
            }
        }

        newPlayerViewModel.newPlayer = newPlayer
        newPlayerViewModel.contentFitMode = ContentScale.FIT_INSIDE

        activityBrainSlug = ActivityBrainSlug(newPlayerViewModel)
        activityBrainSlug?.let {
            it.embeddedPlayerView = binding.embeddedPlayer
            it.addViewToHideOnFullscreen(binding.buttonsLayout as View)
            it.addViewToHideOnFullscreen(binding.embeddedPlayerLayout as View)
            it.fullscreenPlayerView = binding.fullscreenPlayer
            it.rootView = binding.root
        }
    }
}