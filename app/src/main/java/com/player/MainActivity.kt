package com.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

class MainActivity  : ComponentActivity() {

    private val viewModel: AddMusicViewModel by viewModels()
    lateinit var navController: NavHostController
    private val audioPlayerViewModel: MediaPlayerViewModel by viewModels()
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        appDatabase = AppDatabase.getInstance(applicationContext)
        val viewModelFactory = AddMusicViewModelFactory(appDatabase, applicationContext)
        val viewModel: AddMusicViewModel by viewModels(factoryProducer = { viewModelFactory })
        val mediaPlayerViewModelFactory = MediaPlayerViewModelFactory(applicationContext)
        val audioPlayerViewModel: MediaPlayerViewModel by viewModels(factoryProducer = { mediaPlayerViewModelFactory})
        setContent {
            AddMusicPageTheme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController, viewModel = viewModel, audioPlayerViewModel = audioPlayerViewModel)
            }
        }
        //ActivityUtils.hideStatusBar(this)
    }
}






