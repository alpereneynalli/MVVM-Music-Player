package com.player.ui.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.player.ui.audioPlayer.MediaPlayerViewModel
import com.player.ui.audioPlayer.SongListItem
import com.player.ui.screens.MainViewModel

@Composable
fun SongList(viewModel: MainViewModel, audioPlayerViewModel: MediaPlayerViewModel) {

    val selectedGenreSongs by viewModel.selectedGenreSongs.observeAsState(initial = emptyList())
    val favoriteSongIds by viewModel.favoriteSongIds.observeAsState()

    LazyColumn {
        items(selectedGenreSongs) { song ->
            SongListItem(
                song = song,
                isFavorite = favoriteSongIds?.contains(song.songID) == true ,
                onFavoriteToggle = {
                    viewModel.toggleFavorite(song.songID)
                },
                audioPlayerViewModel = audioPlayerViewModel,
            )
        }
    }
}