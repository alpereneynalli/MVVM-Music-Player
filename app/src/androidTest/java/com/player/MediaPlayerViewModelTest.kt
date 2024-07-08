package com.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.storage.FirebaseStorage
import com.player.ui.audioPlayer.MediaPlayerViewModel
import com.player.utils.CoroutinesDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class MediaPlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MediaPlayerViewModel
    private lateinit var mockContext: Context
    private lateinit var mockStorage: FirebaseStorage
    private lateinit var mockExoPlayer: ExoPlayer

    @Before
    fun setup() {
        mockContext = ApplicationProvider.getApplicationContext()
        mockStorage = mock(FirebaseStorage::class.java)
        mockExoPlayer = mock(ExoPlayer::class.java)

        val dispatchers = object : CoroutinesDispatchers() {
            override val main = testDispatcher
            override val io = testDispatcher
            override val default = testDispatcher
        }

        viewModel = MediaPlayerViewModel(mockContext, mockStorage, dispatchers).apply {
            setExoPlayerForTesting(mockExoPlayer)
        }
    }

    @Test
    fun `initExoplayer sets media item and prepares player`() = runTest {
        val audioFile = Uri.parse("android.resource://com.example.app/drawable/song")

        viewModel.initExoPlayer(audioFile)

        verify(mockExoPlayer).setMediaItem(MediaItem.fromUri(audioFile))
        verify(mockExoPlayer).prepare()
    }

    @Test
    fun `play calls play on ExoPlayer`() = runTest {
        viewModel.play()
        verify(mockExoPlayer).play()
    }

    @Test
    fun `pause calls pause on ExoPlayer`() = runTest {
        viewModel.pause()
        verify(mockExoPlayer).pause()
    }

    @Test
    fun `stopPlaying calls stop on ExoPlayer`() = runTest {
        viewModel.stopPlaying()
        verify(mockExoPlayer).stop()
    }

    @Test
    fun `seekTo calls seekTo on ExoPlayer`() = runTest {
        val position = 1000
        viewModel.seekTo(position)
        verify(mockExoPlayer).seekTo(position.toLong())
    }

    @Test
    fun `forward calls seekTo with increased position on ExoPlayer`() = runTest {
        val seconds = 10
        val currentPosition = 1000L
        `when`(mockExoPlayer.currentPosition).thenReturn(currentPosition)
        viewModel.forward(seconds)
        verify(mockExoPlayer).seekTo(currentPosition + seconds * 1000)
    }

    @Test
    fun `backward calls seekTo with decreased position on ExoPlayer`() = runTest {
        val seconds = 10
        val currentPosition = 20000L
        `when`(mockExoPlayer.currentPosition).thenReturn(currentPosition)
        viewModel.backward(seconds)
        verify(mockExoPlayer).seekTo(currentPosition - seconds * 1000)
    }

}