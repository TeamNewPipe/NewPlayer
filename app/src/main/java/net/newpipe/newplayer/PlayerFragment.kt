package net.newpipe.newplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import net.newpipe.newplayer.ui.VideoPlayerControllerUI
import net.newpipe.newplayer.ui.theme.VideoPlayerTheme

class PlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.player_framgent, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.player_copose_view)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                VideoPlayerTheme {
                    VideoPlayerControllerUI()
                }
            }
        }
        return view
    }
}