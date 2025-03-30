package com.example.diplomproject
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.diplomproject.ViewModel.AuthViewModel
import com.example.diplomproject.ui.theme.EmotionTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onMenuClick: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFffece0),
            titleContentColor = Color(0xFFffece0)
        ),
        title = { Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2A3439)) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    tint = Color(0xFF2A3439),
                    contentDescription = "Меню"
                )
            }
        }
    )
}

@Composable
fun DrawerContent(navController: NavHostController, authViewModel: AuthViewModel, onClose: () -> Unit) {
    val user by authViewModel.user.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .fillMaxHeight()
            .background(Color(0xFFed9a66))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        // Профиль пользователя в Drawer
        user?.let {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(it.displayName ?: "Без имени", color = Color(0xFF2A3439), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(it.email ?: "", color = Color.White, fontSize = 18.sp)
                }
            }
        } ?: Text("Неавторизованный пользователь", color = Color.White)

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color.White.copy(alpha = 0.4f), thickness = 1.dp)

        // Меню
        DrawerItem("Профиль", R.drawable.ic_profile) {
            navController.navigate(Screens.Profile.route)
            onClose()
        }
        DrawerItem("Анализ Эмоций", R.drawable.ic_analyse) {
            navController.navigate(Screens.Analize.route)
            onClose()
        }
        DrawerItem("Настройки", R.drawable.ic_settings) {
            navController.navigate(Screens.Settings.route)
            onClose()
        }
        DrawerItem("Выход", R.drawable.ic_exit) {
            authViewModel.signOut()
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            onClose()
        }
    }
}

@Composable
fun DrawerItem(title: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(){ onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = Color(0xFF2A3439),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = Color(0xFF2A3439))
    }
}


@Composable
fun SettingsScreen(onMenuClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFffece0))) {
        TopBar("Настройки", onMenuClick)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Настройки", fontSize = 24.sp)
        }
    }
}

// Превью
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    EmotionTheme {
        val navController = rememberNavController()
    }
}
