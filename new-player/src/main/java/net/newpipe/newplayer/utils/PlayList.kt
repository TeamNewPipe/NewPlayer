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
 *
 */

package net.newpipe.newplayer.utils

import androidx.media3.common.Player


// TODO: This is cool, but it might still contains all raceconditions since two actors are mutating the
// same time.
// Be aware when using this list and this iterator: There can alwas be out of bounds exceptions
// even if the size in a previous query said otherwise, since between the size query and
// a get element query the count of elements might have been changed by exoplayer itself
// due to this reason some functions force the user to handle elements out of bounds exceptions.


class PlayListIterator(
    val exoPlayer: Player,
    val fromIndex: Int,
    val toIndex: Int
) : ListIterator<String> {

    var index = fromIndex

    override fun hasNext() =
        index < minOf(exoPlayer.mediaItemCount, toIndex)

    override fun hasPrevious() = fromIndex < index

    @Throws(IndexOutOfBoundsException::class)
    override fun next(): String {
        if (exoPlayer.mediaItemCount <= index)
            throw NoSuchElementException("No Stream with index $index in the playlist")
        val item = exoPlayer.getMediaItemAt(index).mediaId
        index++
        return item
    }

    @Throws(IndexOutOfBoundsException::class)
    override fun nextIndex() =
        if (exoPlayer.mediaItemCount <= index)
            exoPlayer.mediaItemCount - fromIndex
        else
            (index + 1) - fromIndex

    @Throws(IndexOutOfBoundsException::class)
    override fun previous(): String {
        if (index <= fromIndex)
            throw NoSuchElementException("No Stream with index ${index - 1} in the playlist")
        index--
        val item = exoPlayer.getMediaItemAt(index).mediaId
        return item
    }

    override fun previousIndex() =
        if (index <= fromIndex)
            0
        else
            (index - 1) - fromIndex
}

class PlayList(val exoPlayer: Player, val fromIndex: Int = 0, val toIndex: Int = Int.MAX_VALUE) :
    List<String> {

    override val size: Int
        get() = minOf(exoPlayer.mediaItemCount, toIndex) - fromIndex

    override fun contains(element: String): Boolean {
        for (i in fromIndex..minOf(exoPlayer.mediaItemCount, toIndex)) {
            try {
                if (exoPlayer.getMediaItemAt(i).mediaId == element)
                    return true
            } catch (e: IndexOutOfBoundsException) {
                return false
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        for (element in elements) {
            if (!this.contains(element)) {
                return false
            }
        }
        return true
    }

    @Throws(IndexOutOfBoundsException::class)
    override fun get(index: Int) =
        if (index < fromIndex || toIndex < index)
            throw IndexOutOfBoundsException("Accessed playlist item outside of permitted Playlist ListWindow: $index")
        else
            exoPlayer.getMediaItemAt(index).mediaId

    override fun isEmpty() = exoPlayer.mediaItemCount == 0 || fromIndex == toIndex

    override fun iterator() = PlayListIterator(exoPlayer, fromIndex, toIndex)

    override fun listIterator() = PlayListIterator(exoPlayer, fromIndex, toIndex)

    override fun listIterator(index: Int) = PlayListIterator(exoPlayer, index, toIndex)

    override fun subList(fromIndex: Int, toIndex: Int): List<String> =
        PlayList(
            exoPlayer,
            fromIndex = this.fromIndex + fromIndex,
            toIndex = this.fromIndex + toIndex
        )

    override fun lastIndexOf(element: String): Int {
        for (i in minOf(toIndex, exoPlayer.mediaItemCount) downTo fromIndex) {
            try {
                if (exoPlayer.getMediaItemAt(i).mediaId == element) {
                    return i - fromIndex
                }
            } catch (e: IndexOutOfBoundsException) {
                return -1
            }
        }
        return -1
    }

    override fun indexOf(element: String): Int {
        for (i in fromIndex.. minOf(toIndex, exoPlayer.mediaItemCount)) {
            try {
                if (exoPlayer.getMediaItemAt(i).mediaId == element) {
                    return i - fromIndex
                }
            } catch (e: IndexOutOfBoundsException) {
                return -1
            }
        }
        return -1
    }
}