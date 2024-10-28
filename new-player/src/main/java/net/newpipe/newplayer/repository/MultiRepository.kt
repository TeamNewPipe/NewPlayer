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

package net.newpipe.newplayer.repository

import net.newpipe.newplayer.data.NewPlayerException

/**
 * @param key is concatenated to the beginning of an **item** that is issued to a [MultiRepository]
 * this is used to identify which actual repository should handle the **item**
 * @param repository the repository that should handle that key
 */
data class MultiRepoEntry(val key: String, val repository: MediaRepository)

/**
 * This repository can be used to combine multiple MediaRepositories.
 * Be aware that the string identifying an **item** is extended by the **key** of the repository.
 * This key is specified by [MultiRepoEntry.key].
 * The string identifying an **item** called `item` would become `key:item` where repo key and
 * item are separated by a `:` character.
 * You need to take care yourself to encode the item strings accordingly when you are using a
 * MediaRepository.
 */
class MultiRepository(val actualRepositories: List<MultiRepoEntry>) : MediaRepository {

    val repos = run {
        val repos = HashMap<String, MediaRepository>()
        for (entry in actualRepositories) {
            repos[entry.key] = entry.repository
        }
        repos
    }

    private suspend fun <T> pickAndExecuteRepo(
        item: String,
        exec: suspend (MediaRepository, String) -> T
    ): T {
        val decomposedItem = item.split(":")
        val repoId = decomposedItem[0]
        val repo = repos[repoId] ?: throw NewPlayerException(
            "Could find a MediaRepository matching the item $item. Its repo key was apparently: $repoId"
        )
        return exec(repo, decomposedItem[1])
    }

    override fun getRepoInfo(): MediaRepository.RepoMetaInfo {
        var pullsDataFromNetwork = false
        var handlesTimestampLinks = true
        for (entry in actualRepositories) {
            pullsDataFromNetwork =
                entry.repository.getRepoInfo().pullsDataFromNetwork || pullsDataFromNetwork
            handlesTimestampLinks =
                entry.repository.getRepoInfo().canHandleTimestampedLinks && handlesTimestampLinks
        }

        return MediaRepository.RepoMetaInfo(
            pullsDataFromNetwork = pullsDataFromNetwork,
            canHandleTimestampedLinks = handlesTimestampLinks
        )
    }

    override suspend fun getMetaInfo(item: String) = pickAndExecuteRepo(item) { repo, item ->
        repo.getMetaInfo(item)
    }

    override suspend fun getStreams(item: String) = pickAndExecuteRepo(item) { repo, item ->
        repo.getStreams(item)
    }

    override suspend fun getSubtitles(item: String) = pickAndExecuteRepo(item) { repo, item ->
        repo.getSubtitles(item)
    }


    override suspend fun getPreviewThumbnail(item: String, timestampInMs: Long) =
        pickAndExecuteRepo(item) { repo, item ->
            repo.getPreviewThumbnail(item, timestampInMs)
        }

    override suspend fun getPreviewThumbnailsInfo(item: String) =
        pickAndExecuteRepo(item) { repo, item ->
            repo.getPreviewThumbnailsInfo(item)
        }

    override suspend fun getChapters(item: String) =
        pickAndExecuteRepo(item) { repo, item ->
            repo.getChapters(item)
        }

    override suspend fun getTimestampLink(item: String, timestampInSeconds: Long) =
        pickAndExecuteRepo(item) { repo, item ->
            repo.getTimestampLink(item, timestampInSeconds)
        }
}