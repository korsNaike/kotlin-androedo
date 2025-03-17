package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.viewmodel.CommentViewModel
import com.korsnaike.unikgallery.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsScreen(
    albumId: Int,
    albumName: String,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    // Получаем список фото для альбома
    val photos by photoViewModel.getPhotosByAlbum(albumId).observeAsState(initial = emptyList())
    // Получаем комментарии к альбому (тип "album")
    val comments by commentViewModel.getComments(albumId, "album").observeAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Альбом: $albumName") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Фото:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            // Отображаем список фото в горизонтальном списке
            LazyRow {
                items(photos) { photo ->
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = "Фото альбома",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Комментарии:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(comments) { comment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Добавить комментарий") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        // Создаём новый комментарий с типом "album"
                        val newComment = Comment(
                            entityId = albumId,
                            type = "album",
                            text = commentText
                        )
                        commentViewModel.insertComment(newComment)
                        commentText = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Отправить")
            }
        }
    }
}
