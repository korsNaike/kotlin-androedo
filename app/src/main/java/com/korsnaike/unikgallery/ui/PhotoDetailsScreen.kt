package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.viewmodel.CommentViewModel
import com.korsnaike.unikgallery.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(
    photoId: Int,
    photoUri: String,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    // Получаем комментарии для фото (тип "photo")
    val comments by commentViewModel.getComments(photoId, "photo").observeAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

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
                model = photoUri,
                contentDescription = "Фото",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Комментарии к фото", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(comments) { comment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = comment.text,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Комментарий") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (commentText.isNotBlank()) {
                        val newComment = Comment(
                            entityId = photoId,
                            type = "photo",
                            text = commentText
                        )
                        commentViewModel.insertComment(newComment)
                        commentText = ""
                    }
                }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Отправить комментарий")
                }
            }
        }
    }
}
