package com.korsnaike.unikgallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val coverUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
