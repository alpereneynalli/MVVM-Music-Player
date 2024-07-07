package com.player.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.data.repository.MusicRepository
import com.player.data.model.Category
import com.player.data.model.OnlineSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddMusicViewModel @Inject constructor(
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
        onFirebaseDataLoaded()
    }

    private suspend fun onFirebaseDataLoaded() {
        val categoryList = musicRepository.getCategoryList()
        val songListsByCategory = musicRepository.getSongListsByCategory()
        initializeGenres(categoryList, songListsByCategory)

        val favoriteSongIds = musicRepository.getFavoriteSongIds()
        val downloadedSongIds = musicRepository.getDownloadedSongIds()

        withContext(Dispatchers.Main) {
            _favoriteSongIds.value = favoriteSongIds.toMutableSet()
            _downloadedSongIds.value = downloadedSongIds.toMutableSet()
        }
    }

    private fun initializeGenres(categories: List<Category>, songListsByCategory: Map<String, List<OnlineSong>>) {
        if (categories.isNotEmpty()) {
            firstRowGenres = categories.subList(0, categories.size / 2)
            secondRowGenres = categories.subList(categories.size / 2, categories.size)
            selectedGenre = categories.first().category
            updateSelectedGenreSongs(songListsByCategory)
        }
    }

    private fun updateSelectedGenreSongs(songListsByCategory: Map<String, List<OnlineSong>>) {
        selectedGenreSongs = songListsByCategory[selectedGenre] ?: emptyList()
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