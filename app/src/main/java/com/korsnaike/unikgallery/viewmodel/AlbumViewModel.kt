package com.korsnaike.unikgallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    // Преобразуем Flow в LiveData для удобства наблюдения из UI
    val albums = albumRepository.getAllAlbums().asLiveData()

    fun insertAlbum(album: Album) {
        viewModelScope.launch {
            albumRepository.insertAlbum(album)
        }
    }

    fun deleteAlbum(album: Album) {
        viewModelScope.launch {
            albumRepository.deleteAlbum(album)
        }
    }
    
    // Метод для синхронного получения всех альбомов
    suspend fun getAllAlbumsSync(): List<Album> {
        return albumRepository.getAllAlbumsSync()
    }
}
