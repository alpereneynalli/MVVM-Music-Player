package com.player.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import java.io.File

interface ProgressListener {
    fun onProgress(progress: Int)
}

class SongDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private var progressListener: ProgressListener? = null

    override suspend fun doWork(): Result {
        val songId = inputData.getInt(INPUT_SONG_ID, -1)
        val songName = inputData.getString(INPUT_SONG_NAME) ?: return Result.failure()

        return try {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("OnlineSongs").child(songName)

            val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val localFile = File(downloadDirectory, songName)

            val task = storageRef.getFile(localFile)

            // Attach progress listener
            task.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                Log.d("WORKER", "WorkerProgress: $progress")
                progressListener?.onProgress(progress)
            }

            // Suspend until the task completes
            while (!task.isComplete) {
                // You can add a delay here if needed
                // e.g., delay(100)
            }

            if (task.isSuccessful) {
                Result.success()
            } else {
                Result.failure()
            }

        } catch (e: Exception) {
            Log.e("SongDownloadWorker", "Error downloading song: ${e.message}")
            Result.failure()
        }
    }

    fun setProgressListener(listener: ProgressListener?) {
        this.progressListener = listener
    }

    companion object {
        const val INPUT_SONG_ID = "song_id"
        const val INPUT_SONG_NAME = "song_name"
        const val PROGRESS = "progress"
    }
}