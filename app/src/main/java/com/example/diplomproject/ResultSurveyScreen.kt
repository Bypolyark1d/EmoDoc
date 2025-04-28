package com.example.diplomproject

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.diplomproject.ViewModel.EmotionResultViewModel
import com.example.diplomproject.ViewModel.EntryViewModel
import com.example.diplomproject.ViewModel.ProfileViewModel
import com.example.diplomproject.ViewModel.StressSurveyViewModel

@Composable
fun CircularStressIndicator(stressLevel: Float) {
    val progress by animateFloatAsState(
        targetValue = stressLevel / 10f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    val color = when {
        stressLevel < 3 -> Color(0xFF76D7B2) // Зеленый
        stressLevel < 6 -> Color(0xFFF4D03F) // Желтый
        else -> Color(0xFFFF5733) // Красный
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawCircle(color.copy(alpha = 0.2f), radius = size.minDimension / 2)
        }

        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 12.dp,
            color = color,
            modifier = Modifier.size(130.dp)
        )

        Text(
            text = "${(stressLevel * 10).toInt()}%",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
@Composable
fun ResultScreen(navController: NavController, viewModel: StressSurveyViewModel = viewModel(), profileViewModel: ProfileViewModel = viewModel()) {
    val stressResult by viewModel.stressResult.collectAsState()
    val stressLevel = (stressResult?.stressLevel ?: 0.0).toFloat()
    val stressDescription = getStressDescription(stressLevel)
    val entryViewModel: EntryViewModel = viewModel()
    val context = LocalContext.current
    val recommendations = getRecommendations(context, stressLevel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(50.dp))
                Text(
                    text = "Результат теста",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A3439)
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularStressIndicator(stressLevel)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ваш уровень стресса: ${"%.1f".format(stressLevel)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A3439),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stressDescription,
                                fontSize = 16.sp,
                                color = Color(0xFF2A3439),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Рекомендации:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            recommendations.forEach {
                                Text(
                                    text = "• $it",
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.weight(0.5f))

            Button(
                onClick = { navController.popBackStack()
                    entryViewModel.saveStressSurveyResult(stressLevel)
                    profileViewModel.incrementEntryCount()
                    profileViewModel.updateStressLevel(stressLevel)},
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFed9a66))
            ) {
                Text("Ок", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewResultScreen() {
    val navController = rememberNavController()
    //ResultScreen(navController, stressLevel = 5.0f)
}
fun getRecommendations(context: Context, stressLevel: Float): List<String> {
    val recommendations = when {
        stressLevel < 3 -> context.resources.getStringArray(R.array.low_stress_recommendations).toList()
        stressLevel in 3.0..6.0 -> context.resources.getStringArray(R.array.medium_stress_recommendations).toList()
        stressLevel > 6 -> context.resources.getStringArray(R.array.high_stress_recommendations).toList()
        else -> listOf(context.getString(R.string.general_recommendation))
    }
    return recommendations.shuffled().take(3)
}
fun getStressDescription(stressLevel: Float): String {
    return when {
        stressLevel <= 2 -> "У вас низкий уровень стресса. Продолжайте следить за своим эмоциональным состоянием!"
        stressLevel <= 5 -> "Ваш уровень стресса средний. Постарайтесь находить время для отдыха и расслабления."
        stressLevel <= 7 -> "У вас высокий уровень стресса. Обратите внимание на своё самочувствие и попробуйте методы релаксации."
        else -> "Ваш уровень стресса очень высокий. Рекомендуется обратиться к специалисту для профессиональной помощи."
    }
}