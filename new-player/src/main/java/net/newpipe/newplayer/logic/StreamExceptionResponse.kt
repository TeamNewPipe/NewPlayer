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
 */

package net.newpipe.newplayer.logic;

import net.newpipe.newplayer.data.StreamSelection
import net.newpipe.newplayer.NewPlayer

interface StreamExceptionResponse

/***
 * Perform a specific action, like halting the playback or etc.
 */
data class ActionResponse(val action: () -> Unit) : StreamExceptionResponse

/**
 * Tell NewPlayer with which stream selection of the current item the currently failing stream
 * selection should be replaced with. This can be used if only individual streams or tracks
 * corresponding to one item fail.
 *
 * If you want to reload the current item due to a timeout issue, or you want to replace the current
 * item all together, you should instead respond with [ReplaceItemResponse].
 */
data class ReplaceStreamSelectionResponse(val streamSelection: StreamSelection) :
    StreamExceptionResponse

/**
 * Tell NewPlayer with which item the currently failing item should be replaced with.
 * NewPlayer will continue to playback the new item at the position the last item failed.
 * The mew item will also be inserted in the same playlist position as the failing item was.
 *
 * This can also be used to just reload the currently failing item. Just supply the same item as
 * the currently failing item. NewPlayer will call the repository and ask for new streams for that
 * item. This can be used if stream urls get deprecated due to a timeout.
 *
 * If you don't want the whole item to be replaced/reloaded you should instead respond with a
 * [ReplaceStreamSelectionResponse].
 */
data class ReplaceItemResponse(val newItem: String) : StreamExceptionResponse

/**
 * Don't do anything to recover from the fail and forward the exception to [NewPlayer.errorFlow]
 */
class NoResponse : StreamExceptionResponse
