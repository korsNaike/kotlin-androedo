package com.korsnaike.unikgallery.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.korsnaike.unikgallery.R

// Перечисление пунктов меню
enum class BottomNavItem(val route: String, val icon: @Composable () -> Unit, val titleResId: Int) {
    Home("album_list", 
        { Icon(Icons.Default.Home, contentDescription = stringResource(id = R.string.nav_home)) }, 
        R.string.nav_home),
    Settings("settings", 
        { Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.nav_settings)) }, 
        R.string.nav_settings);
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = BottomNavItem.entries.toTypedArray()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute?.startsWith(item.route) == true
            
            NavigationBarItem(
                icon = { item.icon() },
                label = { Text(text = stringResource(id = item.titleResId)) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        // Очищаем весь стек навигации до начального экрана
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Избегаем создания дубликатов в стеке
                        launchSingleTop = true
                        // Восстанавливаем состояние при возврате
                        restoreState = true
                    }
                }
            )
        }
    }
} 