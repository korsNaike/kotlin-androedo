package com.korsnaike.unikgallery.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.repository.LanguageRepository
import com.korsnaike.unikgallery.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Вспомогательная функция для безопасного показа снэкбара с использованием строковых ресурсов
@Composable
fun ShowSnackbar(
    snackbarHostState: SnackbarHostState,
    messageResId: Int,
    scope: CoroutineScope
) {
    // Получаем строковый ресурс здесь, в композабельном контексте
    val message = stringResource(id = messageResId)
    
    // Используем DisposableEffect для запуска корутины при входе в композицию
    // и отмены при выходе
    DisposableEffect(messageResId) {
        val job = scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
        
        // При выходе из композиции отменяем корутину, если она еще активна
        onDispose {
            job.cancel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    var showLanguageDropdown by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Получаем контекст активности для перезапуска
    val context = LocalContext.current
    
    // Состояние для отслеживания необходимости показать снэкбар
    var showSnackbar by remember { mutableStateOf(false) }
    
    // Состояние для отслеживания необходимости перезапуска
    var languageChanged by remember { mutableStateOf(false) }
    
    val languages = listOf(
        Pair(LanguageRepository.LANGUAGE_RU, R.string.language_russian),
        Pair(LanguageRepository.LANGUAGE_EN, R.string.language_english)
    )

    Scaffold(
        topBar = { 
            TopAppBar(title = { Text(text = stringResource(id = R.string.settings)) }) 
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.app_settings), 
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Выпадающий список для выбора языка
            Text(
                text = stringResource(id = R.string.language),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDropdown = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = languages.find { it.first == selectedLanguage }?.second 
                                ?: R.string.language_russian
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(id = R.string.select_language)
                    )
                }
            }

            DropdownMenu(
                expanded = showLanguageDropdown,
                onDismissRequest = { showLanguageDropdown = false },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                languages.forEach { (code, nameResId) ->
                    DropdownMenuItem(
                        text = { Text(stringResource(id = nameResId)) },
                        onClick = {
                            if (code != selectedLanguage) {
                                viewModel.updateLanguage(code)
                                // Устанавливаем флаг, что язык был изменен
                                languageChanged = true
                                showSnackbar = true
                            }
                            showLanguageDropdown = false
                        }
                    )
                }
            }
            
            // Если язык был изменен, показываем кнопку для применения изменений
            if (languageChanged) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        // При нажатии на кнопку перезапускаем активность
                        try {
                            // Пытаемся получить Activity из контекста
                            context.findActivity()?.let { activity ->
                                viewModel.applyLanguageAndRestart(activity)
                            }
                        } catch (e: Exception) {
                            // Обработка ошибок
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.apply_changes))
                }
            }
            
            // Если нужно показать снэкбар, вызываем нашу вспомогательную функцию и сбрасываем флаг
            if (showSnackbar) {
                ShowSnackbar(
                    snackbarHostState = snackbarHostState,
                    messageResId = R.string.language_changed,
                    scope = scope
                )
                showSnackbar = false
            }
        }
    }
}

// Расширение для Context, чтобы получить Activity
fun Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}