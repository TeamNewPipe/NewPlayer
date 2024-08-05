package net.newpipe.newplayer.testapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.newpipe.newplayer.MediaRepository
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

    override suspend fun getTitle(item: String) =
        when (item) {
            "6502" -> "Reverse engineering the MOS 6502"
            "portrait" -> "Imitating generative AI videos"
            "imu" -> "Intel Management Engine deep dive "
            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getChannelName(item: String) =
        when (item) {
            "6502" -> "27c3"
            "portrait" -> "无所吊谓～"
            "imu" -> "36c3"
            else -> throw Exception("Unknown stream: $item")
        }

    override suspend fun getThumbnail(item: String) =
        when (item) {
            "6502" -> withContext(Dispatchers.IO) {
                val response =
                    get("https://static.media.ccc.de/media/congress/2010/27c3-4159-en-reverse_engineering_mos_6502_preview.jpg")

                BitmapFactory.decodeStream(response.body.byteStream())
            }


            "portrait" -> withContext(Dispatchers.IO) {
                val response =
                    get("https://64.media.tumblr.com/13f7e4065b4c583573a9a3e40750ccf8/9e8cf97a92704864-4b/s540x810/d966c97f755384b46dbe6d5350d35d0e9d4128ad.jpg")

                BitmapFactory.decodeStream(response.body.byteStream())
            }

            "imu" -> withContext(Dispatchers.IO) {
                val response =
                    get("https://static.media.ccc.de/media/congress/2019/10694-hd_preview.jpg")

                BitmapFactory.decodeStream(response.body.byteStream())
            }

            else -> throw Exception("Unknown stream: $item")
        }


    override suspend fun getAvailableStreams(item: String): List<String> =
        when (item) {
            "6502" -> listOf("576p")
            "portrait" -> listOf("720p")
            "imu" -> listOf("1080p", "576p")
            else -> throw Exception("Unknown stream: $item")
        }


    override suspend fun getStream(item: String, streamSelector: String) =
        MediaItem.fromUri(
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

    override suspend fun getLinkWithStreamOffset(item: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun getPreviewThumbnails(item: String): List<Bitmap> {
        TODO("Not yet implemented")
    }

    override suspend fun getChapters(item: String): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getChapterThumbnail(item: String, chapter: Long): Bitmap {
        TODO("Not yet implemented")
    }
}