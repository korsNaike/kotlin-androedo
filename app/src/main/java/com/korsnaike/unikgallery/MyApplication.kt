// app/src/main/java/com/korsnaike/unikgallery/MyApplication.kt
package com.korsnaike.unikgallery

import android.app.Application
import android.content.Context
import com.korsnaike.unikgallery.repository.LanguageRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var languageRepository: LanguageRepository

    override fun attachBaseContext(base: Context) {
        // Создаем прокси-репозиторий, так как @Inject еще не работает здесь
        val tempRepo = LanguageRepository(base)
        val language = tempRepo.getSelectedLanguage()
        super.attachBaseContext(tempRepo.setAppLanguage(language))
    }

    override fun onCreate() {
        super.onCreate()
        // Дополнительная инициализация при необходимости
    }
}