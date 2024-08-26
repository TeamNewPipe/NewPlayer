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

package net.newpipe.newplayer.playerInternals

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.utils.Thumbnail
import kotlin.coroutines.coroutineContext

data class PlaylistItem(
    val title: String,
    val creator: String,
    val id: String,
    val thumbnail: Thumbnail?,
    val lengthInS: Int
)

suspend fun getPlaylistItemsFromItemList(items: List<String>, mediaRepo: MediaRepository) =
    with(CoroutineScope(coroutineContext)) {
        items.map { item ->
            Pair(item, async {
                mediaRepo.getMetaInfo(item)
            })
        }.map {
            val metaInfo = it.second.await()
            PlaylistItem(
                title = metaInfo.title,
                creator = metaInfo.channelName,
                id = it.first,
                thumbnail = metaInfo.thumbnail,
                lengthInS = metaInfo.lengthInS
            )
        }
    }


