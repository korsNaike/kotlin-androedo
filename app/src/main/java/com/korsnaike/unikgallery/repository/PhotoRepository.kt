package com.korsnaike.unikgallery.repository

import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.data.PhotoDao
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoDao: PhotoDao
) {
    fun getPhotosByAlbum(albumId: Int) = photoDao.getPhotosByAlbum(albumId)
    suspend fun addPhoto(photo: Photo) = photoDao.insertPhoto(photo)
    suspend fun deletePhoto(photo: Photo) = photoDao.deletePhoto(photo)
}
