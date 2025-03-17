package com.korsnaike.unikgallery.repository

import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.data.CommentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentDao: CommentDao
) {
    fun getComments(entityId: Int, type: String) = commentDao.getComments(entityId, type)

    suspend fun addComment(comment: Comment): List<Long> {
        return withContext(Dispatchers.IO) {
            commentDao.insertComment(comment)
        }
    }

    suspend fun deleteComment(comment: Comment): Int {
        return withContext(Dispatchers.IO) {
            commentDao.deleteComment(comment)
        }
    }
}
