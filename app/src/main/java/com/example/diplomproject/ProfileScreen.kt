package com.example.diplomproject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.diplomproject.TextPreProccesor.getEmotionImageAndColor
import com.example.diplomproject.TextPreProccesor.getEmotionName
import com.example.diplomproject.ViewModel.AuthViewModel
import com.example.diplomproject.ViewModel.ProfileViewModel


@Composable
fun ProfileScreen(onMenuClick: () -> Unit, authViewModel: AuthViewModel = viewModel(), profileViewModel: ProfileViewModel = viewModel()) {
    val user by authViewModel.user.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }
    val backgroundColor = Color(0xFFffece0)

    val isProfileCardVisible = remember { mutableStateOf(false) }
    val isStressCardVisible = remember { mutableStateOf(false) }
    val isEmotionCardVisible = remember { mutableStateOf(false) }


    LaunchedEffect(user, userProfile) {
        isProfileCardVisible.value = true
        isStressCardVisible.value = true
        isEmotionCardVisible.value = true
    }

    val currentEmotion = userProfile?.currentEmotion ?: -1
    val emotionName = getEmotionName(currentEmotion)

    Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        TopBar("Профиль", onMenuClick)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Spacer(modifier = Modifier.height(15.dp))

            AnimatedVisibility(
                visible = isProfileCardVisible.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.card_profile),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )

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
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            userProfile?.let {
                                Text(
                                    text = "Email: ${it.email}",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            Divider(
                color = Color(0xFFed9a66),
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            AnimatedVisibility(
                visible = isStressCardVisible.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        userProfile?.let {
                            Text(
                                text = "Шкала стресса: ${it.stressLevel} ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        userProfile?.let {
                            Text(
                                text = "Число обследований: ${it.countEntry}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = isEmotionCardVisible.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Текущая эмоция:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val (emotionPainter, emotionColor) = getEmotionImageAndColor(currentEmotion)

                        Image(
                            painter = emotionPainter,
                            contentDescription = emotionName,
                            colorFilter = ColorFilter.tint(emotionColor),
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        }
    }
}







