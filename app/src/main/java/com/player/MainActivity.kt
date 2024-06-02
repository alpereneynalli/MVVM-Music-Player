package com.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.navigation.SetupNavGraph
import com.player.ui.theme.AddMusicPageTheme
import com.player.viewmodel.AddMusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val addMusicViewModel: AddMusicViewModel by viewModels()
    private val audioPlayerViewModel: MediaPlayerViewModel by viewModels()
    private lateinit var navController: NavHostController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddMusicPageTheme {
                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    viewModel = addMusicViewModel,
                    audioPlayerViewModel = audioPlayerViewModel
                )
            }
        }

    }
}
