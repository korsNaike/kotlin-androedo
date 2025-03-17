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

// Перечисление пунктов меню
enum class BottomNavItem(val route: String, val icon: @Composable () -> Unit, val title: String) {
    Home("album_list", { Icon(Icons.Default.Home, contentDescription = "Домой") }, "Домой"),
    Settings("settings", { Icon(Icons.Default.Settings, contentDescription = "Настройки") }, "Настройки");
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
                label = { Text(text = item.title) },
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