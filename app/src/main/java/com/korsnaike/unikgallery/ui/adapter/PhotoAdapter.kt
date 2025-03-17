package com.korsnaike.unikgallery.ui.adapter

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.data.Album
import com.korsnaike.unikgallery.data.Photo
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Адаптер для отображения фотографий в списке
 */
class PhotoAdapter(
    context: Context,
    private val photos: List<PhotoWithAlbumInfo>
) : ArrayAdapter<PhotoAdapter.PhotoWithAlbumInfo>(context, R.layout.photo_list_item, photos) {

    /**
     * Класс для хранения информации о фотографии вместе с информацией об альбоме
     */
    data class PhotoWithAlbumInfo(
        val photo: Photo,
        val albumName: String?
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Используем ViewHolder для оптимизации
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            // Если view не существует, создаем новую
            view = LayoutInflater.from(context).inflate(R.layout.photo_list_item, parent, false)
            
            // Инициализируем ViewHolder
            viewHolder = ViewHolder()
            viewHolder.photoImageView = view.findViewById(R.id.photoImageView)
            viewHolder.photoIdTextView = view.findViewById(R.id.photoIdTextView)
            viewHolder.photoDateTextView = view.findViewById(R.id.photoDateTextView)
            viewHolder.photoAlbumTextView = view.findViewById(R.id.photoAlbumTextView)
            
            // Сохраняем ViewHolder в тег view
            view.tag = viewHolder
        } else {
            // Если view существует, используем ее и получаем ViewHolder из тега
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // Получаем элемент для текущей позиции
        val photoInfo = getItem(position)
        
        photoInfo?.let {
            // Заполняем данные
            viewHolder.photoIdTextView.text = context.getString(R.string.photo) + " #" + it.photo.id
            
            // Форматируем дату
            val date = Date(it.photo.createdAt)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            viewHolder.photoDateTextView.text = context.getString(R.string.photo_date, dateFormat.format(date))
            
            // Определяем, к какому альбому принадлежит фото
            val albumText = if (it.albumName != null) {
                context.getString(R.string.photo_album, it.albumName)
            } else {
                context.getString(R.string.no_album)
            }
            viewHolder.photoAlbumTextView.text = albumText
            
            // Загружаем изображение
            try {
                viewHolder.photoImageView.setImageURI(Uri.parse(it.photo.uri))
            } catch (e: Exception) {
                // В случае ошибки устанавливаем placeholder
                viewHolder.photoImageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
        
        return view
    }

    /**
     * ViewHolder для хранения ссылок на элементы интерфейса
     */
    private class ViewHolder {
        lateinit var photoImageView: ImageView
        lateinit var photoIdTextView: TextView
        lateinit var photoDateTextView: TextView
        lateinit var photoAlbumTextView: TextView
    }
} 