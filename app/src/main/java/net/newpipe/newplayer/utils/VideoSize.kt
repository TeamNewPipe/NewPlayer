package net.newpipe.newplayer.utils

data class VideoSize(
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

    companion object {
        val DEFAULT = VideoSize(0, 0, 1F)

        fun fromMedia3VideoSize(videoSize: androidx.media3.common.VideoSize) =
            VideoSize(videoSize.width, videoSize.height, videoSize.pixelWidthHeightRatio)
    }
}

