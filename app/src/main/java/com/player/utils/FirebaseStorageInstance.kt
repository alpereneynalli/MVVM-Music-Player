package com.player.utils
import com.google.firebase.storage.FirebaseStorage

object FirebaseStorageInstance {
    val instance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
}