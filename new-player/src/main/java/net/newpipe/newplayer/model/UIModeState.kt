package net.newpipe.newplayer.model

enum class UIModeState {
    PLACEHOLDER,

    EMBEDDED_VIDEO,
    EMBEDDED_VIDEO_CONTROLLER_UI,
    EMBEDDED_VIDEO_CHAPTER_SELECT,
    EMBEDDED_VIDEO_STREAM_SELECT,

    FULLSCREEN_VIDEO,
    FULLSCREEN_VIDEO_CONTROLLER_UI,
    FULLSCREEN_VIDEO_CHAPTER_SELECT,
    FULLSCREEN_VIDEO_STREAM_SELECT;

    val fullscreen: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CHAPTER_SELECT -> true
                EMBEDDED_VIDEO_STREAM_SELECT -> true
                FULLSCREEN_VIDEO -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CHAPTER_SELECT -> true
                FULLSCREEN_VIDEO_STREAM_SELECT -> true
                else -> false
            }

    val controllerUiVisible: Boolean
        get() =
            when (this) {
                EMBEDDED_VIDEO_CONTROLLER_UI -> true
                FULLSCREEN_VIDEO_CONTROLLER_UI -> true
                else -> false
            }

    val systemUiVisible: Boolean
        get() =
            when (this) {
                FULLSCREEN_VIDEO -> false
                else -> true
            }

    // STATE TRANSITIONS

    fun getControllerUiVisibleState() =
        when (this) {
            EMBEDDED_VIDEO -> EMBEDDED_VIDEO_CONTROLLER_UI
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO_CONTROLLER_UI

            else -> this
        }


    fun getUiHiddenState() =
        when (this) {
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO
            FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO
            FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO
            FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO


            EMBEDDED_VIDEO -> EMBEDDED_VIDEO
            EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO
            EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO
            EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO

            else -> this
        }


    fun getStreamSelectUiState() =
        when (this) {
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO_STREAM_SELECT
            FULLSCREEN_VIDEO_CHAPTER_SELECT -> FULLSCREEN_VIDEO_STREAM_SELECT
            FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO_STREAM_SELECT

            EMBEDDED_VIDEO -> EMBEDDED_VIDEO_STREAM_SELECT
            EMBEDDED_VIDEO_CHAPTER_SELECT -> EMBEDDED_VIDEO_STREAM_SELECT
            EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO_STREAM_SELECT

            else -> this
        }

    fun getChapterSelectUiState() =
        when (this) {
            FULLSCREEN_VIDEO -> FULLSCREEN_VIDEO_CHAPTER_SELECT
            FULLSCREEN_VIDEO_STREAM_SELECT -> FULLSCREEN_VIDEO_CHAPTER_SELECT
            FULLSCREEN_VIDEO_CONTROLLER_UI -> FULLSCREEN_VIDEO_CHAPTER_SELECT

            EMBEDDED_VIDEO -> EMBEDDED_VIDEO_CHAPTER_SELECT
            EMBEDDED_VIDEO_STREAM_SELECT -> EMBEDDED_VIDEO_CHAPTER_SELECT
            EMBEDDED_VIDEO_CONTROLLER_UI -> EMBEDDED_VIDEO_CHAPTER_SELECT

            else -> this
        }
}