package com.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.player.R
import com.player.ui.audioPlayer.MediaPlayerViewModel
import com.player.ui.audioPlayer.SongListItem
import com.player.ui.composables.TopAppBar
import com.player.ui.theme.gradientBrush
import com.player.ui.theme.selectedCategoryColor


@Composable
fun FavoritesPage(
    viewModel: MainViewModel,
    audioPlayerViewModel: MediaPlayerViewModel,
    onBackClicked: () -> Unit
) {
    val songList by viewModel.allSongs.observeAsState(initial = emptyList())
    val favoriteSongIds by viewModel.favoriteSongIds.observeAsState()
    val favoriteSongs = remember(songList, favoriteSongIds) {
        derivedStateOf {
            songList.filter { favoriteSongIds?.contains(it.songID) == true }
        }
    }
    val screenState by viewModel.screenState.observeAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
        ) {
            Scaffold(
                backgroundColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        text = stringResource(id = R.string.FAVORITES),
                        onBackClicked = onBackClicked
                    )
                },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))

                        when (screenState) {
                            ScreenState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = selectedCategoryColor)
                                }
                            }

                            ScreenState.Loaded -> {
                                LazyColumn {
                                    items(favoriteSongs.value) { favoriteSong ->
                                        SongListItem(
                                            song = favoriteSong,
                                            onFavoriteToggle = {
                                                viewModel.toggleFavorite(favoriteSong.songID)
                                            },
                                            isFavorite = true,
                                            audioPlayerViewModel = audioPlayerViewModel,
                                        )
                                    }
                                }
                            }

                            ScreenState.Error -> TODO()
                            null -> TODO()
                        }
                    }
                }
            )
        }
    }
}