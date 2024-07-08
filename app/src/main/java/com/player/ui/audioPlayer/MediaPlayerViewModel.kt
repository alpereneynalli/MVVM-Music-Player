package com.player.ui.audioPlayer

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.player.data.model.OnlineSong
import com.player.utils.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaPlayerViewModel @Inject constructor(
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val applicationContext: Context,
    private val storage: FirebaseStorage,
    private val dispatchers: CoroutinesDispatchers
) : ViewModel() {
    companion object {
        internal const val TAG = "MediaPlayerViewModel"
        internal const val UPDATE_INTERVAL_MS = 50L
        internal const val PLAYBACK_STATE_READY = 3
        internal const val PLAYBACK_STATE_ENDED = 4
    }

    private val _songState = MutableLiveData<SongState>(SongState.Stopped)
    val songState: LiveData<SongState> get() = _songState

    private val _songExpansionState = MutableLiveData<SongExpansionState>(SongExpansionState.Closed)
    val songExpansionState: LiveData<SongExpansionState> get() = _songExpansionState

    private val _currentMinutes = MutableLiveData(0)
    val currentMinutes: LiveData<Int> get() = _currentMinutes

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _duration = MutableLiveData(0L)
    val duration: LiveData<Long> get() = _duration

    private val _currentPlayingSongId = MutableLiveData<Int?>(null)
    val currentPlayingSongId: LiveData<Int?> get() = _currentPlayingSongId

    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer
        get() = _exoPlayer ?: throw IllegalStateException("ExoPlayer is not initialized")

    private var lastPosition: Long = 0L
    private var lastMediaItem: MediaItem? = null

    init {
        startUpdatingProgress()
    }

    fun startUpdatingProgress(dispatcher: CoroutineDispatcher = Dispatchers.Main) {
        viewModelScope.launch(dispatchers.main) {
            while (true) {
                _exoPlayer?.currentPosition?.toInt()?.let {
                    if (it != _currentMinutes.value) {
                        Log.d(TAG, "currentPosition: $it")
                        _currentMinutes.value = it
                    }
                }
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    fun setPlayingSongId(songId: Int) {
        _currentPlayingSongId.value = songId
        //_songState.value = SongState.Expanded(songId)
    }

    fun stopPlaying() {
        _exoPlayer?.stop()
        _isPlaying.postValue(false)
        _currentPlayingSongId.postValue(null)
        _songState.value = SongState.Stopped
    }

    fun seekTo(position: Int) {
        exoPlayer.seekTo(position.toLong())
    }

    fun forward(seconds: Int) {
        seekTo((exoPlayer.currentPosition.toInt()) + seconds * 1000)
    }

    fun backward(seconds: Int) {
        seekTo(exoPlayer.currentPosition.toInt() - seconds * 1000)
    }

    fun release() {
        _exoPlayer?.let {
            it.stop()
            it.release()
        }
        _exoPlayer = null
    }

    fun formatMilliseconds(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%01d:%02d", minutes, seconds)
    }

    fun destroy() {
        _exoPlayer = null
    }

    fun play() {
        exoPlayer.play()
        _songState.value = SongState.Playing
    }

    fun pause() {
        exoPlayer.pause()
        _songState.value = SongState.Paused
    }

    fun handlePlayPause() {
        if (_isPlaying.value == true) {
            pause()
        } else {
            play()
        }
    }

    fun initExoPlayer(audioFile: Uri) {
        if (_exoPlayer == null) {
            _exoPlayer = ExoPlayer.Builder(applicationContext).build().apply {
                lastMediaItem = MediaItem.fromUri(audioFile)
                setMediaItem(lastMediaItem!!)
                prepare()
                seekTo(lastPosition)
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(TAG, "isPlayingChangedTriggered")
                        _isPlaying.value = isPlaying

                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            PLAYBACK_STATE_ENDED -> {
                                Log.d(TAG, "onFinish: Media Player Finished")
                            }

                            PLAYBACK_STATE_READY -> {
                                _duration.postValue(_exoPlayer?.duration)
                            }
                        }
                    }

                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        if (isLoading) {
                            _songState.value = SongState.Loading
                        } else {
                            _songState.value = SongState.Playing
                        }
                    }

                })
            }
        } else {
            val newMediaItem = MediaItem.fromUri(audioFile)
            if (newMediaItem != lastMediaItem) {
                _exoPlayer?.setMediaItem(newMediaItem)
                _exoPlayer?.prepare()
                _exoPlayer?.seekTo(lastPosition)
                lastMediaItem = newMediaItem
            }
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

    fun handleSong(song: OnlineSong) {
        Log.d("EXO", "Handling song")
        if (_currentPlayingSongId.value == song.songID) {
            when (_songExpansionState.value) {
                is SongExpansionState.Expanded -> _songExpansionState.value =
                    SongExpansionState.Closed

                is SongExpansionState.Closed -> _songExpansionState.value =
                    SongExpansionState.Expanded

                null -> Unit
            }
        } else {
            _songExpansionState.value = SongExpansionState.Closed
            playNewSong(song)
        }
    }

    fun playNewSong(song: OnlineSong) {
        Log.d("EXO", "Playing new song")
        _songState.value = SongState.Loading
        stopPlaying()
        loadFileFromFirebase(
            callback = { uri ->
                initExoPlayer(uri)
                play()
                setPlayingSongId(song.songID)
                _songState.value = SongState.Playing
                _songExpansionState.value = SongExpansionState.Expanded
            },
            errorCallback = {
                _songState.value = SongState.Error
            },
            songName = song.fileName
        )
    }

    fun setExoPlayerForTesting(exoPlayer: ExoPlayer) {
        _exoPlayer = exoPlayer
    }

}