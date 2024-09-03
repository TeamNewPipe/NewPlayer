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

import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import net.newpipe.newplayer.MediaRepository
import net.newpipe.newplayer.NewPlayerException
import net.newpipe.newplayer.utils.Thumbnail
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

data class PlaylistItem(
    val title: String,
    val creator: String,
    val id: String,
    val uniqueId: Long,
    val thumbnail: Thumbnail?,
    val lengthInS: Int
)

suspend fun getPlaylistItemsFromExoplayer(player: Player, mediaRepo: MediaRepository, idLookupTable: HashMap<Long, String>) =
    with(CoroutineScope(coroutineContext)) {
        (0..player.mediaItemCount-1).map { index ->
            println("gurken index: $index")
            val mediaItem = player.getMediaItemAt(index)
            val uniqueId = mediaItem.mediaId.toLong()
            val id = idLookupTable.get(uniqueId)
                ?: throw NewPlayerException("Unknown uniqueId: $uniqueId, uniqueId Id mapping error. Something went wrong during datafetching.")
            Pair(uniqueId, id)
        }.map { item ->
            Pair(item, async {
                mediaRepo.getMetaInfo(item.second)
            })
        }.map {
            val uniqueId = it.first.first
            val id = it.first.second
            val metaInfo = it.second.await()
            PlaylistItem(
                title = metaInfo.title,
                creator = metaInfo.channelName,
                id = id,
                thumbnail = metaInfo.thumbnail,
                lengthInS = metaInfo.lengthInS,
                uniqueId = uniqueId
            )
        }
    }

fun getPlaylistDurationInS(items: List<PlaylistItem>) : Int {
    var duration = 0
    for(item in items) {
        duration += item.lengthInS
    }
    return duration
}