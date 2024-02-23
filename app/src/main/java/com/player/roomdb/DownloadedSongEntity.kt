package com.player.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_song_table")
data class DownloadedSongEntity(
    @PrimaryKey
    val songId: Int,
    val isDownloaded: Boolean
)