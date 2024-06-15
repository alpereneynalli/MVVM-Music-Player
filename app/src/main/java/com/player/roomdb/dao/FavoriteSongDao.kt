package com.player.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.player.roomdb.entity.FavoriteSongEntity

@Dao
interface FavoriteSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteSong(favoriteSong: FavoriteSongEntity)

    @Query("DELETE FROM favorite_songs WHERE songId = :songId")
    suspend fun deleteFavoriteSong(songId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :songId LIMIT 1)")
    suspend fun isSongFavorite(songId: Int): Boolean

    @Query("SELECT songId FROM favorite_songs")
    suspend fun getFavoriteSongIds(): List<Int>
}