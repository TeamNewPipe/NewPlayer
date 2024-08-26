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

package net.newpipe.newplayer

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.newpipe.newplayer.model.VideoPlayerViewModel

private const val TAG = "ActivityBrainSlug"

class ActivityBrainSlug(val viewModel: VideoPlayerViewModel) {

    val brainSlugScope = CoroutineScope(Dispatchers.Main + Job())

    var rootView: View? = null
        set(value) {
            field = value
            field?.let {
                if (viewModel.uiState.value.uiMode.fullscreen) {
                    removeSystemInsets()
                } else {
                    addSystemInsets()
                }
            }
        }

    private var viewsToHideOnFullscreen: MutableList<View> = ArrayList()
    var fullscreenPlayerView: VideoPlayerView? = null
        set(value) {
            field = value
            if (viewModel.uiState.value.uiMode.fullscreen) {
                value?.visibility = View.VISIBLE
                field?.viewModel = viewModel
            } else {
                value?.visibility = View.GONE
                field?.viewModel = null
            }
        }

    var embeddedPlayerView: VideoPlayerView? = null
        set(value) {
            field = value
            if (viewModel.uiState.value.uiMode.fullscreen) {
                field?.viewModel = null
                value?.visibility = View.GONE
            } else {
                field?.viewModel = viewModel
                value?.visibility = View.VISIBLE
            }
        }

    init {
        brainSlugScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.uiMode.fullscreen) {
                    removeSystemInsets()
                    viewsToHideOnFullscreen.forEach { it.visibility = View.GONE }
                    fullscreenPlayerView?.visibility = View.VISIBLE
                    embeddedPlayerView?.visibility = View.GONE
                    fullscreenPlayerView?.viewModel = viewModel
                    embeddedPlayerView?.viewModel = null
                } else {
                    addSystemInsets()
                    viewsToHideOnFullscreen.forEach { it.visibility = View.VISIBLE }
                    fullscreenPlayerView?.visibility = View.GONE
                    embeddedPlayerView?.visibility = View.VISIBLE
                    fullscreenPlayerView?.viewModel = null
                    embeddedPlayerView?.viewModel = viewModel
                }
            }
        }
    }

    fun addViewToHideOnFullscreen(view: View) {
        viewsToHideOnFullscreen.add(view)
        if (viewModel.uiState.value.uiMode.fullscreen) {
            view.visibility = View.GONE
        }
    }

    private fun addSystemInsets() {
        rootView?.let { rootView ->
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
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

    private fun removeSystemInsets() {
        rootView?.let { rootView ->
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                v.setPadding(
                    0, 0, 0, 0
                )
                insets
            }
        }
    }
}