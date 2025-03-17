package com.korsnaike.unikgallery.repository

import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.data.AlbumDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumDao: AlbumDao
) {
    fun getAllAlbums() = albumDao.getAllAlbums()

    suspend fun insertAlbum(album: Album): Long {
        return withContext(Dispatchers.IO) {
            albumDao.insertAlbum(album)
        }
    }

    suspend fun deleteAlbum(album: Album): Int {
        return withContext(Dispatchers.IO) {
            albumDao.deleteAlbum(album)
        }
    }
}
