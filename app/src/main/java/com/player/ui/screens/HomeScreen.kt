package com.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.player.R
import com.player.ui.audioPlayer.MediaPlayerViewModel
import com.player.ui.composables.IconButtonWithText
import com.player.ui.composables.SearchBar
import com.player.ui.composables.SongListItem
import com.player.ui.composables.SquareButtonWithImage
import com.player.ui.composables.TopAppBar
import com.player.ui.theme.buttonColor
import com.player.ui.theme.gradientBrush

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    audioPlayerViewModel: MediaPlayerViewModel,
    onBackClicked: () -> Unit,
    onFavoritesClicked: () -> Unit,
    onDownloadedClicked: () -> Unit
) {
    val firstRowGenres by viewModel.firstRowGenres.observeAsState(initial = emptyList())
    val secondRowGenres by viewModel.secondRowGenres.observeAsState(initial = emptyList())
    val selectedGenre by viewModel.selectedGenre.observeAsState()
    val selectedGenreSongs by viewModel.selectedGenreSongs.observeAsState(initial = emptyList())
    val favoriteSongIds by viewModel.favoriteSongIds.observeAsState()
    val currentExpandedItemId by viewModel.currentExpandedItemId.observeAsState(initial = -1)
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
        ) {
            Scaffold(
                backgroundColor = Color.Transparent,
                topBar = { TopAppBar(text = stringResource(R.string.AUDIO_PLAYER), onBackClicked) },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(48.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Top,
                        ) {
                            SearchBar(placeHolder = stringResource(id = R.string.SEARCH_MUSIC))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Buttons row (FavoritesPage, Downloaded, Sound Effects, My Files)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            IconButtonWithText(
                                text = stringResource(id = R.string.FAVORITES),
                                icon = Icons.Default.Favorite,
                                buttonColor,
                            ) {
                                onFavoritesClicked()
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButtonWithText(
                                text = stringResource(id = R.string.DOWNLOADED),
                                icon = Icons.Default.Download,
                                buttonColor
                            ) {
                                onDownloadedClicked()
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButtonWithText(
                                text = stringResource(id = R.string.SOUND_EFFECTS),
                                icon = Icons.Default.SurroundSound,
                                buttonColor
                            ) {
                                // Handle Sound Effects button click
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButtonWithText(
                                text = stringResource(id = R.string.MY_FILES),
                                icon = Icons.Default.AudioFile,
                                buttonColor
                            ) {
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(firstRowGenres) { genre ->
                                SquareButtonWithImage(
                                    text = genre.category,
                                    categoryName = genre.category,
                                    selected = selectedGenre == genre.category,
                                    onClick = { /* Handle genre button click */ },
                                    onSelected = { viewModel.setSelectedGenreName(genre.category) },
                                    viewModel
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (secondRowGenres.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(secondRowGenres) { genre ->
                                    SquareButtonWithImage(
                                        text = genre.category,
                                        categoryName = genre.category,
                                        selected = selectedGenre == genre.category,
                                        onClick = { /* Handle genre button click */ },
                                        onSelected = { viewModel.setSelectedGenreName(genre.category) },
                                        viewModel
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn {
                            items(selectedGenreSongs) { song ->
                                SongListItem(
                                    song = song,
                                    isFavorite = false,
                                    onFavoriteToggle = {
                                        viewModel.toggleFavorite(song.songID)
                                    },
                                    audioPlayerViewModel = audioPlayerViewModel,
                                )
                            }
                        }

                    }
                }
            )
        }
    }
}