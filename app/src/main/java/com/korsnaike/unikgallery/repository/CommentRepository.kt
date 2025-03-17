package com.korsnaike.unikgallery.repository

import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.data.CommentDao
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentDao: CommentDao
) {
    fun getComments(entityId: Int, type: String) = commentDao.getComments(entityId, type)
    suspend fun addComment(comment: Comment) = commentDao.insertComment(comment)
    suspend fun deleteComment(comment: Comment) = commentDao.deleteComment(comment)
}
