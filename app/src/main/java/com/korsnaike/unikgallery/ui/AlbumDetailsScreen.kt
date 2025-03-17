package com.korsnaike.unikgallery.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable
fun AlbumDetailsScreen(
    albumId: Int,
    albumName: String,
    navController: NavController,
    photoViewModel: PhotoViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val photos by photoViewModel.getPhotosByAlbum(albumId).observeAsState(emptyList())
    val comments by commentViewModel.getComments(albumId, "album").observeAsState(emptyList())
    var commentText by remember { mutableStateOf("") }
    var editingComment by remember { mutableStateOf<Comment?>(null) }
    val context = LocalContext.current

    val photoPickerLauncher = rememberPhotoPickerLauncher(
        context = context,
        albumId = albumId,
        photoViewModel = photoViewModel
    )

    Scaffold(
        topBar = { AlbumTopBar(albumName) },
        floatingActionButton = { AddPhotoButton(photoPickerLauncher) }
    ) { innerPadding ->
        AlbumContent(
            innerPadding = innerPadding,
            photos = photos,
            comments = comments,
            commentText = commentText,
            navController = navController,
            commentViewModel = commentViewModel,
            onCommentTextChange = { commentText = it },
            onAddComment = {
                addComment(commentText, albumId, commentViewModel, "album")
                commentText = ""
            },
            onEditComment = { editingComment = it }
        )

        editingComment?.let {
            EditCommentDialog(
                comment = it,
                onDismiss = { editingComment = null },
                onSave = { updatedText ->
                    commentViewModel.updateComment(it.copy(text = updatedText))
                    editingComment = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumTopBar(albumName: String) {
    TopAppBar(title = { Text(text = "Альбом: $albumName") })
}

@Composable
private fun AddPhotoButton(launcher: (String) -> Unit) {
    FloatingActionButton(onClick = { launcher("image/*") }) {
        Icon(Icons.Default.AddCircle, "Добавить фото")
    }
}

@Composable
private fun rememberPhotoPickerLauncher(
    context: Context,
    albumId: Int,
    photoViewModel: PhotoViewModel
): (String) -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val persistedUri = persistUri(context, it)
            photoViewModel.insertPhoto(Photo(albumId=albumId, uri=persistedUri.toString()))
        }
    }
    return { mimeType -> launcher.launch(mimeType) }
}

@Composable
private fun AlbumContent(
    innerPadding: PaddingValues,
    photos: List<Photo>,
    comments: List<Comment>,
    commentText: String,
    navController: NavController,
    commentViewModel: CommentViewModel,
    onCommentTextChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onEditComment: (Comment) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PhotoGallery(photos, navController)
        CommentSection(
            comments = comments,
            commentText = commentText,
            commentViewModel = commentViewModel,
            onCommentTextChange = onCommentTextChange,
            onAddComment = onAddComment,
            onEditComment = onEditComment
        )
    }
}

@Composable
private fun PhotoGallery(photos: List<Photo>, navController: NavController) {
    Column {
        Text("Фото:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyRow {
            items(photos) { photo ->
                PhotoItem(photo, navController)
            }
        }
    }
}

@Composable
private fun PhotoItem(photo: Photo, navController: NavController) {
    AsyncImage(
        model = photo.uri,
        contentDescription = "Фото альбома",
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
            .clickable { navController.navigate("photo_details/${photo.id}") }
    )
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
