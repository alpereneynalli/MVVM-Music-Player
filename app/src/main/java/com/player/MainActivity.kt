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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddMusicPageTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController
                )
            }
        }

    }
}
