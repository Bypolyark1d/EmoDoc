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
import com.example.diplomproject.ViewModel.EmotionResultViewModel
import com.example.diplomproject.ViewModel.ProfileViewModel
import com.example.diplomproject.ViewModel.StressSurveyViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Login : Screens("login")
    object Analize : Screens("analize")
    object Profile : Screens("profile")
    object Settings : Screens("settings")
    object EmotionAnalysis : Screens("emotion_analysis")
    object Survey : Screens("survey")
    object Statistic : Screens("statistic")
    object Result : Screens("stress_result")
    object EmotionResult : Screens("emotion_result")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val stressSurveyViewModel: StressSurveyViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val emotionResultViewModel: EmotionResultViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

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
            composable(Screens.EmotionAnalysis.route) { EmotionAnalysisScreen(navController,emotionResultViewModel) }
            composable(Screens.Survey.route) { SurveyScreen(navController,stressSurveyViewModel) }
            composable(Screens.Result.route){ResultScreen(navController,stressSurveyViewModel,profileViewModel ) }
            composable(Screens.Statistic.route) { StatisticScreen(navController) }
            composable(Screens.EmotionResult.route){ EmotionResultScreen (navController, emotionResultViewModel, profileViewModel)}
        }
    }
}