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


package net.newpipe.newplayer.utils

internal data class VideoSize(
    val width: Int,
    val height: Int,
    /// The width/height ratio of a single pixel
    val pixelWidthHeightRatio: Float
) {
    override fun equals(other: Any?) =
        when (other) {
            is VideoSize ->
                other.width == this.width && other.height == this.height && pixelWidthHeightRatio == other.pixelWidthHeightRatio

            else -> false
        }

    override fun hashCode() =
        width + height * 9999999 + (pixelWidthHeightRatio*10000).toInt()

    fun getRatio() =
        (width * pixelWidthHeightRatio) / height

    override fun toString() =
        "VideoSize(width = $width, height = $height, pixelRatio = $pixelWidthHeightRatio, ratio = ${getRatio()})"

    companion object {
        val DEFAULT = VideoSize(0, 0, 1F)

        fun fromMedia3VideoSize(videoSize: androidx.media3.common.VideoSize) =
            VideoSize(videoSize.width, videoSize.height, videoSize.pixelWidthHeightRatio)
    }
}

