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
 * Media3 does not provide a class to represent individual tracks. So here we go.
 */
interface StreamTrack : Comparable<StreamTrack> {
    val fileFormat: String

    fun toShortIdentifierString(): String
    fun toLongIdentifierString(): String
}

/**
 * A track representing a video track.
 */
data class VideoStreamTrack(
    val width: Int,
    val height: Int,
    val frameRate: Int,
    override val fileFormat: String,
) : StreamTrack {

    override fun toShortIdentifierString() =
        "${if (width < height) width else height}p${if (frameRate > 30) frameRate else ""}"

    override fun toLongIdentifierString() = "$fileFormat ${toShortIdentifierString()}"

    override fun compareTo(other: StreamTrack) =
        if (other is VideoStreamTrack) {
            val diff = width * height - other.width * other.height
            if (diff == 0) {
                frameRate - other.frameRate
            } else {
                diff
            }
        } else {
            1
        }

    override fun toString() = """
        VideoStreamTrack {
            width = $width
            height = $height
            frameRate = $frameRate
            fileFormat = $fileFormat
        }
    """.trimIndent()

}

/**
 * A track representing an audio track.
 */
data class AudioStreamTrack(
    val bitrate: Int,
    override val fileFormat: String,
    val language: String? = null
) : StreamTrack {

    override fun toShortIdentifierString() =
        if (bitrate < 1000) "${bitrate}bps" else "${bitrate / 1000}kbps"

    override fun toLongIdentifierString() = "$fileFormat ${toShortIdentifierString()}"

    override fun compareTo(other: StreamTrack) =
        if (other is AudioStreamTrack) {
            bitrate - other.bitrate
        } else {
            -1
        }

    override fun toString() = """
        AudioStreamTrack {
            bitrate = $bitrate
            language = $language
            fileFormat = $fileFormat
        }
    """.trimIndent()
}