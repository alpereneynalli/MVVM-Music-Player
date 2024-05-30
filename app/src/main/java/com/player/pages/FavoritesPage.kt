package com.player.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.player.R
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.composables.SongListItem
import com.player.composables.TopAppBar
import com.player.ui.theme.gradientBrush
import com.player.viewmodel.AddMusicViewModel


@Composable
fun FavoritesPage(
    navController: NavController,
    viewModel: AddMusicViewModel,
    audioPlayerViewModel: MediaPlayerViewModel
) {
    val favoriteSongIds by viewModel.favoriteSongIds
    val downloadedSongIds by viewModel.downloadedSongIds
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
                        navController
                    )
                },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))
                        LazyColumn {
                            items(
                                viewModel.getSongListsByCategory()
                                    .filter { song ->
                                        song.songID in favoriteSongIds
                                    }
                            ) { favoriteSong ->
                                SongListItem(
                                    song = favoriteSong,
                                    isFavorite = favoriteSong.songID in favoriteSongIds,
                                    isDownloaded = favoriteSong.songID in downloadedSongIds,
                                    onFavoriteToggle = {
                                        viewModel.toggleFavorite(favoriteSong.songID)
                                    },
                                    expandedItemId = viewModel.currentExpandedItemId,
                                    onItemClicked = { itemId ->
                                        viewModel.currentExpandedItemId = itemId
                                    },
                                    onDownloadClicked = {
                                        // Handle download button click
                                        //viewModel.scheduleSongDownload(favoriteSong.songID, favoriteSong.fileName)
                                    },
                                    audioPlayerViewModel,
                                    viewModel
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}