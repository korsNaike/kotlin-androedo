package com.korsnaike.unikgallery.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Album::class, Photo::class, Comment::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun photoDao(): PhotoDao
    abstract fun commentDao(): CommentDao
}
