package com.korsnaike.unikgallery.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM Photo ORDER BY createdAt DESC")
    fun getAllPhotos(): Flow<List<Photo>>
    
    @Query("SELECT * FROM Photo WHERE albumId = :albumId ORDER BY createdAt DESC")
    fun getPhotosByAlbum(albumId: Int): Flow<List<Photo>>

    @Query("SELECT * FROM Photo WHERE id= :photoId LIMIT 1")
    fun getPhoto(photoId: Int): Photo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(vararg photo: Photo): List<Long>

    @Delete
    fun deletePhoto(vararg photo: Photo): Int
}
