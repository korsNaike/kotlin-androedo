package com.korsnaike.unikgallery.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.korsnaike.unikgallery.R
import com.korsnaike.unikgallery.activity.CatalogActivity

/**
 * Экран каталога, который запускает CatalogActivity для просмотра фотографий
 */
@Composable
fun CatalogScreen(navController: NavController) {
    val context = LocalContext.current
    
    // Автоматически запускаем активность каталога при переходе на этот экран
    LaunchedEffect(key1 = true) {
        val intent = Intent(context, CatalogActivity::class.java)
        context.startActivity(intent)
    }
    
    // Отображаем заглушку пока активность не запустится
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.catalog_title),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                
                Button(
                    onClick = {
                        val intent = Intent(context, CatalogActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = stringResource(id = R.string.catalog))
                }
            }
        }
    }
} 