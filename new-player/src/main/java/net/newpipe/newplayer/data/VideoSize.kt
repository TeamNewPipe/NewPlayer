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

/**
 * This class depicts video sizes. A Media3 implementation ([androidx.media3.common.VideoSize])
 * could have been used, however because of the pixelWidthHeightRatio stuff I wanted a tool that
 * I can control better.
 *
 * @param width depicts the width of the video with which it is encoded with
 *      (not with which it is played back with).
 * @param height depicts the height of the video with which it is encoded with
 *      (not with which it is played back with).
 * @param pixelWidthHeightRatio the ratio of each individual pixel. Normally it's 1 but some
 *      (older) media.ccc videos go wonky.
 *
 * @hide
 */
/** @hide */
internal data class VideoSize(

    val width: Int,
    val height: Int,
    /// The width/height ratio of a single pixel
    val pixelWidthHeightRatio: Float
) : Comparable<VideoSize> {

    override fun compareTo(other: VideoSize) = width * height - other.width * other.height

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

