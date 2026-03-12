package com.ppp3ppj.wellerton.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ppp3ppj.wellerton.presentation.home.HomeScreen
import com.ppp3ppj.wellerton.presentation.pincode.PinCodeScreen

object Routes {
    const val PIN = "pin"
    const val HOME = "home/{username}"
    fun home(username: String) = "home/$username"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.PIN
    ) {
        composable(Routes.PIN) {
            PinCodeScreen(
                onSuccess = { username ->
                    navController.navigate(Routes.home(username)) {
                        popUpTo(Routes.PIN) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.HOME,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            HomeScreen(
                username = username,
                onLogout = {
                    navController.navigate(Routes.PIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
