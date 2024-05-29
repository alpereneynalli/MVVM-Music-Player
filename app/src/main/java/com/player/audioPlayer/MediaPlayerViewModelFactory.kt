package com.player.audioPlayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage

class MediaPlayerViewModelFactory(
    private val applicationContext: Context, private val storage: FirebaseStorage
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaPlayerViewModel(applicationContext, storage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}