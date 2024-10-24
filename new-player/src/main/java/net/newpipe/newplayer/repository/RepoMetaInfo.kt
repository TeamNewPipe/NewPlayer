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

package net.newpipe.newplayer.repository

/**
 * General information about the repository and the data it provides.
 */
data class RepoMetaInfo(
    /**
     * Depict weather it is possible to create timestamped likes to items.
     * For example, you can create a time stamped url to a youtube video.
     * But you can not even share a link to a video that is stored locally on your phones' storage.
     */
    val canHandleTimestampedLinks: Boolean,

    /**
     * Tells if the repository pulls data from the Network or if the data the repository provides
     * is stored locally. Please keep in mind that this setting will also influence the sleep lock
     * of your device.
     */
    val pullsDataFromNetwork: Boolean
)

