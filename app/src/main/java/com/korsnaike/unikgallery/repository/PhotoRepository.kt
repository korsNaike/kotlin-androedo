package com.korsnaike.unikgallery.repository

import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.data.PhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoDao: PhotoDao
) {
    // Получение всех фотографий
    fun getAllPhotos() = photoDao.getAllPhotos()
    
    fun getPhotosByAlbum(albumId: Int) = photoDao.getPhotosByAlbum(albumId)

    suspend fun addPhoto(photo: Photo): List<Long> {
        return withContext(Dispatchers.IO) {
            photoDao.insertPhoto(photo)
        }
    }

    suspend fun getPhoto(photoId: Int): Photo {
        return withContext(Dispatchers.IO) {
            photoDao.getPhoto(photoId)
        }
    }

    suspend fun deletePhoto(photo: Photo): Int {
        return withContext(Dispatchers.IO) {
            photoDao.deletePhoto(photo)
        }
    }
}
