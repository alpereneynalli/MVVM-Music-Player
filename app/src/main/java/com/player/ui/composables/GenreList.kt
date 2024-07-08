package com.player.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.player.ui.screens.MainViewModel

@Composable
fun GenreList(viewModel: MainViewModel) {

    val firstRowGenres by viewModel.firstRowGenres.observeAsState(initial = emptyList())
    val secondRowGenres by viewModel.secondRowGenres.observeAsState(initial = emptyList())
    val selectedGenre by viewModel.selectedGenre.observeAsState()

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
}