package com.player.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.ui.screens.AddMusicPage
import com.player.ui.screens.DownloadedFilesPage
import com.player.ui.screens.FavoritesPage
import com.player.ui.screens.AddMusicViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
) {
    val viewModel: AddMusicViewModel = hiltViewModel()
    val audioPlayerViewModel: MediaPlayerViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.AddMusicPage.route
    ) {

        composable(
            route = Screen.AddMusicPage.route
        ) {
            AddMusicPage(
                navController = navController,
                viewModel = viewModel,
                audioPlayerViewModel = audioPlayerViewModel
            )
        }

        composable(
            route = Screen.Favorites.route
        ) {
            FavoritesPage(
                navController = navController,
                viewModel = viewModel,
                audioPlayerViewModel = audioPlayerViewModel
            )
        }

        composable(
            route = Screen.Downloaded.route
        ) {
            DownloadedFilesPage(
                navController = navController,
                viewModel = viewModel,
                audioPlayerViewModel = audioPlayerViewModel
            )
        }
    }
}