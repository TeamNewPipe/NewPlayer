package net.newpipe.newplayer.testapp

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.RepoMetaInfo
import net.newpipe.newplayer.Stream
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.StreamVariant
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class TestMediaRepository(val context: Context) : MediaRepository {
    val client = OkHttpClient()

    private fun get(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request).execute()
    }

    override fun getRepoInfo() =
        RepoMetaInfo(canHandleTimestampedLinks = true, pullsDataFromNetwrok = true)

    @OptIn(UnstableApi::class)
    override suspend fun getMetaInfo(item: String): MediaMetadata =
        when (item) {
            "6502" -> MediaMetadata.Builder()
                .setTitle(context.getString(R.string.ccc_6502_title))
                .setArtist(context.getString(R.string.ccc_6502_channel))
                .setArtworkUri(Uri.parse(context.getString(R.string.ccc_6502_thumbnail)))
                .setDurationMs(
                    context.resources.getInteger(R.integer.ccc_6502_length).toLong() * 1000L
                )
                .build()

            "imu" -> MediaMetadata.Builder()
                .setTitle(context.getString(R.string.ccc_imu_title))
                .setArtist(context.getString(R.string.ccc_imu_channel))
                .setArtworkUri(Uri.parse(context.getString(R.string.ccc_imu_thumbnail)))
                .setDurationMs(
                    context.resources.getInteger(R.integer.ccc_imu_length).toLong() * 1000L
                )
                .build()

            "portrait" -> MediaMetadata.Builder()
                .setTitle(context.getString(R.string.portrait_title))
                .setArtist(context.getString(R.string.portrait_channel))
                .setArtworkUri(null)
                .setDurationMs(
                    context.resources.getInteger(R.integer.portrait_length).toLong() * 1000L
                )
                .build()

            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getAvailableStreamVariants(item: String): List<StreamVariant> =
        when (item) {
            "6502" -> listOf(
                StreamVariant(
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = "Deutsch",
                    streamVariantIdentifier = "576p",
                ),
            )

            "portrait" -> listOf(
                StreamVariant(
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = null,
                    streamVariantIdentifier = "720p",
                ),
            )

            "imu" -> listOf(
                StreamVariant(
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = "Deutsch",
                    streamVariantIdentifier = "1080p",
                ),
                StreamVariant(
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = "Deutsch",
                    streamVariantIdentifier = "576p",
                )
            )

            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getAvailableSubtitleVariants(item: String): List<String> {
        TODO("Not yet implemented")
    }


    override suspend fun getStream(item: String, streamVariantSelector: StreamVariant) =
        when (item) {
            "6502" -> Stream(
                streamUri = Uri.parse(context.getString(R.string.ccc_6502_video)),
                mimeType = null
            )

            "portrait" -> Stream(
                streamUri = Uri.parse(context.getString(R.string.portrait_video_example)),
                mimeType = null
            )

            "imu" -> when (streamVariantSelector.streamVariantIdentifier) {
                "1080p" -> Stream(
                    streamUri = Uri.parse(context.getString(R.string.ccc_imu_1080_mp4)),
                    mimeType = null
                )

                "576p" -> Stream(
                    streamUri = Uri.parse(context.getString(R.string.ccc_imu_576_mp4)),
                    mimeType = null
                )

                else -> throw Exception("Unknown stream selector for $item: $streamVariantSelector")
            }

            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getSubtitle(item: String, variant: String) =
        Uri.parse(
            when (item) {
                "imu" -> context.getString(R.string.ccc_imu_subtitles)
                else -> ""
            }
        )

    override suspend fun getPreviewThumbnails(item: String): HashMap<Long, Uri>? {
        val templateUrl = when (item) {
            "6502" -> context.getString(R.string.ccc_6502_preview_thumbnails)
            "imu" -> context.getString(R.string.ccc_imu_preview_thumbnails)
            "portrait" -> null
            else -> throw Exception("Unknown stream: $item")
        }

        if (templateUrl != null) {
            val thumbCount = when (item) {
                "6502" -> 312
                "imu" -> 361
                else -> throw Exception("Unknown stream: $item")
            }

            var thumbMap = HashMap<Long, Uri>()

            for (i in 1..thumbCount) {
                val timeStamp = (i - 1) * 10 * 1000
                thumbMap.put(timeStamp.toLong(), Uri.parse(String.format(templateUrl, i)))
            }

            return thumbMap
        } else {
            return null
        }
    }

    override suspend fun getChapters(item: String) =
        when (item) {
            "6502" -> context.resources.getIntArray(R.array.ccc_6502_chapters)
            "imu" -> context.resources.getIntArray(R.array.ccc_imu_chapters)
            else -> intArrayOf()
        }.map {
            Chapter(
                it.toLong(), chapterTitle = "Dummy Chapter at timestamp $it",
                thumbnail = when (item) {
                    "6502" -> Uri.parse(
                        String.format(
                            context.getString(R.string.ccc_6502_preview_thumbnails),
                            it / (10 * 1000)
                        )
                    )

                    "imu" -> Uri.parse(
                        String.format(
                            context.getString(R.string.ccc_imu_preview_thumbnails),
                            it / (10 * 1000)
                        )
                    )

                    else -> null
                }
            )
        }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) =
        when (item) {
            "6502" -> "${context.getString(R.string.ccc_6502_link)}#t=$timestampInSeconds"
            "imu" -> "${context.getString(R.string.ccc_imu_link)}#t=$timestampInSeconds"
            else -> ""
        }


    override suspend fun tryAndRescueError(item: String?, exception: PlaybackException): Uri? {
        TODO("Not yet implemented")
    }
}