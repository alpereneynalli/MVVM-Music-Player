package com.player.ui.audioPlayer

sealed class SongState {
    object Stopped : SongState()
    object Loading : SongState()
    object Playing : SongState()
    object Paused : SongState()
    object Error : SongState()
}