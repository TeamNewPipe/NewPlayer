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

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import dagger.hilt.android.AndroidEntryPoint
import net.newpipe.newplayer.internal.VideoPlayerFragment

@AndroidEntryPoint
class VideoPlayerView : FrameLayout {

    val videoPlayerFragment:VideoPlayerFragment

    var maxLayoutRatio: Float
        get() = videoPlayerFragment.maxLayoutRatio
        set(value) {videoPlayerFragment.maxLayoutRatio=value}


    var minLayoutRatio: Float
        get() = videoPlayerFragment.minLayoutRatio
        set(value) {videoPlayerFragment.minLayoutRatio = value}

    var newPlayer:NewPlayer?
        set(value) {videoPlayerFragment.newPlayer = value}
        get() = videoPlayerFragment.newPlayer

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        val view = LayoutInflater.from(context).inflate(R.layout.video_player_view, this)

        videoPlayerFragment = VideoPlayerFragment()
        when (context) {
            is AppCompatActivity -> {
                context.supportFragmentManager.beginTransaction()
                    .add(R.id.video_player_fragment_container, videoPlayerFragment).commit()
            }

            else -> {
                throw Exception("The context that should host the NewPlayer Embedded VideoPlayerView is not an AppCompatActivity: $context")
            }
        }
    }
}