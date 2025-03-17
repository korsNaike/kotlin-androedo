package com.korsnaike.unikgallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumId: Int?, // ID альбома, если фото принадлежит альбому; иначе null
    val uri: String,  // URI изображения
    val createdAt: Long = System.currentTimeMillis() // Дата добавления фото
)
