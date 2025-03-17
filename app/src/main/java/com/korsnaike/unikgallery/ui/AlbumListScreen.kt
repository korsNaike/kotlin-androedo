package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.viewmodel.AlbumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(albumViewModel: AlbumViewModel = hiltViewModel()) {
    val albums by albumViewModel.albums.observeAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Альбомы") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить альбом")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(albums) { album ->
                    AlbumItem(album = album)
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Новый альбом") },
                    text = {
                        OutlinedTextField(
                            value = albumName,
                            onValueChange = { albumName = it },
                            label = { Text("Название альбома") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (albumName.isNotBlank()) {
                                albumViewModel.insertAlbum(
                                    Album(name = albumName)
                                )
                                albumName = ""
                                showDialog = false
                            }
                        }) {
                            Text("Создать")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AlbumItem(album: Album) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = album.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Создан: ${album.createdAt}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

