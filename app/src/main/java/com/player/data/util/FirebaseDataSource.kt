package com.player.data.util

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

class FirebaseDataSource(private val storage: FirebaseStorage, private val context: Context) {

    suspend fun getDownloadUri(songName: String): Result<Uri> {
        return runCatching {
            val storageRef: StorageReference = storage.reference.child("songs/$songName")
            storageRef.downloadUrl.await()
        }
    }

    suspend fun downloadJsonFile(fileName: String): Result<String> {
        return runCatching {
            val storageReference = storage.reference.child(fileName)
            val cacheDir = context.cacheDir
            val localFile = File(cacheDir, "songsTemp.json")

            storageReference.getFile(localFile).await()

            val jsonData: String = FileInputStream(localFile).bufferedReader().use { it.readText() }
            localFile.delete()

            jsonData
        }
    }
}