package com.player.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.player.ui.audioPlayer.MediaPlayerViewModel
import com.player.ui.screens.FavoritesPage
import com.player.ui.screens.HomeScreen
import com.player.ui.screens.MainViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val viewModel: MainViewModel = hiltViewModel()
    val audioPlayerViewModel: MediaPlayerViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.AddMusicPage.route
    ) {

        composable(
            route = Screen.AddMusicPage.route
        ) {
            HomeScreen(
                viewModel = viewModel,
                audioPlayerViewModel = audioPlayerViewModel,
                onBackClicked = {
                    navController.popBackStack()
                },
                onFavoritesClicked = {
                    navController.navigate(Screen.Favorites.route)
                },
                onDownloadedClicked = {
                    navController.navigate(Screen.Downloaded.route)
                }
            )
        }

        composable(
            route = Screen.Favorites.route
        ) {
            FavoritesPage(
                viewModel = viewModel,
                audioPlayerViewModel = audioPlayerViewModel,
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

    }
}