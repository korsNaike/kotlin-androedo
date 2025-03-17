package com.korsnaike.unikgallery.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.data.Photo
import com.korsnaike.unikgallery.ui.adapter.PhotoAdapter
import com.korsnaike.unikgallery.viewmodel.AlbumViewModel
import com.korsnaike.unikgallery.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Фрагмент для отображения каталога всех фотографий в приложении
 */
@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var photosListView: ListView
    private lateinit var emptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Инициализируем ViewModel
        photoViewModel = ViewModelProvider(requireActivity())[PhotoViewModel::class.java]
        albumViewModel = ViewModelProvider(requireActivity())[AlbumViewModel::class.java]
        
        // Находим элементы интерфейса
        photosListView = view.findViewById(R.id.photosListView)
        emptyTextView = view.findViewById(R.id.emptyTextView)
        
        // Настраиваем ListView
        setupListView()
        
        // Загружаем данные
        loadData()
    }
    
    private fun setupListView() {
        // Настраиваем обработчик кликов на элементах списка
        photosListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val photoInfo = photosListView.adapter.getItem(position) as PhotoAdapter.PhotoWithAlbumInfo
            val photoId = photoInfo.photo.id
            
            // Показываем Toast с информацией о выбранном фото
            Toast.makeText(
                requireContext(),
                getString(R.string.photo_opened, photoId),
                Toast.LENGTH_SHORT
            ).show()
            
            // Переходим на экран с деталями фотографии с использованием прямой навигации
            val bundle = bundleOf("photoId" to photoId)
            findNavController().navigate(R.id.action_catalog_to_photoDetails, bundle)
        }
    }
    
    private fun loadData() {
        // Наблюдаем за данными из ViewModel
        photoViewModel.allPhotos.observe(viewLifecycleOwner) { photos ->
            if (photos.isEmpty()) {
                // Если фотографий нет, показываем сообщение
                photosListView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                // Если фотографии есть, скрываем сообщение и показываем список
                photosListView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
                
                // Создаем список для адаптера, добавляя информацию об альбомах
                val photosList = mutableListOf<PhotoAdapter.PhotoWithAlbumInfo>()
                
                // Получаем все альбомы для сопоставления albumId
                CoroutineScope(Dispatchers.Main).launch {
                    val albums = withContext(Dispatchers.IO) {
                        albumViewModel.getAllAlbumsSync()
                    }
                    
                    // Создаем map для быстрого поиска названия альбома по id
                    val albumMap = albums.associateBy { it.id }
                    
                    // Создаем список элементов для адаптера
                    photos.forEach { photo ->
                        val albumName = photo.albumId?.let { albumId ->
                            albumMap[albumId]?.name
                        }
                        photosList.add(PhotoAdapter.PhotoWithAlbumInfo(photo, albumName))
                    }
                    
                    // Создаем и устанавливаем адаптер
                    val adapter = PhotoAdapter(requireContext(), photosList)
                    photosListView.adapter = adapter
                }
            }
        }
    }
} 