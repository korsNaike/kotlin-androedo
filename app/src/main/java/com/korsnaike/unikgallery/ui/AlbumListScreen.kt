package com.korsnaike.unikgallery.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    // Получаем список альбомов из ViewModel
    val albums by albumViewModel.albums.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Альбомы") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(albums) { album ->
                AlbumItem(album = album)
            }
        }
    }
}

@Composable
fun AlbumItem(album: Album) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = album.name, style = MaterialTheme.typography.titleMedium)
            // Можно добавить отображение даты создания или другую информацию
        }
    }
}
