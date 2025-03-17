package com.korsnaike.unikgallery.repository

import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.data.AlbumDao
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumDao: AlbumDao
) {
    fun getAllAlbums() = albumDao.getAllAlbums()
    suspend fun addAlbum(album: Album) = albumDao.insertAlbum(album)
    suspend fun deleteAlbum(album: Album) = albumDao.deleteAlbum(album)
}
