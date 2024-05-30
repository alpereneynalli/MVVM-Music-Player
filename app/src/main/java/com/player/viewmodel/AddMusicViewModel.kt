package com.player.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.repository.MusicRepository
import com.player.roomdb.Category
import com.player.roomdb.OnlineSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMusicViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    var currentExpandedItemId by mutableStateOf(-1)
    var selectedGenreSongs by mutableStateOf<List<OnlineSong>>(emptyList())

    private var firstRowGenres = emptyList<Category>()
    private var secondRowGenres = emptyList<Category>()
    private var selectedGenre: String by mutableStateOf("")
    private val _downloadedSongIds = mutableStateOf<Set<Int>>(emptySet())
    val downloadedSongIds: State<Set<Int>> = _downloadedSongIds
    private val _downloadProgress = MutableLiveData<Int>()
    val downloadProgress: LiveData<Int> = _downloadProgress
    private val _favoriteSongIds = mutableStateOf<Set<Int>>(emptySet())
    val favoriteSongIds: State<Set<Int>> = _favoriteSongIds

    init {
        viewModelScope.launch {
            loadSongDataFromFirebase()
        }
    }

    private suspend fun loadSongDataFromFirebase() {
        val result = musicRepository.loadSongDataFromFirebase()
        if (result.isSuccess) {
            Log.d("FIRE", "Success load from firebase")
            val songListsByCategory = musicRepository.getSongListsByCategory()
            Log.d("FIRE", "Fetched categories: ${songListsByCategory.keys}")

            // Initialize genres
            if (songListsByCategory.isNotEmpty()) {
                selectedGenre = songListsByCategory.keys.first()
                Log.d("FIRE", "Default selected genre: $selectedGenre")
            }

            // Initialize the first and second row genres
            val songCategoryList = musicRepository.getCategoryList()
            if (songCategoryList.size < 8) {
                val firstRowLimit = 4
                firstRowGenres = songCategoryList.take(firstRowLimit)
                secondRowGenres = songCategoryList.drop(firstRowLimit)
            } else {
                val firstRowLimit = songCategoryList.size / 2
                firstRowGenres = songCategoryList.take(firstRowLimit)
                secondRowGenres = songCategoryList.drop(firstRowLimit)
            }

            updateSelectedGenreSongs(songListsByCategory)

            viewModelScope.launch(Dispatchers.IO) {
                val favoriteSongIds = musicRepository.getFavoriteSongIds()
                val downloadedSongIds = musicRepository.getDownloadedSongIds()

                withContext(Dispatchers.Main) {
                    _favoriteSongIds.value = favoriteSongIds.toMutableSet()
                    _downloadedSongIds.value = downloadedSongIds.toMutableSet()
                }
            }
        } else {
            Log.d("FIRE", "Error downloading JSON file: ${result.exceptionOrNull()?.message}")
        }
    }

    private fun updateSelectedGenreSongs(songListsByCategory: Map<String, List<OnlineSong>>) {
        Log.d("FIRE", "Updating selected genre songs for genre: $selectedGenre")
        selectedGenreSongs = songListsByCategory[selectedGenre] ?: emptyList()
        Log.d("FIRE", "Selected genre songs: $selectedGenreSongs")
    }

    fun toggleDownloadedSong(songId: Int) {
        viewModelScope.launch {
            musicRepository.toggleDownloadedSong(songId)
            _downloadedSongIds.value = musicRepository.getDownloadedSongIds().toMutableSet()
        }
    }

    fun toggleFavorite(songId: Int) {
        viewModelScope.launch {
            musicRepository.toggleFavoriteSong(songId)
            _favoriteSongIds.value = musicRepository.getFavoriteSongIds().toMutableSet()
        }
    }

    fun getSelectedGenreName(): String {
        return selectedGenre
    }

    fun setSelectedGenreName(genre: String) {
        selectedGenre = genre
        viewModelScope.launch {
            val songListsByCategory = musicRepository.getSongListsByCategory()
            updateSelectedGenreSongs(songListsByCategory)
        }
    }

    fun getFirstRowGenreList(): List<Category> {
        return firstRowGenres
    }

    fun getSecondRowGenreList(): List<Category> {
        return secondRowGenres
    }

    fun getSongListsByCategory(): List<OnlineSong> {
        return musicRepository.getSongListsByCategory().flatMap { it.value }
    }
}
