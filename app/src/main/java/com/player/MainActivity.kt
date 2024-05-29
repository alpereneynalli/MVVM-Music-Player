package com.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.audioPlayer.MediaPlayerViewModelFactory
import com.player.navigation.SetupNavGraph
import com.player.roomdb.AppDatabase
import com.player.ui.theme.AddMusicPageTheme
import com.player.viewmodel.AddMusicViewModel
import com.player.viewmodel.AddMusicViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private lateinit var addMusicViewModel: AddMusicViewModel
    private lateinit var audioPlayerViewModel: MediaPlayerViewModel
    private lateinit var navController: NavHostController
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        appDatabase = AppDatabase.getInstance(applicationContext)

        // Initialize ViewModels
        val viewModelFactory = AddMusicViewModelFactory(appDatabase, applicationContext)
        addMusicViewModel = ViewModelProvider(this, viewModelFactory).get(AddMusicViewModel::class.java)

        val mediaPlayerViewModelFactory = MediaPlayerViewModelFactory(applicationContext)
        audioPlayerViewModel = ViewModelProvider(this, mediaPlayerViewModelFactory).get(MediaPlayerViewModel::class.java)

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
        //ActivityUtils.hideStatusBar(this)
    }
}
