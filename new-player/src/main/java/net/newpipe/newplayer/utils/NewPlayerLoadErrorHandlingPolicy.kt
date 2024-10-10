package net.newpipe.newplayer.utils

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import net.newpipe.newplayer.NewPlayer

@UnstableApi
class NewPlayerLoadErrorHandlingPolicy(val exceptionHandler: (Exception) -> Unit) :
    DefaultLoadErrorHandlingPolicy() {
    override fun getFallbackSelectionFor(
        fallbackOptions: LoadErrorHandlingPolicy.FallbackOptions,
        loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo
    ): LoadErrorHandlingPolicy.FallbackSelection? {
        val fallbackSelection = super.getFallbackSelectionFor(fallbackOptions, loadErrorInfo)
        exceptionHandler(loadErrorInfo.exception)
        return fallbackSelection
    }
}