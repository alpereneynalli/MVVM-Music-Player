package com.player.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.player.roomdb.AppDatabase
import com.player.roomdb.Category
import com.player.roomdb.DownloadedSongEntity
import com.player.roomdb.FavoriteSongEntity
import com.player.roomdb.OnlineSong
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.UUID

class AddMusicViewModel(
    private val appDatabase: AppDatabase,
    @SuppressLint("StaticFieldLeak") private val applicationContext: Context
) : ViewModel() {
    var currentExpandedItemId by mutableStateOf(-1)
    var selectedGenreSongs by mutableStateOf<List<OnlineSong>>(emptyList())

    private val gson = Gson()
    private val songListsByCategory = mutableMapOf<String, MutableList<OnlineSong>>()
    private var songCategoryList = mutableListOf<Category>()
    private var firstRowLimit = 0
    private var firstRowGenres = emptyList<Category>()
    private var secondRowGenres = emptyList<Category>()
    private var selectedGenre: String by mutableStateOf("")
    private val favoriteSongDao = appDatabase.favoriteSongDao()
    private val _downloadedSongIds = mutableStateOf<Set<Int>>(emptySet())
    val downloadedSongIds: State<Set<Int>> = _downloadedSongIds
    private val _downloadProgress = MutableLiveData<Int>()
    val downloadProgress: LiveData<Int> = _downloadProgress

    init {
        loadSongDataFromFirebase()
    }


    private suspend fun isSongDownloaded(songId: Int): Boolean {
        return appDatabase.downloadedSongDao().isSongDownloaded(songId) > 0
    }

    fun toggleDownloadedSong(songId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val isDownloaded = isSongDownloaded(songId)

            if (isDownloaded) {
                appDatabase.downloadedSongDao().deleteDownloadedSong(songId)
            } else {
                appDatabase.downloadedSongDao()
                    .insertDownloadedSong(DownloadedSongEntity(songId, true))
            }

            withContext(Dispatchers.Main) {
                // Update the Composable state after database operation
                _downloadedSongIds.value = getDownloadedSongIds().toMutableSet()
            }
        }
    }

    private suspend fun getDownloadedSongIds(): List<Int> {
        return appDatabase.downloadedSongDao().getDownloadedSongIds()
    }

    fun observeDownloadProgressAndDoSomething(workRequestId: UUID, onDownloadFinished: () -> Unit) {
        WorkManager.getInstance(applicationContext)
            .getWorkInfoByIdLiveData(workRequestId)
            .observeForever { workInfo ->
                if (workInfo != null) {
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        // Download is finished, call the function
                        viewModelScope.launch {
                            onDownloadFinished()
                        }
                    }
                    if (workInfo.state == WorkInfo.State.RUNNING) {
                        val progress = workInfo.progress.getInt(SongDownloadWorker.PROGRESS, 0)
                        _downloadProgress.postValue(progress)
                        Log.d("SONG", progress.toString())
                    }
                }
            }
    }

    fun scheduleSongDownload(songId: Int, songName: String) {
        val inputData = workDataOf(
            SongDownloadWorker.INPUT_SONG_ID to songId,
            SongDownloadWorker.INPUT_SONG_NAME to songName
        )

        val songDownloadWorkRequest =
            OneTimeWorkRequestBuilder<SongDownloadWorker>()
                .setInputData(inputData)
                .build()


        // Observe the progress for this download task
        observeDownloadProgressAndDoSomething(songDownloadWorkRequest.id) {
            // Call your function here after the download is finished
            toggleDownloadedSong(songId)
        }

        WorkManager.getInstance(applicationContext)
            .enqueue(songDownloadWorkRequest)
    }

    private fun loadSongDataFromFirebase() {
        val storageReference =
            FirebaseStorage.getInstance().getReference().child("_db_songs.json")
        Log.d("FIRE", "started")
        // Create a temporary local file to store the downloaded JSON

        val cacheDir = applicationContext.cacheDir
        val localFile = File(cacheDir, "songsTemp.json")

        storageReference.getFile(localFile)
            .addOnSuccessListener {
                Log.d("FIRE", "File downloaded successfully")

                val jsonData = FileInputStream(localFile).bufferedReader().use { it.readText() }

                localFile.delete()

                val jsonObject = JsonParser.parseString(jsonData).asJsonObject

                val onlineSongList = gson.fromJson<List<OnlineSong>>(
                    jsonObject.getAsJsonArray("onlineSongList"),
                    object : TypeToken<MutableList<OnlineSong>>() {}.type
                )

                songCategoryList = gson.fromJson(
                    jsonObject.getAsJsonArray("songCategoryList"),
                    object : TypeToken<MutableList<Category>>() {}.type
                )

                for (song in onlineSongList) {
                    song.setSongID()
                    val list = songListsByCategory.get(song.category)
                    if (list != null) {
                        list.add(song)
                    } else {
                        val newList = mutableListOf<OnlineSong>()
                        newList.add(song)
                        songListsByCategory.put(song.category, newList)
                    }
                }

                if (songCategoryList.size < 8) {
                    firstRowLimit = 4
                    firstRowGenres = songCategoryList.take(firstRowLimit)
                    secondRowGenres = songCategoryList.drop(firstRowLimit)
                } else {
                    firstRowLimit = songCategoryList.size / 2
                    firstRowGenres = songCategoryList.take(firstRowLimit)
                    secondRowGenres = songCategoryList.drop(firstRowLimit)
                }
                selectedGenre = songCategoryList.first().category
                updateSelectedGenreSongs()
                viewModelScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        val favoriteSongIds = getFavoriteSongIds()
                        _favoriteSongIds.value = favoriteSongIds.toMutableSet()
                        val downloadedSongIds = getDownloadedSongIds()
                        _downloadedSongIds.value = downloadedSongIds.toMutableSet()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the download
                Log.d("FIRE", "Error downloading JSON file: ${exception.message}")
            }
    }

    private suspend fun isSongFavorite(songId: Int): Boolean {
        return favoriteSongDao.isSongFavorite(songId)
    }

    private suspend fun getFavoriteSongIds(): List<Int> {
        return favoriteSongDao.getFavoriteSongIds()
    }

    private val _favoriteSongIds = mutableStateOf<Set<Int>>(emptySet())
    val favoriteSongIds: State<Set<Int>> = _favoriteSongIds


    fun toggleFavorite(songId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFavorite = isSongFavorite(songId)

            if (isFavorite) {
                favoriteSongDao.deleteFavoriteSong(songId)
            } else {
                Log.d("DB", "inserted")
                favoriteSongDao.insertFavoriteSong(FavoriteSongEntity(songId))
            }

            withContext(Dispatchers.Main) {
                // Update the Composable state after database operation
                _favoriteSongIds.value = getFavoriteSongIds().toMutableSet()
            }
        }
    }

    fun getSelectedGenreName(): String {
        return selectedGenre
    }

    private fun updateSelectedGenreSongs() {
        selectedGenreSongs = songListsByCategory[selectedGenre] ?: emptyList()
    }

    fun setSelectedGenreName(genre: String) {
        selectedGenre = genre
        updateSelectedGenreSongs()
    }

    fun getFirstRowGenreList(): List<Category> {
        return firstRowGenres
    }


    fun getSecondRowGenreList(): List<Category> {
        return secondRowGenres
    }

    fun getSongListsByCategory(): List<OnlineSong> {
        return songListsByCategory.flatMap { it.value }
    }
}