package com.korsnaike.unikgallery.activity

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.ui.adapter.PhotoAdapter
import com.korsnaike.unikgallery.viewmodel.AlbumViewModel
import com.korsnaike.unikgallery.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Активность для отображения каталога всех фотографий в приложении
 */
@AndroidEntryPoint
class CatalogActivity : AppCompatActivity() {
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var photosListView: ListView
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)
        
        // Настраиваем toolbar
        supportActionBar?.apply {
            title = getString(R.string.catalog_title)
            setDisplayHomeAsUpEnabled(true)
        }

        // Инициализируем ViewModel
        photoViewModel = ViewModelProvider(this)[PhotoViewModel::class.java]
        albumViewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        
        // Находим элементы интерфейса
        photosListView = findViewById(R.id.photosListView)
        emptyTextView = findViewById(R.id.emptyTextView)
        
        // Настраиваем ListView
        setupListView()
        
        // Загружаем данные
        loadData()
    }
    
    private fun setupListView() {
        // Настраиваем обработчик кликов на элементах списка
        photosListView.setOnItemClickListener { _, _, position, _ ->
            val photoInfo = photosListView.adapter.getItem(position) as PhotoAdapter.PhotoWithAlbumInfo
            val photoId = photoInfo.photo.id
            
            // Показываем Toast с информацией о выбранном фото
            Toast.makeText(
                this,
                getString(R.string.photo_opened, photoId),
                Toast.LENGTH_SHORT
            ).show()
            
            // Здесь можно добавить переход к деталям фотографии при необходимости
            // Например, запустить новую активность:
            // val intent = Intent(this, PhotoDetailsActivity::class.java).apply {
            //    putExtra("photoId", photoId)
            // }
            // startActivity(intent)
        }
    }
    
    private fun loadData() {
        // Наблюдаем за данными из ViewModel
        photoViewModel.allPhotos.observe(this) { photos ->
            if (photos.isEmpty()) {
                // Если фотографий нет, показываем сообщение
                photosListView.visibility = android.view.View.GONE
                emptyTextView.visibility = android.view.View.VISIBLE
            } else {
                // Если фотографии есть, скрываем сообщение и показываем список
                photosListView.visibility = android.view.View.VISIBLE
                emptyTextView.visibility = android.view.View.GONE
                
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
                    val adapter = PhotoAdapter(this@CatalogActivity, photosList)
                    photosListView.adapter = adapter
                }
            }
        }
    }
    
    // Обработка нажатия кнопки "Назад" в toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 