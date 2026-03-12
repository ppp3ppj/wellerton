package com.ppp3ppj.wellerton.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ppp3ppj.wellerton.presentation.home.HomeScreen
import com.ppp3ppj.wellerton.presentation.pincode.PinCodeScreen

object Routes {
    const val PIN = "pin"
    const val HOME = "home"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.PIN
    ) {
        composable(Routes.PIN) {
            PinCodeScreen(
                onSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.PIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}
