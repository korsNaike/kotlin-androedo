package com.korsnaike.unikgallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    fun getPhotosByAlbum(albumId: Int) = photoRepository.getPhotosByAlbum(albumId).asLiveData()

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
