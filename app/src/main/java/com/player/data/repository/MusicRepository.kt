package com.player.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.player.data.roomdb.AppDatabase
import com.player.data.model.Category
import com.player.data.roomdb.entity.DownloadedSongEntity
import com.player.data.roomdb.entity.FavoriteSongEntity
import com.player.data.model.OnlineSong
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

class MusicRepository(
    private val appDatabase: AppDatabase,
    private val storage: FirebaseStorage,
    private val applicationContext: Context
) {
    private val gson = Gson()
    private val songListsByCategory = mutableMapOf<String, MutableList<OnlineSong>>()
    private var songCategoryList = mutableListOf<Category>()

    suspend fun isSongDownloaded(songId: Int): Boolean {
        return appDatabase.downloadedSongDao().isSongDownloaded(songId) > 0
    }

    suspend fun toggleDownloadedSong(songId: Int) {
        val isDownloaded = isSongDownloaded(songId)
        if (isDownloaded) {
            appDatabase.downloadedSongDao().deleteDownloadedSong(songId)
        } else {
            appDatabase.downloadedSongDao().insertDownloadedSong(DownloadedSongEntity(songId, true))
        }
    }

    suspend fun getDownloadedSongIds(): List<Int> {
        return appDatabase.downloadedSongDao().getDownloadedSongIds()
    }

    suspend fun loadSongDataFromFirebase(): Result<Unit> {
        return runCatching {
            Log.d("FIRE", "fetch started")

            // Attempt to download the file
            val storageReference = storage.getReference().child("_db_songs.json")
            val cacheDir = applicationContext.cacheDir
            val localFile = File(cacheDir, "songsTemp.json")

            try {
                storageReference.getFile(localFile).await()
                Log.d("FIRE", "File downloaded successfully")
            } catch (e: Exception) {
                Log.e("FIRE", "File download failed: ${e.message}", e)
                throw e
            }

            // Attempt to read JSON data from the file
            val jsonData: String
            try {
                jsonData = FileInputStream(localFile).bufferedReader().use { it.readText() }
                localFile.delete()
                Log.d("FIRE", "JSON data: $jsonData")
            } catch (e: Exception) {
                Log.e("FIRE", "Reading JSON data failed: ${e.message}", e)
                throw e
            }

            // Attempt to parse JSON data
            try {
                val jsonObject = JsonParser.parseString(jsonData).asJsonObject
                val onlineSongList = gson.fromJson<List<OnlineSong>>(
                    jsonObject.getAsJsonArray("onlineSongList"),
                    object : TypeToken<MutableList<OnlineSong>>() {}.type
                )
                Log.d("FIRE", "Parsed onlineSongList: $onlineSongList")

                songCategoryList = gson.fromJson(
                    jsonObject.getAsJsonArray("songCategoryList"),
                    object : TypeToken<MutableList<Category>>() {}.type
                )
                Log.d("FIRE", "Parsed songCategoryList: $songCategoryList")

                for (song in onlineSongList) {
                    song.setSongID()
                    val list = songListsByCategory[song.category]
                    if (list != null) {
                        list.add(song)
                    } else {
                        val newList = mutableListOf<OnlineSong>()
                        newList.add(song)
                        songListsByCategory[song.category] = newList
                    }
                }
                Log.d("FIRE", "Updated songListsByCategory: $songListsByCategory")
            } catch (e: Exception) {
                Log.e("FIRE", "Parsing JSON data failed: ${e.message}", e)
                throw e
            }

            Unit
        }.onFailure {
            Log.e("FIRE", "Error fetching songs: ${it.message}", it)
        }
    }

    fun getSongListsByCategory(): Map<String, List<OnlineSong>> {
        return songListsByCategory
    }

    suspend fun isSongFavorite(songId: Int): Boolean {
        return appDatabase.favoriteSongDao().isSongFavorite(songId)
    }

    suspend fun toggleFavoriteSong(songId: Int) {
        val isFavorite = isSongFavorite(songId)
        if (isFavorite) {
            appDatabase.favoriteSongDao().deleteFavoriteSong(songId)
        } else {
            appDatabase.favoriteSongDao().insertFavoriteSong(FavoriteSongEntity(songId))
        }
    }

    suspend fun getFavoriteSongIds(): List<Int> {
        return appDatabase.favoriteSongDao().getFavoriteSongIds()
    }

    fun getCategoryList(): List<Category> {
        return songCategoryList
    }
}

