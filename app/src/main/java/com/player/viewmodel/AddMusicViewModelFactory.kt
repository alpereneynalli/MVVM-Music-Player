package com.player.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.player.roomdb.AppDatabase

class AddMusicViewModelFactory(
    private val appDatabase: AppDatabase,
    private val storage: FirebaseStorage,
    private val applicationContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddMusicViewModel(appDatabase, storage, applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}