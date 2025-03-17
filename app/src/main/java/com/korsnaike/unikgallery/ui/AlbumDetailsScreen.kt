package com.korsnaike.unikgallery.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.korsnaike.unikgallery.data.Comment
import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.viewmodel.CommentViewModel
import com.korsnaike.unikgallery.viewmodel.PhotoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsScreen(
    albumId: Int,
    albumName: String,
    navController: NavController,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    // Получаем список фото для альбома
    val photos by photoViewModel.getPhotosByAlbum(albumId).observeAsState(initial = emptyList())
    // Получаем комментарии к альбому (тип "album")
    val comments by commentViewModel.getComments(albumId, "album").observeAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Создаем постоянную копию URI, чтобы сохранить доступ к изображению
            val persistedUri = persistUri(context, selectedUri)
            // Вставляем новое фото в базу данных
            val newPhoto = Photo(
                albumId = albumId,
                uri = persistedUri.toString()
            )
            photoViewModel.insertPhoto(newPhoto)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Альбом: $albumName") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Симулируем добавление фото с тестовым URI
                launcher.launch("image/*")
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Добавить фото")
            }
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
                            .clickable {
                                navController.navigate("photo_details/${photo.id}")
                            }
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

private fun persistUri(context: Context, uri: Uri): Uri {
    // Получаем тип файла
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

    // Копируем файл в кеш приложения (или другое место хранения)
    val inputStream = contentResolver.openInputStream(uri)
    val fileName = "${System.currentTimeMillis()}.${mimeType.split("/").last()}"
    val outputFile = File(context.filesDir, fileName)

    inputStream?.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    // Возвращаем локальный URI файла
    return Uri.fromFile(outputFile)
}
