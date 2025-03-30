package com.example.diplomproject
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.diplomproject.ViewModel.AuthViewModel
import com.example.diplomproject.ui.theme.EmotionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            Spacer(modifier = Modifier.height(16.dp))
            Text("Emotional Security", fontSize = 24.sp, color = Color(0xFF2A3439))
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