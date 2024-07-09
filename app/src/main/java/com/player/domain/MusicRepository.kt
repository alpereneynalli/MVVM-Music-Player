package com.player.domain

import android.net.Uri
import com.player.data.model.Category
import com.player.data.model.OnlineSong

interface MusicRepository {
    suspend fun loadSongData(): Result<Unit>
    suspend fun getSongDownloadUri(songName: String): Result<Uri>
    fun getSongListsByCategory(): Map<String, List<OnlineSong>>
    suspend fun isSongFavorite(songId: Int): Boolean
    suspend fun toggleFavoriteSong(songId: Int)
    suspend fun getFavoriteSongIds(): List<Int>
    fun getCategoryList(): List<Category>
}