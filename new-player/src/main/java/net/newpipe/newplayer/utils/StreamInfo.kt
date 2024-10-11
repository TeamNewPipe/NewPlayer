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

interface StreamInfo {
    val fileFormat: String

    fun toShortIdentifierString(): String
    fun toLongIdentifierString(): String
}

data class DynamicStreamInfo(override val fileFormat: String) : StreamInfo {
    override fun toShortIdentifierString() = fileFormat

    override fun toLongIdentifierString() = fileFormat
}

data class AudioVideoStreamInfo(
    val width: Int,
    val height: Int,
    val frameRate: Int,
    val audioBitrate: Int,
    override val fileFormat: String
) : StreamInfo {
    override fun toShortIdentifierString() = "${width}p${if (frameRate != 30) frameRate else ""}"

    override fun toLongIdentifierString() = "$fileFormat ${toShortIdentifierString()}"

    override fun equals(other: Any?) =
        other is AudioVideoStreamInfo
                && other.width == width
                && other.height == height
                && other.frameRate == frameRate
                && other.audioBitrate == audioBitrate
                && other.fileFormat == fileFormat
}

data class VideoStreamInfo(
    val width: Int,
    val height: Int,
    val frameRate: Int,
    override val fileFormat: String
) : StreamInfo {
    override fun toShortIdentifierString() = "${width}p${if (frameRate != 30) frameRate else ""}"

    override fun toLongIdentifierString() = "$fileFormat ${toShortIdentifierString()}"

    override fun equals(other: Any?) =
        other is VideoStreamInfo
                && other.width == width
                && other.height == height
                && other.frameRate == frameRate
                && other.fileFormat == fileFormat
}

data class AudioStreamInfo(
    val bitrate: Int,
    override val fileFormat: String
) : StreamInfo {
    override fun toShortIdentifierString() = if(bitrate < 1000) "${bitrate}bps" else "${bitrate/1000}kbps"

    override fun toLongIdentifierString() = "$fileFormat ${toShortIdentifierString()}"

    override fun equals(other: Any?) =
        other is AudioStreamInfo
                && other.bitrate == bitrate
                && other.fileFormat == fileFormat
}