package com.korsnaike.unikgallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment")
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entityId: Int,   // ID фото или альбома, к которому привязан комментарий
    val type: String,    // Тип: "photo" или "album"
    val text: String,    // Текст комментария
    val createdAt: Long = System.currentTimeMillis() // Дата добавления комментария
)
