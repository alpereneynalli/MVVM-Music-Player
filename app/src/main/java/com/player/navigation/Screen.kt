package com.player.navigation

sealed class Screen(val route: String){
    object AddMusicPage: Screen(route = "AddMusicPage")
    object Favorites: Screen(route = "FavoritesPage")
    object Downloaded: Screen(route = "DownloadedFilesPage")
}
