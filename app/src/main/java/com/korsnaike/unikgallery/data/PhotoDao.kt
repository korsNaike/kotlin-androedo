package com.korsnaike.unikgallery.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM Photo WHERE albumId = :albumId ORDER BY createdAt DESC")
    fun getPhotosByAlbum(albumId: Int): Flow<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(vararg photo: Photo)

    @Delete
    suspend fun deletePhoto(vararg photo: Photo)
}
