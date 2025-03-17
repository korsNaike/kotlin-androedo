// app/src/main/java/com/korsnaike/unikgallery/viewmodel/SettingsViewModel.kt
package com.korsnaike.unikgallery.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korsnaike.unikgallery.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageItem(val code: String, val nameResId: Int)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow(languageRepository.getSelectedLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()
    
    // Храним предыдущее значение языка для отслеживания изменений
    var previousLanguage: String? = null
        private set

    fun updateLanguage(languageCode: String) {
        previousLanguage = _selectedLanguage.value
        viewModelScope.launch {
            languageRepository.saveLanguage(languageCode)
            _selectedLanguage.value = languageCode
        }
    }
    
    // Метод для применения выбранного языка и перезапуска приложения
    fun applyLanguageAndRestart(activity: Activity) {
        languageRepository.applyLanguageAndRestartApp(activity)
    }
}