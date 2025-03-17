package com.korsnaike.unikgallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository
) : ViewModel() {

    fun getComments(entityId: Int, type: String) = commentRepository.getComments(entityId, type).asLiveData()

    fun insertComment(comment: Comment) {
        viewModelScope.launch {
            commentRepository.addComment(comment)
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            commentRepository.deleteComment(comment)
        }
    }
}
