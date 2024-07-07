package com.player.data.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.player.data.roomdb.entity.DownloadedSongEntity


@Dao
interface DownloadedSongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDownloadedSong(downloadedSong: DownloadedSongEntity)

    @Query("DELETE FROM downloaded_song_table WHERE songId = :songId")
    suspend fun deleteDownloadedSong(songId: Int)

    @Query("SELECT COUNT(*) FROM downloaded_song_table WHERE songId = :songId")
    suspend fun isSongDownloaded(songId: Int): Int

    @Query("SELECT * FROM downloaded_song_table")
    suspend fun getAllDownloadedSongs(): List<DownloadedSongEntity>

    @Query("SELECT songId FROM downloaded_song_table WHERE isDownloaded = 1")
    suspend fun getDownloadedSongIds(): List<Int>
}