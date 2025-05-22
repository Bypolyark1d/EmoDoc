package com.example.diplomproject
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.diplomproject.ViewModel.AuthViewModel
import com.example.diplomproject.ui.theme.EmotionTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.Transparent.toArgb()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val workManager = WorkManager.getInstance(this)
        workManager.getWorkInfosByTagLiveData("reminder_123").observe(this) { workInfos ->
            Log.d("WorkManagerDebug", "Work status: ${workInfos?.firstOrNull()?.state}")
        }
        window.navigationBarColor = Color.Transparent.toArgb()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        setContent {
            AppNavigation()
        }
    }
}
@Composable
fun SplashScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val isUserAuthenticated by viewModel.isUserAuthenticated.collectAsState()
    LaunchedEffect(isUserAuthenticated) {
        delay(2000)
        when (isUserAuthenticated) {
            true -> navController.navigate("profile") { popUpTo("splash") { inclusive = true } }
            false -> navController.navigate("login") { popUpTo("splash") { inclusive = true } }
            null -> {}
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFffece0)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(225.dp)
            )
            Spacer(modifier = Modifier.height(70.dp))
            Text("Emotion Doc", fontSize = 30.sp, fontWeight =  FontWeight.Bold, color = Color(0xFF2A3439))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    EmotionTheme {
        SplashScreen(navController = rememberNavController())
    }
}