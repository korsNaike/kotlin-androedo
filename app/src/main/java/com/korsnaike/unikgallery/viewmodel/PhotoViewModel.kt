package com.korsnaike.unikgallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    // Получение всех фотографий
    val allPhotos = photoRepository.getAllPhotos().asLiveData()

    fun getPhotosByAlbum(albumId: Int) = photoRepository.getPhotosByAlbum(albumId).asLiveData()

    fun getPhotoById(photoId: Int): LiveData<Photo?> = liveData {
        val photo = photoRepository.getPhoto(photoId)
        emit(photo)
    }


    fun insertPhoto(photo: Photo) {
        viewModelScope.launch {
            photoRepository.addPhoto(photo)
        }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            photoRepository.deletePhoto(photo)
        }
    }
}
