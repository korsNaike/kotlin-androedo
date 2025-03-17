package com.korsnaike.unikgallery.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "language_preferences"
        private const val KEY_LANGUAGE = "selected_language"
        const val LANGUAGE_RU = "ru"
        const val LANGUAGE_EN = "en"
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getSelectedLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, getSystemLanguage()) ?: LANGUAGE_RU
    }
    
    fun saveLanguage(languageCode: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }
    
    fun getSystemLanguage(): String {
        val locale = Resources.getSystem().configuration.locales.get(0)
        return when (locale.language) {
            "ru" -> LANGUAGE_RU
            else -> LANGUAGE_EN
        }
    }
    
    fun setAppLanguage(languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
    
    // Метод для применения языка и перезапуска активности
    fun applyLanguageAndRestartApp(activity: Activity) {
        val languageCode = getSelectedLanguage()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(activity.resources.configuration)
        config.setLocale(locale)
        
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
        
        // Перезапуск активности
        val intent = activity.intent
        activity.finish()
        activity.startActivity(intent)
        // Плавная анимация перезапуска
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}