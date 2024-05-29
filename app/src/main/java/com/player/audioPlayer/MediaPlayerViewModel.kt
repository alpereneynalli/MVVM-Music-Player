package com.player.audioPlayer

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MediaPlayerViewModel(@SuppressLint("StaticFieldLeak") private val applicationContext: Context, private val storage: FirebaseStorage) :
    ViewModel() {
    companion object {
        private const val TAG = "MediaPlayerViewModel"
        private const val UPDATE_INTERVAL_MS = 50L
        private const val PLAYBACK_STATE_READY = 3
        private const val PLAYBACK_STATE_ENDED = 4
    }

    private val _currentMinutes = MutableLiveData(0)
    val currentMinutes: LiveData<Int> get() = _currentMinutes
    private val _audioFinish = MutableLiveData(false)
    val audioFinish: LiveData<Boolean> get() = _audioFinish
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _duration = MutableLiveData(0L)
    val duration: LiveData<Long> get() = _duration

    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer
        get() = _exoPlayer ?: throw IllegalStateException("ExoPlayer is not initialized")

    private val handler = Handler(Looper.getMainLooper())

    val progressRunnable: Runnable = object : Runnable {
        override fun run() {

            _exoPlayer?.currentPosition?.toInt()?.let {
                // Update _currentMinutes only if the new position is different
                if (it != _currentMinutes.value) {
                    _currentMinutes.value = it
                }
            }

            handler.postDelayed(this, 50)

        }
    }


    private fun cancelTimerTask() {
        Log.d(TAG, "cancelTimerTask: ")
        handler.removeCallbacks(progressRunnable)
    }


    fun seekTo(position: Int) {
        exoPlayer.seekTo(position.toLong())
    }

    fun forward(seconds: Int) {
        seekTo((exoPlayer.currentPosition.toInt()) + seconds * 1000)
    }

    fun backward(seconds: Int) {
        seekTo(exoPlayer!!.currentPosition.toInt() - seconds * 1000)
    }

    fun release() {
        if (exoPlayer != null){
            exoPlayer!!.stop()
            exoPlayer!!.release()
        }
    }


    fun formatMilliseconds(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%01d:%02d", minutes, seconds)
    }

    fun destroy() {
        cancelTimerTask()
        _exoPlayer = null
    }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun initExoPlayer(audioFile: Uri) {
        if (_exoPlayer == null) {
            _exoPlayer = ExoPlayer.Builder(applicationContext).build().apply {
                setMediaItem(MediaItem.fromUri(audioFile))
                prepare()
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(TAG, "isPlayingChangedTriggered")
                        _isPlaying.value = isPlaying
                        if (isPlaying) {
                            _audioFinish.value = false
                            handler.post(progressRunnable)
                        } else {
                            handler.removeCallbacks(progressRunnable)
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            PLAYBACK_STATE_ENDED -> {
                                _audioFinish.value = true
                                Log.d(TAG, "onFinish: Media Player Finished")
                                handler.removeCallbacks(progressRunnable)
                            }
                            PLAYBACK_STATE_READY -> {
                                _duration.postValue(_exoPlayer?.duration)
                            }
                        }
                    }
                })
            }
        } else {
            _exoPlayer?.setMediaItem(MediaItem.fromUri(audioFile))
            _exoPlayer?.prepare()
        }
    }

    fun loadFileFromFirebase(
        callback: (Uri) -> Unit,
        errorCallback: (String) -> Unit,
        songName: String
    ) {
        val storageRef: StorageReference =
            storage.getReference().child("songs/" + songName)

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Handle the URI here (e.g., display or use it)
                Log.d("EXO", "Download success")
                callback(uri)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                val errorMessage = "Error getting download URL: ${exception.message}"
                Log.d("EXO", errorMessage)
                errorCallback(errorMessage)
            }
    }

    fun getDownloadedFileUri(context: Context, songName: String): Uri? {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        }

        context.contentResolver.query(
            uri,
            null,
            "${MediaStore.Downloads.DISPLAY_NAME}=?",
            arrayOf(songName),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val uriColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                val downloadId = cursor.getLong(uriColumnIndex)
                return ContentUris.withAppendedId(uri, downloadId)
            }
        }

        return null
    }
    fun triggerMediaScan(context: Context, fileName: String) {
        val downloadsDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val filePath = "$downloadsDirPath/$fileName"

        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            null
        ) { path, uri ->
            Log.i("MediaScanner", "Scanned $path:")
            Log.i("MediaScanner", "-> uri=$uri")
        }
    }
}
