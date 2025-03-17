package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavController
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.viewmodel.AlbumViewModel

// Экран списка альбомов с FloatingActionButton для создания нового альбома
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    navController: NavController,
    albumViewModel: AlbumViewModel = hiltViewModel()
) {
    val albums by albumViewModel.albums.observeAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Альбомы") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить альбом")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AlbumListContent(
                albums = albums,
                onAlbumClick = { album ->
                    // Переход к экрану деталей альбома с передачей параметров
                    navController.navigate("album_details/${album.id}/${album.name}")
                },
                onAlbumDelete = { album ->
                    albumViewModel.deleteAlbum(album)
                }
            )

            if (showDialog) {
                CreateAlbumDialog(
                    albumName = albumName,
                    onAlbumNameChange = { albumName = it },
                    onCreateAlbum = {
                        if (albumName.isNotBlank()) {
                            albumViewModel.insertAlbum(Album(name = albumName))
                            albumName = ""
                            showDialog = false
                        }
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

// Отображение списка альбомов
@Composable
fun AlbumListContent(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onAlbumDelete: (Album) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(albums) { album ->
            AlbumItem(
                album = album,
                onAlbumClick = onAlbumClick,
                onAlbumDelete = onAlbumDelete
            )
        }
    }
}

// Отображение отдельного элемента альбома с возможностью клика и удаления
@Composable
fun AlbumItem(
    album: Album,
    onAlbumClick: (Album) -> Unit,
    onAlbumDelete: (Album) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onAlbumClick(album) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = album.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Создан: ${album.createdAt}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onAlbumDelete(album) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Удалить альбом")
            }
        }
    }
}

// Диалог для создания нового альбома
@Composable
fun CreateAlbumDialog(
    albumName: String,
    onAlbumNameChange: (String) -> Unit,
    onCreateAlbum: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый альбом") },
        text = {
            OutlinedTextField(
                value = albumName,
                onValueChange = onAlbumNameChange,
                label = { Text("Название альбома") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onCreateAlbum) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
