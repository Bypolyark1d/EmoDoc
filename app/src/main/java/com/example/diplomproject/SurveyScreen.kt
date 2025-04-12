package com.example.diplomproject

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SurveyScreen(navController: NavController) {
    var testStarted by remember { mutableStateOf(false) }
    var stressLvl by remember { mutableStateOf<Float>(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (testStarted) {
                StressTestScreen(
                    onFinish = { stressLevel ->
                        testStarted = false
                        stressLvl = stressLevel
                        navController.navigate(Screens.Result.createRoute(stressLvl))
                    }
                )
            } else {
                Spacer(modifier = Modifier.height(30.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 16.dp)
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.card_profile),
                            contentDescription = "Анализ настроения",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Column {
                                Text(
                                    text = "Описание теста",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Этот тест поможет вам определить уровень стресса. Ответьте на несколько вопросов, чтобы мы могли оценить вашу ситуацию.",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                SurveyCard(
                    title = "Тест на уровень стресса",
                    iconRes = R.drawable.ic_analyse
                ) {
                    testStarted = true
                }
                SurveyCard(
                    title = "Посмотреть статистику",
                    iconRes = R.drawable.ic_stat
                ) {
                    navController.navigate("statistic")
                }
            }
        }
    }
}


@Composable
fun SurveyCard(title: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick() }
            .shadow(10.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StressTestScreen(onFinish: (Float) -> Unit) {
    var questionIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val questions = remember { getRandomQuestions(context) }
    val textColor = Color(0xFF2A3439)
    val primaryColor = Color(0xFFed9a66)
    val scores = listOf(0, 1, 2, 3, 4)

    var userScores by remember { mutableStateOf(List(questions.size) { 0 }) }
    var stressLevel by remember { mutableStateOf(0f) }
    var totalScore by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Вопрос ${questionIndex + 1} / ${questions.size}",
                fontSize = 18.sp,
                color = primaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LinearProgressIndicator(
                progress = (questionIndex + 1).toFloat() / questions.size.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = primaryColor,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(15.dp))

            AnimatedContent(
                targetState = questionIndex,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "QuestionAnimation"
            ) { targetIndex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = primaryColor),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = questions[targetIndex].first,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            questions[questionIndex].second.forEachIndexed { index, answer ->
                var isClicked by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (isClicked) 0.9f else 1f,
                    animationSpec = tween(durationMillis = 150),
                    label = "ClickAnimation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(65.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clickable {
                            isClicked = true
                            val updatedScores = userScores.toMutableList()
                            updatedScores[questionIndex] = scores[index]
                            userScores = updatedScores
                            totalScore += scores[index]

                            coroutineScope.launch {
                                delay(150)
                                isClicked = false
                                Log.d("StressTest", "Answer selected: ${scores[index]} for question ${questionIndex + 1}")
                                if (questionIndex < questions.size - 1) {
                                    questionIndex++
                                } else {
                                    stressLevel = (totalScore.toFloat() / (questions.size * 4)) * 10f
                                    onFinish(stressLevel)
                                    totalScore = 0
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = answer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }

            if (questionIndex == questions.size) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Уровень стресса: ${stressLevel.toInt()} / 10",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

fun getRandomQuestions(context: Context): List<Pair<String, List<String>>> {
    val questionsArray = context.resources.getStringArray(R.array.questions)
    val answersArray = context.resources.getStringArray(R.array.answers)
    val answers = answersArray.toList()
    val allQuestions = questionsArray.map { question ->
        question to answers
    }
    return allQuestions.shuffled().take(20)
}









