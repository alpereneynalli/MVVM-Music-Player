package com.player.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.ui.screens.AddMusicPage
import com.player.ui.screens.DownloadedFilesPage
import com.player.ui.screens.FavoritesPage
import com.player.ui.screens.AddMusicViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController
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

        composable(
            route = Screen.Downloaded.route
        ) {
            DownloadedFilesPage(
                viewModel = viewModel,
                audioPlayerViewModel = audioPlayerViewModel,
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}