package com.korsnaike.unikgallery.ui

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.activity.LifecycleDemoActivity
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.viewmodel.AlbumViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
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
                title = { Text(stringResource(id = R.string.albums)) },
                actions = {
                    // Кнопка для запуска LifecycleDemoActivity
                    IconButton(onClick = {
                        context.startActivity(Intent(context, LifecycleDemoActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info, 
                            contentDescription = stringResource(id = R.string.show_demo)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add, 
                    contentDescription = stringResource(id = R.string.add_album)
                )
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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
                Text(
                    text = stringResource(id = R.string.created_at, formatMillisToReadableDate(album.createdAt)), 
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = { onAlbumDelete(album) }) {
                Icon(
                    imageVector = Icons.Default.Delete, 
                    contentDescription = stringResource(id = R.string.delete_album)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatMillisToReadableDate(millis: Long): String {
    // Преобразуем миллисекунды в LocalDateTime по часовому поясу
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("Europe/Moscow"))

    // Создаем форматтер для нужного формата
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    // Форматируем и возвращаем дату
    return dateTime.format(formatter)
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
        title = { Text(stringResource(id = R.string.new_album)) },
        text = {
            OutlinedTextField(
                value = albumName,
                onValueChange = onAlbumNameChange,
                label = { Text(stringResource(id = R.string.album_name)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onCreateAlbum) {
                Text(stringResource(id = R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
