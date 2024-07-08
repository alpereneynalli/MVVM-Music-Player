package com.player.ui.audioPlayer

sealed class SongExpansionState {
    object Expanded : SongExpansionState()
    object Closed : SongExpansionState()
}