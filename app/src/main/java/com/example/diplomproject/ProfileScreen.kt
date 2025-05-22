package com.example.diplomproject
import android.inputmethodservice.Keyboard.Row
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
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
import com.example.diplomproject.data.UserProfile
import com.google.firebase.auth.FirebaseUser
import kotlin.math.roundToInt


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    onMenuClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user by authViewModel.user.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val backgroundColor = Color(0xFFffece0)

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    val isDataLoaded = user != null && userProfile != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        TopBar("Профиль", onMenuClick)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = isDataLoaded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { -it }
                ) {
                    ProfileCard(user, userProfile)
                }
            }

            item {
                Divider(
                    color = Color(0xFFed9a66),
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                AnimatedVisibility(
                    visible = isDataLoaded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { -it }
                ) {
                    StressLevelCard(userProfile)
                }
            }

            item {
                AnimatedVisibility(
                    visible = isDataLoaded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { -it }
                ) {
                    EmotionCard(userProfile)
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(user: FirebaseUser?, userProfile: UserProfile?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.card_profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x80000000)
                            ),
                            startY = 0.3f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.BottomStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFffece0).copy(alpha = 0.2f))
                        .border(2.dp, Color(0xFFffece0), CircleShape)
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

                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.displayName ?: "Гость",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFffece0),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userProfile?.email ?: user?.email ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFffece0).copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun StressLevelCard(userProfile: UserProfile?) {
    val stressLevel = userProfile?.stressLevel ?: 0f
    val stressPercent = (stressLevel * 10).roundToInt()

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF4E756E).copy(alpha = 0.9f), Color(0xFFfaae8e))
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .height(150.dp)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Ваш уровень стресса",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFffece0),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = stressLevel / 10f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = when {
                        stressLevel < 3 -> Color(0xFF4E9D7C)
                        stressLevel < 6 -> Color(0xFFC0B848)
                        else -> Color(0xFFBA4D4B)
                    },
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$stressPercent%",
                        fontSize = 16.sp,
                        color = Color(0xFFffece0)
                    )

                    Text(
                        text = "${userProfile?.countEntry ?: 0} обследований",
                        fontSize = 16.sp,
                        color = Color(0xFFffece0)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionCard(userProfile: UserProfile?) {
    val currentEmotion = userProfile?.currentEmotion ?: -1
    val emotionName = getEmotionName(currentEmotion)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFFfaae8e).copy(alpha = 0.9f),
                                0.6f to Color(0xFFfaae8e).copy(alpha = 0.9f),
                                1.0f to Color(0xFFed9a66)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Текущая эмоция:",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFffece0),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val (emotionPainter, emotionColor) = getEmotionImageAndColor(currentEmotion)

                Image(
                    painter = emotionPainter,
                    contentDescription = emotionName,
                    colorFilter = ColorFilter.tint(emotionColor),
                    modifier = Modifier.size(110.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = emotionName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFffece0).copy(alpha = 0.85f)
                )
            }
        }
    }
}







