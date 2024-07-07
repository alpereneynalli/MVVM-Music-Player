package com.player.data.model

data class OnlineSong(
    val category: String,
    val duration: String,
    val fileName: String,
    val musician: String,
    val songName: String,
    val sourceURL: String,
    var songID: Int,
) {
    fun setSongID() {
        songID = songName.hashCode()
    }
}