package com.korsnaike.unikgallery.di

import android.content.Context
import androidx.room.Room
import com.korsnaike.unikgallery.data.AppDatabase
import com.korsnaike.unikgallery.data.AlbumDao
import com.korsnaike.unikgallery.data.PhotoDao
import com.korsnaike.unikgallery.data.CommentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "album_db"
        )
            .fallbackToDestructiveMigration() // Для простоты разработки
            .build()
    }

    @Provides
    fun provideAlbumDao(db: AppDatabase): AlbumDao = db.albumDao()

    @Provides
    fun providePhotoDao(db: AppDatabase): PhotoDao = db.photoDao()

    @Provides
    fun provideCommentDao(db: AppDatabase): CommentDao = db.commentDao()
}
