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

package net.newpipe.newplayer.internal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.R
import net.newpipe.newplayer.VideoPlayerView
import net.newpipe.newplayer.internal.model.VideoPlayerViewModel
import net.newpipe.newplayer.internal.model.VideoPlayerViewModelImpl
import net.newpipe.newplayer.internal.ui.VideoPlayerUI
import net.newpipe.newplayer.internal.ui.theme.VideoPlayerTheme

private const val TAG = "VideoPlayerFragment"

@AndroidEntryPoint
class VideoPlayerFragment() : Fragment() {

    private val viewModel: VideoPlayerViewModel by viewModels<VideoPlayerViewModelImpl>()
    private var currentVideoRatio = 0F
    private lateinit var composeView: ComposeView

    var fullScreenToggleListener: VideoPlayerView.FullScreenToggleListener? = null

    var minLayoutRatio = 4F / 3F
        set(value) {
            if (value <= 0 && maxLayoutRatio < minLayoutRatio)
                Log.e(
                    TAG,
                    "minLayoutRatio can not be 0 or smaller or bigger then maxLayoutRatio. Ignore: $value"
                )
            else {
                field = value
                updateViewRatio()
            }
        }

    var maxLayoutRatio = 16F / 9F
        set(value) {
            if (value <= 0 && value < minLayoutRatio)
                Log.e(
                    TAG,
                    "maxLayoutRatio can not be 0 smaller ans smaller then minLayoutRatio. Ignore: $value"
                )
            else {
                field = value
                updateViewRatio()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window = activity?.window!!
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val view = inflater.inflate(R.layout.video_player_framgent, container, false)
        composeView = view.findViewById(R.id.player_copose_view)

        viewModel.listener = object : VideoPlayerViewModel.Listener {
            override fun requestUpdateLayoutRatio(videoRatio: Float) {
                currentVideoRatio = videoRatio
                updateViewRatio()
            }
        }

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                VideoPlayerTheme {
                    VideoPlayerUI(viewModel = viewModel,
                        {fullscreenOn ->
                            fullScreenToggleListener?.fullscreenToggle(fullscreenOn)
                        })
                }
            }
        }

        return view
    }

    private fun updateViewRatio() {
        if(this::composeView.isInitialized) {
            composeView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                val ratio = currentVideoRatio.coerceIn(minLayoutRatio, maxLayoutRatio)
                dimensionRatio = "$ratio:1"
            }
        }
    }
}