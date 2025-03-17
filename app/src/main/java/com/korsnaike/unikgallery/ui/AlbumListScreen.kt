package com.korsnaike.unikgallery.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.viewmodel.AlbumViewModel
import com.korsnaike.unikgallery.activity.LifecycleDemoActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    navController: NavController,
    albumViewModel: AlbumViewModel = hiltViewModel()
) {
    val albums by albumViewModel.albums.observeAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Альбомы") },
                actions = {
                    // Кнопка для запуска LifecycleDemoActivity
                    IconButton(onClick = {
                        context.startActivity(Intent(context, LifecycleDemoActivity::class.java))
                    }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Показать Demo")
                    }
                }
            )
        },
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

@Composable
fun AlbumListScreenPreview() {
    val navController = rememberNavController()
    AlbumListScreen(navController = navController)
}
