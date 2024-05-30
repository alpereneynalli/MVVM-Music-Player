package com.player.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.pages.AddMusicPage
import com.player.pages.DownloadedFilesPage
import com.player.pages.FavoritesPage
import com.player.viewmodel.AddMusicViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModel: AddMusicViewModel,
    audioPlayerViewModel: MediaPlayerViewModel
) {
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