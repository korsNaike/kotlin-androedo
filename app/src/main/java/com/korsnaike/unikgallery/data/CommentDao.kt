package com.korsnaike.unikgallery.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM Comment WHERE entityId = :entityId AND type = :type ORDER BY createdAt DESC")
    fun getComments(entityId: Int, type: String): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(vararg comment: Comment): List<Long>

    @Delete
    fun deleteComment(vararg comment: Comment): Int
}
