package com.player.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.data.model.Category
import com.player.data.model.OnlineSong
import com.player.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {


    private val _firstRowGenres = MutableLiveData<List<Category>>()
    val firstRowGenres: LiveData<List<Category>> = _firstRowGenres

    private val _secondRowGenres = MutableLiveData<List<Category>>()
    val secondRowGenres: LiveData<List<Category>> = _secondRowGenres

    private val _selectedGenre = MutableLiveData<String>("")
    var selectedGenre : LiveData<String> = _selectedGenre

    private val _selectedGenreSongs = MutableLiveData<List<OnlineSong>>(emptyList())
    val selectedGenreSongs: LiveData<List<OnlineSong>> = _selectedGenreSongs

    private val _currentExpandedItemId = MutableLiveData(-1)
    val currentExpandedItemId: LiveData<Int> = _currentExpandedItemId

    private val _favoriteSongIds = MutableLiveData<Set<Int>>(emptySet())
    val favoriteSongIds: LiveData<Set<Int>> = _favoriteSongIds

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
        }
    }

    private fun initializeGenres(
        categories: List<Category>,
        songListsByCategory: Map<String, List<OnlineSong>>
    ) {
        if (categories.isNotEmpty()) {
            _firstRowGenres.value = categories.subList(0, categories.size / 2)
            _secondRowGenres.value = categories.subList(categories.size / 2, categories.size)
            _selectedGenre.value = categories.first().category
            updateSelectedGenreSongs(songListsByCategory)
        }
    }

    private fun updateSelectedGenreSongs(songListsByCategory: Map<String, List<OnlineSong>>) {
        _selectedGenreSongs.value = songListsByCategory[_selectedGenre.value] ?: emptyList()
    }

    fun toggleDownloadedSong(songId: Int) {
        viewModelScope.launch {
            musicRepository.toggleDownloadedSong(songId)
        }
    }

    fun toggleFavorite(songId: Int) {
        viewModelScope.launch {
            musicRepository.toggleFavoriteSong(songId)
            _favoriteSongIds.value = musicRepository.getFavoriteSongIds().toMutableSet()
        }
    }

    fun getSelectedGenreName(): String? {
        return _selectedGenre.value
    }

    fun setSelectedGenreName(genre: String) {
        _selectedGenre.value = genre
        viewModelScope.launch {
            val songListsByCategory = musicRepository.getSongListsByCategory()
            updateSelectedGenreSongs(songListsByCategory)
        }
    }

    fun setExpandedItemId(itemId: Int) {
        _currentExpandedItemId.value = itemId
    }

    fun getSongListsByCategory(): List<OnlineSong> {
        return musicRepository.getSongListsByCategory().flatMap { it.value }
    }

}