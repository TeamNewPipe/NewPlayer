package net.newpipe.newplayer.testapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.newpipe.newplayer.Chapter
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.RepoMetaInfo
import net.newpipe.newplayer.Stream
import net.newpipe.newplayer.StreamType
import net.newpipe.newplayer.Subtitle
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class TestMediaRepository(private val context: Context) : MediaRepository {
    private val client = OkHttpClient()
    private val thumbnailCache = HashMap<String, Bitmap>()

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

            "yt_test" -> MediaMetadata.Builder()
                .setTitle(context.getString(R.string.yt_test_title))
                .setArtist(context.getString(R.string.yt_test_channel))
                .setArtworkUri(null)
                .setDurationMs(
                    context.resources.getInteger(R.integer.yt_test_length).toLong() * 1000L
                )
                .build()


            else -> throw Exception("Unknown stream: $item")
        }


    override suspend fun getStreams(item: String) =
        when (item) {
            "6502" -> listOf(
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.ccc_6502_video)),
                    mimeType = null,
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = "Deutsch",
                    identifier = "576p",
                )
            )

            "portrait" -> listOf(
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.portrait_video_example)),
                    mimeType = null,
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = null,
                    identifier = "720p",
                )
            )

            "imu" -> listOf(
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.ccc_imu_1080_mp4)),
                    mimeType = null,
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = null,
                    identifier = "1080p",
                ),

                Stream(
                    streamUri = Uri.parse(context.getString(R.string.ccc_imu_576_mp4)),
                    mimeType = null,
                    streamType = StreamType.AUDIO_AND_VIDEO,
                    language = null,
                    identifier = "576p"
                )
            )

            "yt_test" -> listOf(
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.yt_test_video_sd)),
                    mimeType = null,
                    streamType = StreamType.VIDEO,
                    language = null,
                    identifier = "SD",
                ),

                Stream(
                    streamUri = Uri.parse(context.getString(R.string.yt_test_video_hd)),
                    mimeType = null,
                    streamType = StreamType.VIDEO,
                    language = null,
                    identifier = "HD",
                ),
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.yt_test_video_fullhd)),
                    mimeType = null,
                    streamType = StreamType.VIDEO,
                    language = null,
                    identifier = "FullHD",
                ),
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.yt_test_audio_english)),
                    mimeType = null,
                    streamType = StreamType.AUDIO,
                    language = "English",
                    identifier = "default audio",
                ),
                Stream(
                    streamUri = Uri.parse(context.getString(R.string.yt_test_audio_spanish)),
                    mimeType = null,
                    streamType = StreamType.AUDIO,
                    language = "Spanish",
                    identifier = "default audio",
                )
            )


            else -> throw Exception("Unknown item: $item")
        }


    override suspend fun getSubtitles(item: String) =
        when (item) {
            "imu" -> listOf(
                Subtitle(
                    Uri.parse(context.getString(R.string.ccc_imu_subtitles)),
                    "english"
                )
            )

            else -> emptyList()
        }


    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long): Bitmap? {

        val templateUrl = when (item) {
            "6502" -> context.getString(R.string.ccc_6502_preview_thumbnails)
            "imu" -> context.getString(R.string.ccc_imu_preview_thumbnails)
            "portrait" -> null
            "ty_test" -> null
            else -> throw Exception("Unknown stream: $item")
        }

        val thumbCount = when (item) {
            "6502" -> 312
            "imu" -> 361
            else -> throw Exception("Unknown stream: $item")
        }

        //val thumbnailTimestamp = (timestampInMs / (10 * 1000)) + 1
        val thumbnailTimestamp = 20
        if (thumbCount < thumbnailTimestamp) {
            return null
        }

        if(templateUrl == null)
            return null

        val thumbUrl = String.format(templateUrl, thumbnailTimestamp)

        thumbnailCache[thumbUrl]?.let {
            return it
        }

        val bitmap = withContext(Dispatchers.IO) {
            val request = Request.Builder().url(thumbUrl).build()
            val response = client.newCall(request).execute()
            try {
                val responseBody = response.body
                val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                return@withContext bitmap
            } catch (e: Exception) {
                return@withContext null
            }
        }
        if(bitmap != null) {
            thumbnailCache[thumbUrl] = bitmap
        }

        return bitmap
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