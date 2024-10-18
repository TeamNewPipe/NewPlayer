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


package net.newpipe.newplayer.data

import net.newpipe.newplayer.ui.NewPlayerUI

/**
 * Depicts the playback mode that NewPlayer is currently in.
 * Or in other words this tells how NewPlayer is displaying media.
 */
enum class PlayMode {

    /**
     * NewPlayer is currently idling and is not displaying anything.
     * If NewPlayer is in this mode [NewPlayerUI] will show a loading circle on a black background.
     * If this should not be visible please hide ore remove [NewPlayerUI] when NewPlayer switches
     * to IDLE mode.
     */
    IDLE,

    /**
     * Video is played in an embedded view.
     * [See Screenshot](https://github.com/TeamNewPipe/NewPlayer/blob/master/misc/screenshots/373686583-1164cf7c-66eb-48be-aeda-55e6e6294cf1.png)
     */
    EMBEDDED_VIDEO,

    /**
     * Video is played in fullscreen mode.
     * [See Screenshot](https://github.com/TeamNewPipe/NewPlayer/blob/master/misc/screenshots/373685724-42609e51-6bf7-4008-b084-a59ce111f3c1.png)
     */
    FULLSCREEN_VIDEO,

    /**
     * Video is displayed in Picture in Picture mode
     * [see Screenshot](https://github.com/TeamNewPipe/NewPlayer/blob/master/misc/screenshots/373691456-4aaff87d-dbf8-4877-866b-60e6fc05ea6a.png)
     */
    PIP,

    /**
     * TODO: Obsolete and does not work. Remove this!!!
     */
    BACKGROUND_VIDEO,

    /**
     * TODO: Obsolete and does not work. Remove this!!!
     */
    BACKGROUND_AUDIO,

    /**
     * Plays a Video/Audio stream while showing the audio player ui.
     * [See Screenshot](https://github.com/TeamNewPipe/NewPlayer/blob/master/misc/screenshots/373688583-9011749c-3aec-4bf7-a368-40000c84f8e3.png)
     * [See Screenshot in landscape](https://github.com/TeamNewPipe/NewPlayer/blob/master/misc/screenshots/373689058-9fc27dfd-7f89-48de-b0ff-cd6e9bd4fdbd.png)
     */
    FULLSCREEN_AUDIO,

    /**
     * Shows the embedded UI for Audio playback of a Video/Audio stream.
     */
    EMBEDDED_AUDIO
}