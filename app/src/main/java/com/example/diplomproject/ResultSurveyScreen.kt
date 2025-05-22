package com.example.diplomproject

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.diplomproject.ViewModel.EntryViewModel
import com.example.diplomproject.ViewModel.ProfileViewModel
import com.example.diplomproject.ViewModel.StressSurveyViewModel
import com.example.diplomproject.data.StressSurveyResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CircularStressIndicator(stressLevel: Float) {
    val safeStressLevel = stressLevel.coerceIn(0f, 10f)
    val progress by animateFloatAsState(
        targetValue = safeStressLevel / 10f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium
        )
    )

    val (mainColor, trackColor, textColor) = when {
        safeStressLevel < 3 -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF76D7B2), Color(0xFF4E9D7C))),
            Color(0xFFB9F6CA).copy(alpha = 0.3f),
            Color(0xFF2A3439)
        )
        safeStressLevel < 6 -> Triple(
            Brush.verticalGradient(listOf(Color(0xFFDCD36A), Color(0xFFC0B848))),
            Color(0xFFFFF59D).copy(alpha = 0.3f),
            Color(0xFF2A3439)
        )
        else -> Triple(
            Brush.verticalGradient(listOf(Color(0xFFE66761), Color(0xFFBA4D4B))),
            Color(0xFFFF8A80).copy(alpha = 0.3f),
            Color(0xFF2A3439)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        if (safeStressLevel > 5) {
            InfinitePulseAnimation(color = mainColor)
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = mainColor,
                radius = size.minDimension / 2.2f,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        CircularProgressIndicator(
            progress = 1f,
            strokeWidth = 14.dp,
            color = trackColor,
            modifier = Modifier.size(180.dp)
        )

        Canvas(modifier = Modifier.size(180.dp)) {
            drawArc(
                brush = mainColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        AnimatedContent(
            targetState = (safeStressLevel * 10).toInt(),
            transitionSpec = {
                fadeIn() + slideInVertically { it } with fadeOut() + slideOutVertically { -it }
            }
        ) { percent ->
            Text(
                text = "$percent%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun InfinitePulseAnimation(color: Brush) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = color,
            radius = size.minDimension / 2 * pulse,
            alpha = 0.1f,
            style = Fill
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    var isUIVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isUIVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = isUIVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 1000)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 1000)
            )
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
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
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66).copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, Color(0xFFed9a66))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stressDescription,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2A3439),
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
                                    color = Color(0xFFffece0),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                recommendations.forEach {
                                    Text(
                                        text = "• $it",
                                        fontSize = 18.sp,
                                        color = Color(0xFFffece0)
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.weight(0.5f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                        .shadow(
                            elevation = elevation,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color(0xFF4E756E).copy(alpha = 0.4f),
                            ambientColor = Color(0xFF4E756E).copy(alpha = 0.2f)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                navController.popBackStack()
                                entryViewModel.saveStressSurveyResult(stressLevel)
                                profileViewModel.incrementEntryCount()
                                profileViewModel.updateStressLevel(stressLevel)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFed9a66), Color(0xFF4E756E)),
                                    startX = 0f,
                                    endX = 1000f
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .graphicsLayer {
                                scaleX = if (isPressed) 0.98f else 1f
                                scaleY = if (isPressed) 0.98f else 1f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ок",
                            color = Color(0xFFffece0),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}
class FakeStressSurveyViewModel : StressSurveyViewModel() {
    private val _stressResult = MutableStateFlow(
        StressSurveyResult(
            userId = "fakeUser",
            stressLevel = 5f
        )
    )
    override val stressResult: StateFlow<StressSurveyResult?> = _stressResult
}

@Preview(showBackground = true)
@Composable
fun PreviewResultScreen() {
    val navController = rememberNavController()
    val fakeStressViewModel = remember { FakeStressSurveyViewModel() }
    val fakeProfileViewModel = remember { ProfileViewModel() }

    ResultScreen(
        navController = navController,
        viewModel = fakeStressViewModel,
        profileViewModel = fakeProfileViewModel
    )
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
        stressLevel <= 2 -> "У вас низкий уровень стресса. Продолжайте следить за своим эмоциональным состоянием."
        stressLevel <= 5 -> "Ваш уровень стресса средний. Постарайтесь находить время для отдыха и расслабления."
        stressLevel <= 7 -> "У вас высокий уровень стресса. Обратите внимание на своё самочувствие и попробуйте методы релаксации."
        else -> "Ваш уровень стресса очень высокий. Рекомендуется обратиться к специалисту для профессиональной помощи."
    }
}