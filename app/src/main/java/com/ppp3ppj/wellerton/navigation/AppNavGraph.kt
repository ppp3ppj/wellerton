package com.ppp3ppj.wellerton.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ppp3ppj.wellerton.presentation.healthlog.HealthLogFormScreen
import com.ppp3ppj.wellerton.presentation.healthlog.HealthLogScreen
import com.ppp3ppj.wellerton.presentation.home.HomeScreen
import com.ppp3ppj.wellerton.presentation.pincode.PinCodeScreen

object Routes {
    const val PIN = "pin"
    const val HOME = "home/{username}"
    fun home(username: String) = "home/$username"
    const val HEALTH_LOG = "health_log"
    const val HEALTH_LOG_ADD = "health_log_add/{date}"
    fun healthLogAdd(date: String) = "health_log_add/$date"
    const val HEALTH_LOG_EDIT = "health_log_edit/{logId}"
    fun healthLogEdit(logId: Int) = "health_log_edit/$logId"
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
                },
                onNavigateToHealthLog = {
                    navController.navigate(Routes.HEALTH_LOG)
                }
            )
        }
        composable(Routes.HEALTH_LOG) {
            HealthLogScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { date ->
                    navController.navigate(Routes.healthLogAdd(date))
                },
                onNavigateToEdit = { logId ->
                    navController.navigate(Routes.healthLogEdit(logId))
                }
            )
        }
        composable(
            route = Routes.HEALTH_LOG_ADD,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) {
            HealthLogFormScreen(
                isEditMode = false,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.HEALTH_LOG_EDIT,
            arguments = listOf(navArgument("logId") { type = NavType.IntType })
        ) {
            HealthLogFormScreen(
                isEditMode = true,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
