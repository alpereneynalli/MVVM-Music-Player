package com.player.data.repository

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.player.data.model.Category
import com.player.data.model.OnlineSong
import com.player.data.roomdb.AppDatabase
import com.player.data.roomdb.entity.FavoriteSongEntity
import com.player.data.util.FirebaseDataSource
import com.player.domain.MusicRepository

class MusicRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val firebaseDataSource: FirebaseDataSource,
    private val gson: Gson
) : MusicRepository {
    private val songListsByCategory = mutableMapOf<String, MutableList<OnlineSong>>()
    private var songCategoryList = mutableListOf<Category>()

    override suspend fun loadSongData(): Result<Unit> {
        return runCatching {
            val jsonData = firebaseDataSource.downloadJsonFile("_db_songs.json").getOrThrow()

            val jsonObject = JsonParser.parseString(jsonData).asJsonObject
            val onlineSongList = gson.fromJson<List<OnlineSong>>(
                jsonObject.getAsJsonArray("onlineSongList"),
                object : TypeToken<MutableList<OnlineSong>>() {}.type
            )

            songCategoryList = gson.fromJson(
                jsonObject.getAsJsonArray("songCategoryList"),
                object : TypeToken<MutableList<Category>>() {}.type
            )

            songListsByCategory.clear()
            for (song in onlineSongList) {
                song.setSongID()
                songListsByCategory.getOrPut(song.category) { mutableListOf() }.add(song)
            }
        }
    }

    override suspend fun getSongDownloadUri(songName: String): Result<Uri> {
        return firebaseDataSource.getDownloadUri(songName)
    }

    override fun getSongListsByCategory(): Map<String, List<OnlineSong>> {
        return songListsByCategory
    }

    override suspend fun isSongFavorite(songId: Int): Boolean {
        return appDatabase.favoriteSongDao().isSongFavorite(songId)
    }

    override suspend fun toggleFavoriteSong(songId: Int) {
        val isFavorite = isSongFavorite(songId)
        if (isFavorite) {
            appDatabase.favoriteSongDao().deleteFavoriteSong(songId)
        } else {
            appDatabase.favoriteSongDao().insertFavoriteSong(FavoriteSongEntity(songId))
        }
    }

    override suspend fun getFavoriteSongIds(): List<Int> {
        return appDatabase.favoriteSongDao().getFavoriteSongIds()
    }

    override fun getCategoryList(): List<Category> {
        return songCategoryList
    }
}

