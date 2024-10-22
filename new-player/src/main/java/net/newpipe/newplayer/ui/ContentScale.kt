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

package net.newpipe.newplayer.ui

/**
 * Depicts how the video should be layout in fullscreen mode.
 */
enum class ContentScale {
    /**
     * The video will fill the entire screen but it will also be stretched.
     */
    STRETCHED,

    /**
     * The video will fit fully inside the screen's view pod, and will align with at least two
     * opposing borders.
     */
    FIT_INSIDE,

    /**
     * The video will fill the entire screen. The aspect ratio of the video will remain true,
     * but parts of the video will be cropped of. However, the video will align with at least
     * two opposing borders, so that as little as possible video content is cropped of.
     */
    CROP
}
