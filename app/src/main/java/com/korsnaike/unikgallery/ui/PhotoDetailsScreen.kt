package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.viewmodel.CommentViewModel
import com.korsnaike.unikgallery.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(
    photoId: Int,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val photo by photoViewModel.getPhotoById(photoId).observeAsState()
    val comments by commentViewModel.getComments(photoId, "photo").observeAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }
    var editingComment by remember { mutableStateOf<Comment?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Детали фото") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Отображение фото
            AsyncImage(
                model = photo?.uri,
                contentDescription = "Фото",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CommentSection(
                comments = comments,
                commentText = commentText,
                commentViewModel = commentViewModel,
                onCommentTextChange = { commentText = it },
                onAddComment = {
                    addComment(commentText, photoId, commentViewModel, "photo")
                    commentText = ""
                },
                onEditComment = { editingComment = it }
            )
        }
    }
}
