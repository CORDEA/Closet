package jp.cordea.closet.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.cordea.closet.ui.add_item.AddItem
import jp.cordea.closet.ui.home.Home
import jp.cordea.closet.ui.type_select.TypeSelect

@Composable
fun ClosetApp() {
    AppNavHost(navController = rememberNavController())
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(navController)
        }
        composable("type-select") {
            TypeSelect(navController)
        }
        composable("add-item/{type}") {
            val type = requireNotNull(it.arguments?.getString("type"))
            AddItem()
        }
    }
}
