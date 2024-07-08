package com.player.ui.screens

sealed class ScreenState {
    object Loading : ScreenState()
    object Error : ScreenState()
    object Loaded : ScreenState()
}