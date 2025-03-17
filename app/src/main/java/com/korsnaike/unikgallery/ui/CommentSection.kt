package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.viewmodel.CommentViewModel

@Composable
fun CommentSection(
    comments: List<Comment>,
    commentText: String,
    commentViewModel: CommentViewModel,
    onCommentTextChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onEditComment: (Comment) -> Unit
) {
    Column {
        Text(stringResource(id = R.string.comments), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        CommentList(comments, commentViewModel, onEditComment)
        AddCommentInput(
            commentText = commentText,
            onCommentTextChange = onCommentTextChange,
            onAddComment = onAddComment
        )
    }
}

@Composable
private fun CommentList(
    comments: List<Comment>,
    commentViewModel: CommentViewModel,
    onEditComment: (Comment) -> Unit
) {
    LazyColumn {
        items(comments, key = { it.id }) { comment ->
            CommentItem(
                comment = comment,
                onEdit = { onEditComment(comment) },
                onDelete = { commentViewModel.deleteComment(comment) }
            )
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(comment.text, Modifier.weight(1f))
            CommentActions(onEdit, onDelete)
        }
    }
}

@Composable
private fun CommentActions(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row {
        IconButton(onClick = onEdit) {
            Icon(
                Icons.Default.Edit, 
                contentDescription = stringResource(id = R.string.edit_comment)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete, 
                contentDescription = stringResource(id = R.string.delete_comment)
            )
        }
    }
}

@Composable
private fun AddCommentInput(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onAddComment: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = onCommentTextChange,
            label = { Text(stringResource(id = R.string.comment)) },
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onAddComment) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(id = R.string.send_comment)
            )
        }
    }
}

fun addComment(
    text: String,
    albumId: Int,
    commentViewModel: CommentViewModel,
    type: String
) {
    if (text.isNotBlank()) {
        commentViewModel.insertComment(
            Comment(
                entityId = albumId,
                type = type,
                text = text
            )
        )
    }
}