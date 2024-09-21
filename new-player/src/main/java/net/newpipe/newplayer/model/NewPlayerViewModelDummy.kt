package net.newpipe.newplayer.model

import android.os.Bundle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.newpipe.newplayer.NewPlayer
import net.newpipe.newplayer.ui.ContentScale

open class NewPlayerViewModelDummy : NewPlayerViewModel {
    override var newPlayer: NewPlayer? = null
    override val uiState = MutableStateFlow(NewPlayerUIState.DEFAULT)
    override var minContentRatio = 4F / 3F
    override var maxContentRatio = 16F / 9F
    override var contentFitMode = ContentScale.FIT_INSIDE
    override val embeddedPlayerDraggedDownBy = MutableSharedFlow<Float>().asSharedFlow()
    override val onBackPressed: SharedFlow<Unit> = MutableSharedFlow<Unit>().asSharedFlow()

    override fun initUIState(instanceState: Bundle) {
        println("dummy impl")
    }

    override fun play() {
        println("dummy impl")
    }

    override fun switchToEmbeddedView() {
        println("dummy impl")
    }

    override fun onBackPressed() {
        println("dummy impl")
    }

    override fun switchToFullscreen(embeddedUiConfig: EmbeddedUiConfig) {
        println("dummy impl")
    }

    override fun showUi() {
        println("dummy impl")
    }

    override fun hideUi() {
        println("dummy impl")
    }

    override fun seekPositionChanged(newValue: Float) {
        println("dymmy seekPositionChanged: newValue: ${newValue}")
    }

    override fun seekingFinished() {
        println("dummy impl")
    }

    override fun embeddedDraggedDown(offset: Float) {
        println("dymmy embeddedDraggedDown: offset: ${offset}")
    }

    override fun fastSeek(steps: Int) {
        println("dummy impl: steps: $steps")
    }

    override fun finishFastSeek() {
        println("dummy impl")
    }

    override fun brightnessChange(changeRate: Float, systemBrightness: Float) {
        println("dummy impl")
    }

    override fun volumeChange(changeRate: Float) {
        println("dummy impl")
    }

    override fun openStreamSelection(selectChapter: Boolean, embeddedUiConfig: EmbeddedUiConfig) {
        println("dummy impl")
    }

    override fun closeStreamSelection() {
        println("dummy impl")
    }

    override fun chapterSelected(chapterId: Int) {
        println("dummp impl chapter selected: $chapterId")
    }

    override fun streamSelected(streamId: Int) {
        println("dummy impl stream selected: $streamId")
    }

    override fun cycleRepeatMode() {
        println("dummy impl")
    }

    override fun toggleShuffle() {
        println("dummy impl")
    }

    override fun onStorePlaylist() {
        println("dummy impl")
    }

    override fun movePlaylistItem(from: Int, to: Int) {
        println("dummy impl")
    }

    override fun removePlaylistItem(uniqueId: Long) {
        println("dummy impl delete uniqueId: $uniqueId")
    }

    override fun onStreamItemDragFinished() {
        println("dummy impl")
    }

    override fun dialogVisible(visible: Boolean) {
        println("dummy impl dialog visible: $visible")
    }

    override fun pause() {
        println("dummy pause")
    }

    override fun prevStream() {
        println("dummy impl")
    }

    override fun nextStream() {
        println("dummy impl")
    }
}