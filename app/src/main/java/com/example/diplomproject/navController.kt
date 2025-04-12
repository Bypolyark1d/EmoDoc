package com.example.diplomproject

import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diplomproject.ViewModel.AuthViewModel
import kotlinx.coroutines.launch

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Login : Screens("login")
    object Analize : Screens("analize")
    object Profile : Screens("profile")
    object Settings : Screens("settings")
    object EmotionAnalysis : Screens("emotion_analysis")
    object Survey : Screens("survey")
    object Statistic : Screens("statistic")
    object Result : Screens("result/{stressLevel}") {
        fun createRoute(stressLevel: Float) =
            "result/$stressLevel"
    }
}

fun NavHostController.navigateSafe(route: String) {
    try {
        navigate(route)
    } catch (e: Exception) {
        Log.e("NAVIGATION", "Ошибка перехода: ${e.message}")
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = viewModel()

    val onMenuClick: () -> Unit = {
        scope.launch {
            if (drawerState.isClosed) drawerState.open() else drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(navController, authViewModel) {
                scope.launch { drawerState.close() }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.Splash.route
        ) {
            composable(Screens.Splash.route) { SplashScreen(navController, authViewModel) }
            composable(Screens.Login.route) { LoginScreen(navController, authViewModel) }
            composable(Screens.Profile.route) { ProfileScreen(onMenuClick) }
            composable(Screens.Analize.route) { AnalizeScreen(navController, onMenuClick) }
            composable(Screens.Settings.route) { SettingsScreen(onMenuClick) }
            composable(Screens.EmotionAnalysis.route) { EmotionAnalysisScreen(navController) }
            composable(Screens.Survey.route) { SurveyScreen(navController) }
            composable(
                route = Screens.Result.route,
                arguments = listOf(
                    navArgument("stressLevel") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val stressLevel = backStackEntry.arguments?.getFloat("stressLevel") ?: 0f
                ResultScreen(navController, stressLevel)
            }
            composable(Screens.Statistic.route) { StatisticScreen(navController) }
        }
    }
}