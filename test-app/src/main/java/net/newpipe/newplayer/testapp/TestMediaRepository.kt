package net.newpipe.newplayer.testapp

import android.content.Context
import android.net.Uri
import androidx.media3.common.PlaybackException
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.MetaInfo
import net.newpipe.newplayer.utils.OnlineThumbnail
import net.newpipe.newplayer.utils.Thumbnail
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

    override suspend fun getMetaInfo(item: String) =
        when (item) {
            "6502" -> MetaInfo(
                title = context.getString(R.string.ccc_6502_title),
                channelName = context.getString(R.string.ccc_6502_channel),
                thumbnail = OnlineThumbnail(context.getString(R.string.ccc_6502_thumbnail)),
                lengthInS = context.resources.getInteger(R.integer.ccc_6502_length)
            )
            "imu" -> MetaInfo(
                title = context.getString(R.string.ccc_imu_title),
                channelName = context.getString(R.string.ccc_imu_channel),
                thumbnail = OnlineThumbnail(context.getString(R.string.ccc_imu_thumbnail)),
                lengthInS = context.resources.getInteger(R.integer.ccc_imu_length)
            )
            "portrait" -> MetaInfo(
                title = context.getString(R.string.portrait_title),
                channelName = context.getString(R.string.portrait_channel),
                thumbnail = null,
                lengthInS = context.resources.getInteger(R.integer.portrait_length)
            )

            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getAvailableStreamVariants(item: String): List<String> =
        when (item) {
            "6502" -> listOf("576p")
            "portrait" -> listOf("720p")
            "imu" -> listOf("1080p", "576p")
            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getAvailableSubtitleVariants(item: String): List<String> {
        TODO("Not yet implemented")
    }


    override suspend fun getStream(item: String, streamSelector: String) =
        Uri.parse(
            when (item) {
                "6502" -> context.getString(R.string.ccc_6502_video)
                "portrait" -> context.getString(R.string.portrait_video_example)
                "imu" -> when (streamSelector) {
                    "1080p" -> context.getString(R.string.ccc_imu_1080_mp4)
                    "576p" -> context.getString(R.string.ccc_imu_576_mp4)
                    else -> throw Exception("Unknown stream selector for $item: $streamSelector")
                }

                else -> throw Exception("Unknown stream: $item")
            }
        )

    override suspend fun getSubtitle(item: String, variant: String) =
        Uri.parse(
            when (item) {
                "imu" -> context.getString(R.string.ccc_imu_subtitles)
                else -> ""
            }
        )

    override suspend fun getPreviewThumbnails(item: String): HashMap<Long, Thumbnail>? {
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

            var thumbMap = HashMap<Long, Thumbnail>()

            for (i in 1..thumbCount) {
                val timeStamp = (i - 1) * 10 * 1000
                thumbMap.put(timeStamp.toLong(), OnlineThumbnail(String.format(templateUrl, i)))
            }

            return thumbMap
        } else {
            return null
        }
    }

    override suspend fun getChapters(item: String) =
        when (item) {
            "6502" -> context.resources.getIntArray(R.array.ccc_6502_chapters)
            "imu" -> TODO()
            else -> intArrayOf()
        }.map {
            Chapter(it.toLong(), "Dummy Chapter at timestamp $it")
        }

    override suspend fun getChapterThumbnail(item: String, chapter: Long) =
        when (item) {
            "6502" -> OnlineThumbnail(
                String.format(
                    context.getString(R.string.ccc_6502_preview_thumbnails),
                    chapter / (10 * 1000)
                )
            )

            "imu" -> OnlineThumbnail(
                String.format(
                    context.getString(R.string.ccc_imu_preview_thumbnails),
                    chapter / (10 * 1000)
                )
            )

            else -> null
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