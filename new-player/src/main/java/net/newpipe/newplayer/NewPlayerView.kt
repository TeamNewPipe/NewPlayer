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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.uiModel.InternalNewPlayerViewModel
import net.newpipe.newplayer.uiModel.NewPlayerViewModel
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme
import net.newpipe.newplayer.data.NewPlayerException


/**
 * A wrapper for [NewPlayerUI] to allow NewPlayer to be used in a [views](https://developer.android.com/develop/ui/views/layout/declaring-layout) environment.
 */
@AndroidEntryPoint
class NewPlayerView : FrameLayout {

    var viewModel: NewPlayerViewModel? = null
        set(value) {
            assert(viewModel is InternalNewPlayerViewModel?) {
                throw NewPlayerException("The view model given to NewPlayerView must be of type InternalNewPlayerViewModel. This can not be implemented externally, so do not extend NewPlayerViewModel")
            }
            field = value
            applyViewModel()
        }

    private val composeView:ComposeView

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        val view = LayoutInflater.from(context).inflate(R.layout.video_player_view, this)
        composeView = view.findViewById(R.id.video_player_compose_view)

        applyViewModel()
    }

    private fun applyViewModel() {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                VideoPlayerTheme {
                    NewPlayerUI(viewModel = viewModel as InternalNewPlayerViewModel?)
                }
            }
        }
    }

}