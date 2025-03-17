package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun PhotoDetailsScreen(
    photoId: Int,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val photo by photoViewModel.getPhotoById(photoId).observeAsState()

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
                model = photo?.uri,
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить комментарий")
                }
            }
        }
    }
}
