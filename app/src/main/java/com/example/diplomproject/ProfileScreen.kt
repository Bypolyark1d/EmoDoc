package com.example.diplomproject
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.diplomproject.ViewModel.AuthViewModel


@Composable
fun ProfileScreen(onMenuClick: () -> Unit, viewModel: AuthViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()

    val backgroundColor = Color(0xFFffece0)

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        TopBar("Профиль", onMenuClick)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Spacer(modifier = Modifier.height(50.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    user?.photoUrl?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF2A3439), CircleShape)
                                .padding(2.dp)
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.ic_default_avatar),
                        contentDescription = "Default Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .padding(2.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    user?.let {
                        Text(
                            text = "${it.displayName}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A3439)
                        )
                    } ?: Text(
                        "Ошибка загрузки пользователя",
                        fontSize = 16.sp,
                        color = Color.Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    user?.let {
                        Text(
                            text = "Email: ${it.email}",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    user?.let {
                        Text(
                            text = "Дата регистрации: Не указана",
                            fontSize = 16.sp,
                            color = Color(0xFF2A3439)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    user?.let {
                        Text(
                            text = "Количество дней обследований: Не указано",
                            fontSize = 16.sp,
                            color = Color(0xFF2A3439)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    user?.let {
                        Text(
                            text = "Текущая эмоция: Не указана",
                            fontSize = 16.sp,
                            color = Color(0xFF2A3439)
                        )
                    }
                }
            }
        }
    }
}





