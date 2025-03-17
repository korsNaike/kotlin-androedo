package com.korsnaike.unikgallery.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM Album ORDER BY createdAt DESC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM Album ORDER BY createdAt DESC")
    fun getAllAlbumsSync(): List<Album>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(album: Album): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbums(albums: List<Album>): List<Long>

    @Delete
    fun deleteAlbum(album: Album): Int
}
