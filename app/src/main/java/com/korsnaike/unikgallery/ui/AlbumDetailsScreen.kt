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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
                addComment(commentText, albumId, commentViewModel)
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

@Composable
private fun CommentSection(
    comments: List<Comment>,
    commentText: String,
    commentViewModel: CommentViewModel,
    onCommentTextChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onEditComment: (Comment) -> Unit
) {
    Column {
        Text("Комментарии:", style = MaterialTheme.typography.titleMedium)
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
            Icon(Icons.Default.Edit, "Редактировать")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, "Удалить")
        }
    }
}

@Composable
private fun AddCommentInput(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onAddComment: () -> Unit
) {
    Column {
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = commentText,
            onValueChange = onCommentTextChange,
            label = { Text("Добавить комментарий") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onAddComment,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Отправить")
        }
    }
}

private fun addComment(
    text: String,
    albumId: Int,
    commentViewModel: CommentViewModel
) {
    if (text.isNotBlank()) {
        commentViewModel.insertComment(
            Comment(
                entityId = albumId,
                type = "album",
                text = text
            )
        )
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
