package net.newpipe.newplayer.uiModel

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.ui.NewPlayerUI
import net.newpipe.newplayer.ui.ContentScale
import net.newpipe.newplayer.data.PlayMode.IDLE


/**
 * The NewPlayerViewModel must live and be owned by an Activity context.
 */
@OptIn(UnstableApi::class)
interface NewPlayerViewModel {

    /**
     * The current instance of [NewPlayer]. If set to null the [NewPlayerUI] instance
     * which the viewModel talks too will display the same as if [NewPlayer] was in [IDLE] mode.
     *
     * You can always add or remove the NewPlayer instance.
     */
    var newPlayer: NewPlayer?

    /**
     * Depicts weather the picture of the video should be stretched, fit inside or be cropped.
     * You can set it yourself, but [NewPlayerUI] can also change it.
     */
    var contentFitMode: ContentScale

    /**
     * This represents the state the UI is in. [NewPlayerUI] will basically render out that state.
     * You can make your UI also listen to changes to this state. This is especially helpful
     * for switching to or from fullscreen or to or from PiP mode.
     */
    val uiState: StateFlow<NewPlayerUIState>

    /**
     * If the user dragged down the embedded video or audio player. This callback will tell you
     * how far the user dragged down. Keep in mind that the user can also decide to drag the
     * [NewPlayerUI] up again. If the [NewPlayerUI] is not dragged up to its original position
     * you should take over control and maybe make the [NewPlayerUI] snap to the bottom of the
     * screen to make space for your UI.
     */
    val embeddedPlayerDraggedDownBy: SharedFlow<Float>

    /**
     * [NewPlayerUI] will use some back press events for its own navigation purposes.
     * If the viewModel decides that a back press event should not be handled by itself.
     * It will forward the event to you through this callback.
     */
    val onBackPressed: SharedFlow<Unit>

    /**
     * This is something you have to call in the Activity that should host the [NewPlayerUI].
     * See the [example app's](https://github.com/TeamNewPipe/NewPlayer/blob/master/test-app/src/main/java/net/newpipe/newplayer/testapp/MainActivity.kt) main activity to find out how to use this.
     *
     * Long story short your activity should handle the `onPictureInPictureModeChanged` event.
     * You can do this by adding this code to your Activity's `onCreate()` function:
     * ```
     *  addOnPictureInPictureModeChangedListener { mode ->
     *      newPlayerViewModel.onPictureInPictureModeChanged(mode.isInPictureInPictureMode)
     *  }
     * ```
     */
    fun onPictureInPictureModeChanged(isPictureInPictureMode: Boolean)
}