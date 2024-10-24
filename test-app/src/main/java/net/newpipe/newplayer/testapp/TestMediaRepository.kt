package net.newpipe.newplayer.testapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.newpipe.newplayer.data.Chapter
import net.newpipe.newplayer.repository.MediaRepository
import net.newpipe.newplayer.repository.RepoMetaInfo
import net.newpipe.newplayer.data.AudioStreamTrack
import net.newpipe.newplayer.data.Stream
import net.newpipe.newplayer.data.Subtitle
import net.newpipe.newplayer.data.VideoStreamTrack
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class TestMediaRepository(private val context: Context) : MediaRepository {
    private val client = OkHttpClient()
    private val thumbnailCache = HashMap<String, Bitmap>()
    private val testRepoScope = CoroutineScope(Dispatchers.Main + Job())

    private fun get(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request).execute()
    }

    override fun getRepoInfo() =
        RepoMetaInfo(canHandleTimestampedLinks = true, pullsDataFromNetwork = true)

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

            "faulty" -> MediaMetadata.Builder()
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


    override suspend fun getStreams(item: String): List<Stream> {
        testRepoScope.launch {
            populateSeekPreviewThumbnailCache(item)
        }

        return when (item) {
            "6502" -> listOf(
                Stream(
                    item = "6502",
                    streamUri = Uri.parse(context.getString(R.string.ccc_6502_video)),
                    mimeType = null,
                    streamTracks = listOf(
                        AudioStreamTrack(
                            bitrate = 480000,
                            fileFormat = "MPEG4",
                            language = "en"
                        ),
                        VideoStreamTrack(
                            width = 1024,
                            height = 576,
                            frameRate = 25,
                            fileFormat = "MPEG4"
                        )
                    )
                )
            )

            "faulty" -> listOf(
                Stream(
                    item = "faulty",
                    streamUri = Uri.parse("https://kernel.org"),
                    mimeType = null,
                    streamTracks = listOf(
                        AudioStreamTrack(
                            bitrate = 480000,
                            fileFormat = "MP4A",
                            language = "en"
                        ),
                        VideoStreamTrack(
                            width = 1024,
                            height = 576,
                            frameRate = 25,
                            fileFormat = "MPEG4"
                        )
                    )
                )
            )

            "portrait" -> listOf(
                Stream(
                    item = "portrait",
                    streamUri = Uri.parse(context.getString(R.string.portrait_video_example)),
                    mimeType = null,
                    streamTracks = listOf(
                        VideoStreamTrack(
                            width = 720,
                            height = 1280,
                            frameRate = 30,
                            fileFormat = "MPEG4"
                        ),
                    AudioStreamTrack(
                        bitrate = 125000,
                        fileFormat = "MP4A"
                    )
                )
            )
            )

            "imu" -> listOf(
                Stream(
                    item = "imu",
                    streamUri = Uri.parse(context.getString(R.string.ccc_imu_1080_mp4)),
                    mimeType = null,
                    streamTracks = listOf(
                        VideoStreamTrack(
                            fileFormat = "MPEG4",
                            width = 1920,
                            height = 1080,
                            frameRate = 25
                        ),
                        VideoStreamTrack(
                            fileFormat = "MPEG4",
                            width = 1920,
                            height = 1080,
                            frameRate = 25
                        ),
                        AudioStreamTrack(
                            bitrate = 128000,
                            fileFormat = "MP4A",
                            language = "en"
                        ),
                        AudioStreamTrack(
                            bitrate = 127000,
                            fileFormat = "MP4A",
                            language = "de"
                        )
                    )
                ),

                Stream(
                    item = "imu",
                    streamUri = Uri.parse(context.getString(R.string.ccc_imu_576_mp4)),
                    mimeType = null,
                    streamTracks = listOf(
                        VideoStreamTrack(
                            fileFormat = "MPEG4",
                            width = 720,
                            height = 576,
                            frameRate = 25
                        ),
                        VideoStreamTrack(
                            fileFormat = "MPEG4",
                            width = 720,
                            height = 576,
                            frameRate = 25
                        ),
                        AudioStreamTrack(
                            bitrate = 128000,
                            fileFormat = "MP4A",
                            language = "en"
                        ),
                        AudioStreamTrack(
                            bitrate = 127000,
                            fileFormat = "MP4A",
                            language = "de"
                        )

                    )
                )
            )

            "yt_test" -> listOf(
                Stream(
                    item = "yt_test",
                    streamUri = Uri.parse(context.getString(R.string.yt_test_video_sd)),
                    mimeType = null,
                    streamTracks = listOf(
                        VideoStreamTrack(
                            width = 854,
                            height = 428,
                            frameRate = 30,
                            fileFormat = "MPEG4"
                        )
                    )
                ),

                Stream(
                    item = "yt_test",
                    streamUri = Uri.parse(context.getString(R.string.yt_test_video_hd)),
                    mimeType = null,
                    streamTracks = listOf(
                        VideoStreamTrack(
                            width = 1280,
                            height = 640,
                            frameRate = 30,
                            fileFormat = "MPEG4"
                        )
                    )
                ),
                Stream(
                    item = "yt_test",
                    streamUri = Uri.parse(context.getString(R.string.yt_test_video_fullhd)),
                    mimeType = null,
                    streamTracks = listOf(
                        VideoStreamTrack(
                            width = 1920,
                            height = 960,
                            frameRate = 30,
                            fileFormat = "MPEG4"
                        )
                    )
                ),
                Stream(
                    item = "yt_test",
                    streamUri = Uri.parse(context.getString(R.string.yt_test_audio_english)),
                    mimeType = null,
                    streamTracks = listOf(
                        AudioStreamTrack(
                            bitrate = 125000,
                            language = "en",
                            fileFormat = "MP4A"
                        )
                    )
                ),
                Stream(
                    item = "yt_test",
                    streamUri = Uri.parse(context.getString(R.string.yt_test_audio_spanish)),
                    mimeType = null,
                    streamTracks = listOf(
                        AudioStreamTrack(
                            bitrate = 125000,
                            language = "es",
                            fileFormat = "MP4A"
                        )
                    )
                )
            )


            else -> throw Exception("Unknown item: $item")
        }
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

    private suspend fun populateSeekPreviewThumbnailCache(item: String) {
        val templateUrl = when (item) {
            "6502" -> context.getString(R.string.ccc_6502_preview_thumbnails)
            "imu" -> context.getString(R.string.ccc_imu_preview_thumbnails)
            "portrait" -> return
            "yt_test" -> return
            "faulty" -> return
            else -> throw Exception("Unknown stream: $item")
        }

        val thumbCount = when (item) {
            "6502" -> 312
            "imu" -> 361
            else -> throw Exception("Unknown stream: $item")
        }

        for (i in 1..thumbCount) {
            val thumbUrl = String.format(templateUrl, i)

            if (thumbnailCache[thumbUrl] == null) {
                testRepoScope.launch {
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
                    if (bitmap != null) {
                        thumbnailCache[thumbUrl] = bitmap
                    }
                }
            }
        }
    }

    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long): Bitmap? {

        val templateUrl = when (item) {
            "6502" -> context.getString(R.string.ccc_6502_preview_thumbnails)
            "imu" -> context.getString(R.string.ccc_imu_preview_thumbnails)
            "portrait" -> return null
            "yt_test" -> return null
            "faulty" -> return null
            else -> throw Exception("Unknown stream: $item")
        }

        val thumbCount = when (item) {
            "6502" -> 312
            "imu" -> 361
            else -> throw Exception("Unknown stream: $item")
        }

        val thumbnailTimestamp = (timestampInMs / (10 * 1000)) + 1

        if (thumbCount < thumbnailTimestamp) {
            return null
        }

        val thumbUrl = String.format(templateUrl, thumbnailTimestamp)

        thumbnailCache[thumbUrl]?.let {
            return it
        }

        return null
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
}
