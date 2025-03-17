package com.korsnaike.unikgallery

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.korsnaike.unikgallery.ui.AlbumDetailsScreen
import com.korsnaike.unikgallery.ui.AlbumListScreen
import com.korsnaike.unikgallery.ui.BottomNavBar
import com.korsnaike.unikgallery.ui.CatalogScreen
import com.korsnaike.unikgallery.ui.PhotoDetailsScreen
import com.korsnaike.unikgallery.ui.SettingsScreen
import com.korsnaike.unikgallery.ui.theme.UnikgalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnikgalleryTheme {
                AppScaffold()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = "album_list", 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("album_list") {
                AlbumListScreen(navController)
            }
            // Пример маршрута с параметрами albumId и albumName.
            composable(
                "album_details/{albumId}/{albumName}",
                arguments = listOf(
                    navArgument("albumId") { type = NavType.IntType },
                    navArgument("albumName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val albumId = backStackEntry.arguments?.getInt("albumId") ?: 0
                val albumName = backStackEntry.arguments?.getString("albumName") ?: "Без названия"
                AlbumDetailsScreen(albumId = albumId, albumName = albumName, navController = navController)
            }
            composable(
                route = "photo_details/{photoId}",
                arguments = listOf(
                    navArgument("photoId") { type = NavType.IntType },
                )
            ) { backStackEntry ->
                val photoId = backStackEntry.arguments?.getInt("photoId") ?: return@composable
                PhotoDetailsScreen(photoId = photoId, navController = navController)
            }
            
            // Добавляем экран каталога
            composable("catalog") {
                CatalogScreen(navController)
            }
            
            // Добавляем экран настроек
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    androidx.compose.material3.Text(text = "Главный экран приложения")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UnikgalleryTheme {
        HomeScreen()
    }
}
