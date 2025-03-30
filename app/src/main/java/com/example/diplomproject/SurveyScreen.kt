package com.example.diplomproject

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
    var testResult by remember { mutableStateOf<String?>(null) }
    var stressImageRes by remember { mutableStateOf<Int?>(null) }

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
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                    onFinish = { result ->
                        testStarted = false
                        testResult = result

                        stressImageRes = when (result) {
                            "Очень низкий уровень стресса" -> R.drawable.emoji
                            "Низкий уровень стресса" -> R.drawable.emoji
                            "Средний уровень стресса" -> R.drawable.emoji
                            "Высокий уровень стресса" -> R.drawable.emoji
                            "Очень высокий уровень стресса" -> R.drawable.emoji
                            else -> null
                        }
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFed9a66)),
                    shape = RectangleShape
                ) {
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
                                color = Color.White
                            )
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
                    navController.navigate("statisticScreen")
                }
            }
        }
    }

    testResult?.let { result ->
        ResultDialog(resultText = result, stressImageRes = stressImageRes) {
            testResult = null
            stressImageRes = null
        }
    }
}

@Composable
fun ResultDialog(resultText: String, stressImageRes: Int?, onDismiss: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(stressImageRes) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)) + expandVertically(),
        exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(),
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFFed9a66),
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Результат теста",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    stressImageRes?.let { imageRes ->
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Stress level image",
                            modifier = Modifier
                                .size(150.dp)
                                .padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = resultText,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "ОК",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFed9a66)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun SurveyCard(title: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick() }
            .shadow(10.dp)
            .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A3439))
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

@Preview(showBackground = true)
@Composable
fun ResultDialogPreview() {
    ResultDialog(resultText = "Ваш уровень стресса: Средний",R.drawable.emoji) {
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StressTestScreen(onFinish: (String) -> Unit) {
    var questionIndex by remember { mutableStateOf(0) }
    val questions = getStressTestQuestions()
    val textColor = Color(0xFF2A3439)
    val primaryColor = Color(0xFFed9a66)
    val scores = listOf(0, 1, 2, 3, 4)

    var userScores by remember { mutableStateOf(List(questions.size) { 0 }) }
    var resultText by remember { mutableStateOf("") }

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
            Spacer(modifier = Modifier.height(25.dp))

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
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

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
                        .height(70.dp)
                        .border(2.dp, textColor, RoundedCornerShape(12.dp))
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clickable {
                            isClicked = true
                            userScores = userScores.toMutableList().also { it[questionIndex] = scores[index] }

                            coroutineScope.launch {
                                delay(150)
                                isClicked = false

                                if (questionIndex < questions.size - 1) {
                                    questionIndex++
                                } else {
                                    val totalScore = userScores.sum()
                                    resultText = when {
                                        totalScore <= 15 -> "Очень низкий уровень стресса"
                                        totalScore <= 30 -> "Низкий уровень стресса"
                                        totalScore <= 45 -> "Средний уровень стресса"
                                        totalScore <= 60 -> "Высокий уровень стресса"
                                        else -> "Очень высокий уровень стресса"
                                    }
                                    onFinish(resultText)
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = answer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}


fun getStressTestQuestions(): List<Pair<String, List<String>>> {
    return listOf(
        "Как часто за последний месяц вы чувствовали себя расстроенным из-за чего-то неожиданного?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы ощущали, что не можете справиться с важными делами?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали нервозность и стресс?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали уверенность в своей способности контролировать важные вещи?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы ощущали, что все идет не так, как вам хотелось бы?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что не можете контролировать важные события в своей жизни?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что вас раздражает что-то, что не поддается вашему контролю?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что вам не хватает энергии для выполнения повседневных задач?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что вам трудно справляться с возникающими проблемами?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы испытывали чувство тревоги?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы ощущали, что не успеваете сделать все, что запланировали?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что вас беспокоит будущее?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы испытывали проблемы со сном из-за переживаний?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что вас не понимают окружающие?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы испытывали трудности с концентрацией?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы ощущали нехватку поддержки со стороны окружающих?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что ваша жизнь выходит из-под контроля?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы ощущали физическую усталость без видимой причины?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц у вас были проблемы с аппетитом (переедание или потеря аппетита)?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали, что вам сложно расслабиться?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы испытывали раздражительность без видимой причины?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы замечали у себя перепады настроения?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы чувствовали нехватку мотивации?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто"),
        "Как часто за последний месяц вы испытывали трудности в общении с людьми?" to listOf("Никогда", "Редко", "Иногда", "Часто", "Очень часто")
    )
}





